## Реализация coffman-graham-layering алгоритма

*В replit.com уже есть собранный jar используйте его для быстрого рисования графа*  
*Команда запуска jar:* `kotlin -classpath main.jar MainKt "path to graph file"`  
*пример:* `kotlin -classpath main.jar MainKt "examples/flow-from-lesson"`

*При запуске генерируется два изображения графа:*
1. *Результат алгоритма Грэхема—Коффмана*
2. *Результат алгоритма Грэхема—Коффмана и оптимизации кол-ва пересечений*

*В папке examples есть подготовленные примеры графов*

- **CoffmanGrahamLayeringAlgorithm.kt** - Реализация алгоритма
- **IntersectionOptimizer.kt** - Реализация алгоритма оптимизации количества пересечений
- **AdjacencyMatrix.kt** - Матрица смежности
- **ReadGraphml.kt** - Чтение графа из graphml файла
- **GraphGraphics.kt** - Инструменты отрисовки графа

При инициализации CoffmanGrahamLayeringAlgorithm задается:
- `layerSize` - количество проходов по графу
- `shuffle` - количество попыток перестановок вершин на каждом слое
- `placingStrategy` - стратегия предварительного размещения вершин (AVERAGE или MEDIAN)

При инициализации IntersectionOptimizer задается:
- `steps` - максимальное количество вершин на уровне
- `intersectionOptimizer` - эксземпляр оптимизатора пересечений

При инициализации GraphGraphics задается:
- `fillColor` - цвет заливки вершин
- `borderColor` - цвет ребер и границ вершин
- `xPadding` - размер сетки по оси x
- `xPadding` - размер сетки по оси y
- `bezierEdges` - флаг отрисовки ребер с помощью кривых Безье
- `nodeTitle` - флаг вывода на изображение графа идентификаторов вершин
- `grid` - флаг вывода сетки на изображение графа
