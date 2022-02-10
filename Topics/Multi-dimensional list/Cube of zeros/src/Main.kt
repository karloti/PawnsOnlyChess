// List(3) { List(3) { List(3) { 0 } } }.let(::println)
fun main() = List(7) { 0 }.windowed(3).windowed(3).let(::println)
// [[[0, 0, 0], [0, 0, 0], [0, 0, 0]], [[0, 0, 0], [0, 0, 0], [0, 0, 0]], [[0, 0, 0], [0, 0, 0], [0, 0, 0]]]