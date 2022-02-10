private class ChristmasTree(private val color: String) {
    fun putTreeTopper(color: String) = TreeTopper(color).sparkle()
    private inner class TreeTopper(val color: String) {
        fun sparkle() = "The sparkling %s tree topper looks stunning on the %s Christmas tree!"
            .format(color, this@ChristmasTree.color)
            .let(::println)
    }
}