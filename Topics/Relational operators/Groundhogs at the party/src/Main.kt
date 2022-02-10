import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val i = scanner.nextInt()
    val b = scanner.nextBoolean()
    // I tried to change the range and not a cups for variety ;)
    println((15..25).map { it - 5 * true.compareTo(b) }.contains(i))
}
