import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val defaultGraphFile = "examples/flow-from-lesson"
    val graphFile = if (args.isEmpty()) defaultGraphFile else args[0]
    val nodes = readGraphml("$graphFile.graphml")

    val steps = nodes.size
    val shuffle = nodes.flatMap(Node::children).size
    val intersectionOptimizer = IntersectionOptimizer(steps, shuffle)

    val nodesPositions = CoffmanGrahamLayeringAlgorithm(3).place(nodes)
    val nodesPositionsWithOptimization = CoffmanGrahamLayeringAlgorithm(3, intersectionOptimizer).place(nodes)

    val graphGraphics = GraphGraphics(
        nodeRadius = 20.0,
        fillColor = Color.blue,
        borderColor = Color.black,
        xPadding = 80.0,
        yPadding = 100.0,
        bezierEdges = true,
        nodeTitle = true,
        grid = true
    )

    val image1 = graphGraphics.drawGraph(nodesPositions)
    val file1 = File("$graphFile.png")
    ImageIO.write(image1, "png", file1)

    val image2 = graphGraphics.drawGraph(nodesPositionsWithOptimization)
    val file2 = File("${graphFile}_with_optimization.png")
    ImageIO.write(image2, "png", file2)
}




