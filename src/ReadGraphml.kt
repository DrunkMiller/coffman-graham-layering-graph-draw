import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

fun readGraphml(path: String): Collection<Node> {
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(path))
    val nodes = findNodes(doc)
    val edges = findEdges(doc)
    return link(nodes, edges)
}

private fun link(nodes: List<String>, edges: List<Pair<String, String>>): Collection<Node> {
    val mappedNodes = nodes.associateWith { Node(it) }
    edges.forEach { (source, target) ->
        val parent = mappedNodes[source] ?: throw IllegalStateException("GRAPHML: wrong graph structure")
        val child = mappedNodes[target] ?: throw IllegalStateException("GRAPHML: wrong graph structure")
        parent.children.add(child)
    }
    //val root = (nodes - edges.map { it.second })
    //if (root.size != 1) throw IllegalStateException("GRAPHML: wrong graph structure")
    return mappedNodes.values
    //return mappedNodes[root.first()] ?: throw IllegalStateException("GRAPHML: wrong graph structure")
}

private fun findNodes(doc: Document) = with(doc.getElementsByTagName("node")) {
    (0 until length).map {
        item(it).findAttributeVal("id")
    }
}

private fun findEdges(doc: Document) = with(doc.getElementsByTagName("edge")) {
    (0 until length).map {
        item(it).findAttributeVal("source") to item(it).findAttributeVal("target")
    }
}

private fun org.w3c.dom.Node.findAttributeVal(name: String) = attributes.getNamedItem(name).nodeValue