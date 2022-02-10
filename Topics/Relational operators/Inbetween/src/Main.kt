fun main() {
    val (a, b, c) = List(3) { readln().toInt() }
    println(b <= a == a <= c)
}