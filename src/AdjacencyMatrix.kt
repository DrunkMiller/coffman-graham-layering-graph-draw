
typealias AdjacencyMatrix = Map<String, Map<String, Boolean>>

fun Collection<Node>.toAdjacencyMatrix(): AdjacencyMatrix {
    fun lineWithNode(node: Node) = this
        .associate { it.id to false }
        .toMutableMap()
        .also { line -> node.children.forEach { line[it.id] = true } }

    return this.associate { it.id to lineWithNode(it) }
}

fun AdjacencyMatrix.row(id: String): List<String> {
    val row = mutableListOf<String>()
    this[id]?.filter { it.value }?.mapTo(row) { it.key }
    return row
}

fun AdjacencyMatrix.isEmptyRow(id: String) = row(id).isEmpty()

fun AdjacencyMatrix.rows() = this.keys.associateWith { row(it) }

fun AdjacencyMatrix.col(id: String): List<String> {
    val col = mutableListOf<String>()
    this.filter { it.value[id] == true }.mapTo(col) { it.key }
    return col
}

fun AdjacencyMatrix.isEmptyCol(id: String) = col(id).isEmpty()

fun AdjacencyMatrix.cols() = this.keys.associateWith { col(it) }

