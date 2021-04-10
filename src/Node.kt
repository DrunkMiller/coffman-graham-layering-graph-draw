
data class Node(
    val id: String,
    val children: MutableList<Node> = mutableListOf()
) {
    fun isDummy() = id.startsWith("dummy_")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Node
        if (id != other.id) return false
        return true
    }

    override fun hashCode() = id.hashCode()
}

data class Position(val y: Double, val x: Double) {
    fun shift(shift: Double) = this.copy(x = this.x - shift)
}
