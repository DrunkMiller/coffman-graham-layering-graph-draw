import java.util.*
import kotlin.random.Random

class IntersectionOptimizer(val steps: Int, val shuffle: Int ) {

    fun optimize(layers: List<List<Node>>): List<List<Node>> {
        if (layers.size < 3) return layers
        var bestShuffle = prevPlacement(layers)
        var bestIntersectionsSize = calcIntersections(bestShuffle)
        repeat(steps) {
            val newShuffle = when(it % 2) {
                0 -> bestShuffle.directShuffle()
                1 -> bestShuffle.reversedShuffle()
                else -> bestShuffle
            }
            if (calcIntersections(newShuffle) < bestIntersectionsSize)
                bestShuffle = newShuffle
        }
        return bestShuffle
    }

    private fun prevPlacement(layers: List<List<Node>>) =
        listOf(layers.first()) + (0 until layers.size - 1).map { layerPrevPlacement(layers[it], layers[it + 1], ::averagePlacement) }

    private fun List<List<Node>>.directShuffle(): List<List<Node>> {
        val one = this.map { it }.toMutableList()
        val two = this.map { it }.toMutableList().apply { removeFirst() }
        val three = two.map { it }.toMutableList().apply { removeFirst() }
        val shuffled = one.zip(two).zip(three)
            .map { Triple(it.first.first, it.first.second, it.second) }
            .map { it.second.shuffleLayer(it.first, it.third) }
        return listOf(this.first()) + shuffled + listOf(this.last())
    }

    private fun List<List<Node>>.reversedShuffle(): List<List<Node>> {
        val one = this.map { it }.toMutableList().reversed()
        val two = this.map { it }.toMutableList().apply { removeFirst() }
        val three = two.map { it }.toMutableList().apply { removeFirst() }
        val shuffled = one.zip(two).zip(three)
            .map { Triple(it.first.first, it.first.second, it.second) }
            .map { it.second.shuffleLayer(it.first, it.third) }
        return listOf(this.first()) + shuffled + listOf(this.last()).reversed()
    }

    private fun List<Node>.shuffleLayer(prev: List<Node>, next: List<Node>): List<Node> {
        if(this.size < 2) return this
        val bestShuffle = this.map { it }.toMutableList()
        var bestIntersectionsSize = calcIntersections(prev, bestShuffle) + calcIntersections(bestShuffle, next)
        repeat(shuffle) {
            var pos1 = 0
            var pos2 = 0
            while (pos1 == pos2) {
                pos1 = Random.nextInt(bestShuffle.size)
                pos2 = Random.nextInt(bestShuffle.size)
            }
            bestShuffle.swap(pos1, pos2)
            val newIntersectionsSize = calcIntersections(prev, bestShuffle) + calcIntersections(bestShuffle, next)
            if (newIntersectionsSize < bestIntersectionsSize)
                bestIntersectionsSize = newIntersectionsSize
            else
                bestShuffle.swap(pos2, pos1)
        }
        return bestShuffle
    }

    private fun layerPrevPlacement(
        firstLayer: List<Node>,
        secondLayer: List<Node>,
        finalPlace: (List<Int>) -> Int
    ): List<Node> {
        val indexedOne = firstLayer.mapIndexed { index, node -> node to index }
        val newTwoPlace = secondLayer.map { node ->
            node to indexedOne
                .filter { node in it.first.children }
                .map { it.second }
                .ifEmpty { listOf(Int.MAX_VALUE) }
                .let(finalPlace)
        }
        return newTwoPlace.sortedBy { it.second }.map { it.first }
    }

    private fun averagePlacement(values: List<Int>) = values.average().toInt()
    private fun medianPlacement(values: List<Int>) = values.sorted().let { it[it.size / 2] }

    fun calcIntersections(layers: List<List<Node>>) =
        layers.zipWithNext().sumBy { calcIntersections(it.first, it.second) }

    fun calcIntersections(firstLayer: List<Node>, secondLayer: List<Node>): Int {
        val indexedFirstLayer = firstLayer.mapIndexed { index, node -> node to index }
        val indexedSecondLayer = secondLayer.mapIndexed { index, node -> node to index }
        val edges = ArrayDeque(indexedFirstLayer.flatMap { (parent, pos) ->
            parent.children.mapNotNull { child ->
                indexedSecondLayer.find { it.first == child }?.let { pos to it.second }
            }
        })
        var countIntersections = 0
        while (edges.isNotEmpty()) {
            countIntersections += with(edges.pop()) {
                edges.sumBy {
                    when {
                        it.first > this.first && it.second < this.second -> 1
                        it.first < this.first && it.second > this.second -> 1
                        else -> 0
                    }
                }
            }
        }
        return countIntersections
    }


    private fun MutableList<Node>.swap(one: Int, two: Int) {
        this[one] = this[two].also { this[two] = this[one] }
    }
}
