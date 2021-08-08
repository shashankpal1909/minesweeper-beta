package com.example.minesweeper_beta

class Minesweeper(private val width: Int, private val height: Int) {
    var board = Array(width) { Array(height) { MineCell() } }
    var status = Status.ONGOING
        internal set

    fun setMine(row: Int, column: Int): Boolean {
        if (board[row][column].value != MINE) {
            board[row][column].value = MINE
            return true
        }
        return false
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

    companion object {
        const val MINE = -1
    }
}

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