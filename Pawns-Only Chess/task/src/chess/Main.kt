package chess

import chess.Chess.Color.*
import kotlin.math.absoluteValue

class Chess(
    firstPlayerName: String,
    secondPlayerName: String,
) {
    enum class State(val message: String) {
        CANCELED("Canceled!"),
        STALEMATE("Stalemate!"),
        WHITE_WIN("White Wins!"),
        BLACK_WIN("Black Wins!"),
    }

    private enum class Color { NONE, WHITE, BLACK, }

    private sealed interface Player { val name: String; val color: Color }
    private data class PlayerWhite(override val name: String, override val color: Color) : Player
    private data class PlayerBlack(override val name: String, override val color: Color) : Player

    private sealed interface ResultAfterMove
    private data class Success(val move: String, val removeOppositeAt: String? = null) : ResultAfterMove
    private data class Failure(val reason: Throwable?) : ResultAfterMove

    private operator fun List<MutableList<Color>>.set(files: Char, ranks: Char, color: Color) { board['8' - ranks][files - 'a'] = color }
    private operator fun List<MutableList<Color>>.get(files: Char, ranks: Char): Color? = if (files in 'a'..'h' && ranks in '1'..'8') board['8' - ranks][files - 'a'] else null

    private val board = List(8) { MutableList(8) { NONE } }
    private val playerWhite = PlayerWhite(name = firstPlayerName, color = WHITE)
    private val playerBlack = PlayerBlack(name = secondPlayerName, color = BLACK)
    private var currentPlayer: Player = playerWhite
    private var lastMove: String? = null

    init {
        ('a'..'h').forEach { files ->
            board[files, '2'] = WHITE
            board[files, '7'] = BLACK
        }
    }

    fun start(): State {
        println(this)
        while (true) {
            println("${currentPlayer.name}'s turn:")
            val move = readln().takeIf { it != "exit" } ?: return State.CANCELED
            val result = check(move, currentPlayer)
            if (result is Success) {
                val r2: Char = move[3]
                move(result.move, result.removeOppositeAt)
                println(this)
                if (r2 in setOf('1', '8') || isNoBlackOrWhite()) return when (currentPlayer.color) {
                    NONE -> State.CANCELED
                    WHITE -> State.WHITE_WIN
                    BLACK -> State.BLACK_WIN
                }
                if (isStalemate()) return State.STALEMATE
                swapPlayer()
            }
            if (result is Failure) println(result.reason?.message)
        }
    }

    private fun check(move: String, player: Player): ResultAfterMove {
        val regex = Regex("[a-h][1-8][a-h][1-8]")
        val (f1, r1, f2, r2) = move.toList()
        return when {
            !move.matches(regex) -> Failure(Exception("Invalid Input"))
            board[f1, r1] != currentPlayer.color -> Failure(Exception("No ${player.color.name.lowercase()} pawn at $f1$r1"))
            board[f1, r1] == WHITE && board[f2, r2] == BLACK && (f2 - f1).absoluteValue == 1 && r2 == r1 + 1 -> Success(move, removeOppositeAt = "$f2$r2")
            board[f1, r1] == BLACK && board[f2, r2] == WHITE && (f2 - f1).absoluteValue == 1 && r2 == r1 - 1 -> Success(move, removeOppositeAt = "$f2$r2")
            board[f1, r1] == WHITE && board[f2, r2] == NONE && (f2 - f1).absoluteValue == 1 && r2 == r1 + 1 && board[f2, r1] == BLACK && "$f2$r1" == lastMove -> Success(move, removeOppositeAt = "$f2$r1")
            board[f1, r1] == BLACK && board[f2, r2] == NONE && (f2 - f1).absoluteValue == 1 && r2 == r1 - 1 && board[f2, r1] == WHITE && "$f2$r1" == lastMove -> Success(move, removeOppositeAt = "$f2$r1")
            board[f1, r1] == WHITE && board[f2, r2] == NONE && f2 == f1 && r2 - r1 == 2 && (board[f2 - 1, r2] == BLACK || board[f2 + 1, r2] == BLACK) -> Success(move)
            board[f1, r1] == BLACK && board[f2, r2] == NONE && f2 == f1 && r2 - r1 == -2 && (board[f2 - 1, r2] == WHITE || board[f2 + 1, r2] == WHITE) -> Success(move)
            board[f2, r2] != NONE || f1 != f2 -> Failure(Exception("Invalid Input"))
            player.color == WHITE && r2 == r1.inc() -> Success(move)
            player.color == BLACK && r2 == r1.dec() -> Success(move)
            player.color == WHITE && r1 == '2' && r2 in '3'..'4' -> Success(move)
            player.color == BLACK && r1 == '7' && r2 in '5'..'6' -> Success(move)
            else -> Failure(Exception("Invalid Input"))
        }
    }

    private fun move(move: String, removeOpposite: String? = null) {
        val (f1, r1, f2, r2) = move.toList()
        if (removeOpposite != null) board[removeOpposite[0], removeOpposite[1]] = NONE
        lastMove = move.takeLast(2)
        board[f2, r2] = board[f1, r1]!!
        board[f1, r1] = NONE
    }

    private fun swapPlayer() {
        currentPlayer = when (currentPlayer) {
            is PlayerBlack -> playerWhite
            is PlayerWhite -> playerBlack
        }
    }

    private fun isStalemate(): Boolean {
        var black = 0
        var white = 0
        ('2'..'7').forEach { r ->
            ('a'..'h').forEach { f ->
                when (board[f, r]!!) {
                    WHITE -> if (board[f, r + 1] == NONE || board[f - 1, r + 1] == BLACK || board[f + 1, r + 1] == BLACK) white++
                    BLACK -> if (board[f, r - 1] == NONE || board[f - 1, r - 1] == WHITE || board[f + 1, r - 1] == WHITE) black++
                    NONE -> {}
                }
            }
        }
        return (currentPlayer.color == WHITE && black == 0) || (currentPlayer.color == BLACK && white == 0)
    }

    private fun isNoBlackOrWhite() = board.flatten().fold(0 to 0) { (black, white), color ->
        when (color) {
            NONE -> black to white
            WHITE -> black to white.inc()
            BLACK -> black.inc() to white
        }
    }.let { (black, white) -> black == 0 || white == 0 }


    override fun toString() = """
          +---+---+---+---+---+---+---+---+
        8 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        7 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        6 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        5 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        4 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        3 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        2 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
        1 | %c | %c | %c | %c | %c | %c | %c | %c |
          +---+---+---+---+---+---+---+---+
            a   b   c   d   e   f   g   h""".trimIndent().format(*board.flatten().map { field ->
        when (field) {
            NONE -> ' '
            WHITE -> 'W'
            BLACK -> 'B'
        }
    }.toTypedArray())
}

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = readln()
    println("Second Player's name:")
    val secondPlayer = readln()
    val chess = Chess(firstPlayer, secondPlayer)
    val state = chess.start()
    if (state != Chess.State.CANCELED) println(state.message)
    println("Bye!")
}