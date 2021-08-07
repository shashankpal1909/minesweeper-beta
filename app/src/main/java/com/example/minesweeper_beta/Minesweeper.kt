package com.example.minesweeper_beta

//fun main(args: Array<String>) {
//    val read = Scanner(System.`in`)
//    val size = read.nextInt()
//    val count = read.nextInt()
//
//    //creates the instance of the board
//    val game = Minesweeper(size)
//    //sets mines as per the input
//    for (i in 1..count)
//        game.setMine(read.nextInt(), read.nextInt())
//
//    //iterates input ans prints output
//    val n = read.nextInt()
//    for (i in 1..n) {
//        if (game.move(read.nextInt(), read.nextInt(), read.nextInt())) {
//            println("true")
//            game.displayBoard()
//        } else {
//            println("false")
//        }
//        if (game.status != Status.ONGOING)
//            break
//    }
//    println(game.status)
//}

//board class
class Minesweeper(private val width: Int, private val height: Int) {
    var board = Array(width) { Array(height) { MineCell() } }
    var status = Status.ONGOING
        internal set

    //sets up mines
    fun setMine(row: Int, column: Int): Boolean {
        if (board[row][column].value != MINE) {
            board[row][column].value = MINE
//            updateNeighbours(row, column)
            return true
        }
        return false
    }

    //updates the values os the cells neighbouring to the mines
    private fun updateNeighbours(row: Int, column: Int) {
        for (i in movement) {
            for (j in movement) {
                if (((row + i) in 0 until width) && ((column + j) in 0 until height) && board[row + i][column + j].value != MINE)
                    board[row + i][column + j].value++
            }
        }
    }

    private fun getAdjacentMines(row: Int, column: Int): Int {
        var noOfMines = 0
        for (r in row - 1..row + 1) {
            for (c in column - 1..column + 1) {
                if (r in board.indices && c in board[r].indices && board[r][c].value == MINE) {
                    noOfMines++
                }
            }
        }
        return noOfMines
    }

    private fun checkWin() {
        var mineTriggered = false
        var cellLeft = width * height
        for (i in board.indices) {
            for (j in board[i].indices) {
                mineTriggered = board[i][j].value == MINE && board[i][j].isRevealed
//                cellLeft = board[i][j].value != MINE && (!board[i][j].isRevealed || board[i][j].isMarked)
                if (board[i][j].isRevealed || board[i][j].value == MINE || board[i][j].isMarked) {
                    cellLeft--
                }
            }
        }
        if (!mineTriggered && cellLeft == 0) {
            status = Status.WON
        }
    }

    fun move(choice: Int, x: Int, y: Int): Boolean {

        if (status == Status.LOST || status == Status.WON) {
            return false
        }

        if (choice == 1) {

            if (board[x][y].isRevealed || board[x][y].isMarked) {
                return false
            }

            if (board[x][y].value == MINE) {
                status = Status.LOST
                return true
            } else if (board[x][y].value != MINE) {
                val noOfMines = getAdjacentMines(x, y)
                board[x][y].isRevealed = true
                if (noOfMines > 0) {
                    board[x][y].value = noOfMines
                } else {
                    for (r in x - 1..x + 1) {
                        for (c in y - 1..y + 1) {
                            if (r in board.indices && c in board[r].indices)
                                if (!board[r][c].isRevealed)
                                    move(choice, r, c)
                        }
                    }

                }
                checkWin()
                return true
            }

        } else if (choice == 2) {
            if (board[x][y].isRevealed || board[x][y].isMarked) {
                return false
            }
            board[x][y].isMarked = true
            checkWin()
            return true
        }
        return false
    }

    //displays the board
    fun displayBoard() {
        board.forEach { row ->
            row.forEach {
                if (it.isRevealed)
                    print("| ${it.value} |")
                else if (it.isMarked)
                    print("| X |")
                else if (status == Status.LOST && it.value == MINE)
                    print("| * |")
                else if (status == Status.WON && it.value == MINE)
                    print("| X |")
                else
                    print("|   |")
            }
            println()
        }
    }

    companion object {
        const val MINE = -1
        val movement = intArrayOf(-1, 0, 1)
    }
}

//mineCell Data Class
data class MineCell(
    var value: Int = 0,
    var isRevealed: Boolean = false,
    var isMarked: Boolean = false,
    var isFinal: Boolean = false
)

enum class Status {
    WON,
    ONGOING,
    LOST
}