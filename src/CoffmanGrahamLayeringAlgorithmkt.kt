import java.util.*

class CoffmanGrahamLayeringAlgorithm(val layerSize: Int) {
    private val positions: MutableMap<String, Position> = mutableMapOf()
    private var markersCounter: Int = 0

    fun place(nodes: Collection<Node>): Map<Node, Position> {
        val mappedNodes = nodes.associateBy { it.id }
        val adjacencyMatrix = nodes.toAdjacencyMatrix()
        val layers = placeInLayers(adjacencyMatrix).map { layer ->
            layer.mapNotNull { mappedNodes[it] }.toMutableList()
        }
        appendDummy(layers)
        layers.forEach {
            println(it.map(Node::id))
        }
//        walk(root)
//        val nodes = (listOf(root) + root.allChildren())
//            .associateWith { positions[it.id]!! }
//        return xAdjustment(nodes)

        return assignCoordinates(layers).associate { it.first to it.second }
    }

    private fun assignCoordinates(layers: List<List<Node>>): List<Pair<Node, Position>> {
        var yCounter = 0
        return layers.flatMap { layer ->
            yCounter++
            var xCounter = 1
            layer.map { it to Position(yCounter.toDouble(), (xCounter++).toDouble()) }
        }
    }

    private fun appendDummy(layers: List<MutableList<Node>>) {
        var dummyCounter = 1
        (0 until layers.size - 1).forEach { i ->
            val current = layers[i]
            val next = layers[i + 1]
            val dummyNodes = current.flatMap { node ->
                node.children
                    //.filterNot { it.children.isEmpty() }
                    //.also { println(it) }
                    .filterNot { it.id in next.map(Node::id) }
                    .also { println(it) }
                    .map {
                        Node("dummy_${dummyCounter++}", mutableListOf(it)).apply {
                            node.children.remove(it)
                            node.children.add(this)
                        }
                    }
            }
            next.addAll(dummyNodes)
        }
    }

    private fun placeInLayers(adj: AdjacencyMatrix): List<List<String>> {
        val sortedNodes = topologicalSort(adj).toList()
            .sortedByDescending { it.second }
            .map { it.first }
            .toMutableList()
        val layers = mutableListOf<MutableList<String>>(mutableListOf()).apply {
            // Добавляем вершины без детей в последний слой
            last().addAll(
                sortedNodes
                    .filter { adj.row(it).isEmpty() }
                    .let { it.subList(0, if (it.size > layerSize) layerSize else it.size) }
                    .also { sortedNodes.removeAll(it) }
                    .toMutableList()
            )
        }
        // Распределяем оставшиеся вершины по слоям
        sortedNodes.forEach {
            val currentLayer = when {
                layers.last().size == layerSize -> layers.addLayer()
                it.hasNeighborsOf(layers.last(), adj) -> layers.addLayer()
                else -> layers.last()
            }
            currentLayer.add(it)
        }
        return layers.reversed()
    }

    private fun <K, T> MutableMap<K, T>.removeAll(keys: List<K>) = keys.forEach { this.remove(it) }
    private fun <T> MutableList<MutableList<T>>.addLayer() = mutableListOf<T>().also { this.add(it) }
    private fun String.hasNeighborsOf(neighbors: List<String>, adj: AdjacencyMatrix) =
        neighbors.any { it in adj.row(this) } || adj.row(this).any { it in neighbors }

    private fun topologicalSort(adj: AdjacencyMatrix): Map<String, Int> {
        fun findNodeCoeffs(parents: List<String>, marked: Map<String, Int>) = parents.map {
            if (marked.containsKey(it)) marked[it]!! else Int.MAX_VALUE
        }

        var marksCounter = 0
        val nodes = adj.keys.toMutableList()
        val marked = mutableMapOf<String, Int>()
        do {
            val notMarked = nodes - marked.keys
            val toMark = notMarked
                .map { it to findNodeCoeffs(adj.col(it), marked) }
                .filterNot { it.second.contains(Int.MAX_VALUE) }
                .sortedWith(coeffsComparator)
                .map { it.first }
                .onEach { marked[it] = marksCounter++ }
        } while (notMarked.isNotEmpty() && toMark.isNotEmpty())
        return marked
    }

    private val coeffsComparator = Comparator<Pair<String, List<Int>>> { one, two ->
        when {
            one.second.isEmpty() -> -1
            two.second.isEmpty() -> 1
            else -> (one.second.sortedDescending() zip two.second.sortedDescending())
                .map { it.first - it.second }
                .firstOrNull { it != 0 } ?: 1
        }
    }

//    private fun xAdjustment(nodes: Map<Node, Position>): Map<Node, Position> {
//        val adjustment = nodes.values.minOf { it.x }
//        return if(adjustment < 1) nodes.mapValues { it.value.copy(x = it.value.x + abs(adjustment) + 1) } else nodes
//    }

}
