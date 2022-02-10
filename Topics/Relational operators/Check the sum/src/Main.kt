import java.util.Scanner

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    // put your code here
    val (a, b, c) = Array(3) { scanner.nextInt() }
    println(when (20) {
        a + b -> true
        a + c -> true
        b + c -> true
        else -> false
    })
}
