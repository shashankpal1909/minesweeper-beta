package com.example.minesweeper_beta

class Minesweeper(private val width: Int, private val height: Int) {

    // ARRAY TO STORE BOARD
    var board = Array(width) { Array(height) { MineCell() } }
    var status = Status.ONGOING
        internal set
    var mineCount = 0
        internal set

    // SETS MINES
    fun setMine(row: Int, column: Int): Boolean {
        if (board[row][column].value != MINE) {
            board[row][column].value = MINE
            mineCount++
            return true
        }
        return false
    }

    // GET ADJACENT MINES TO A CELL
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

    // CHECKS IF USER WON THE GAME
    private fun checkWin() {
        var mineTriggered = false
        var cellLeft = width * height
        var flaggedCells = 0
        for (i in board.indices) {
            for (j in board[i].indices) {
                mineTriggered = board[i][j].value == MINE && board[i][j].isRevealed
                if (board[i][j].isRevealed || board[i][j].value == MINE || board[i][j].isMarked) {
                    if (board[i][j].isMarked && board[i][j].value == MINE) {
                        flaggedCells++
                    }
                    cellLeft--
                }
            }
        }
        if ((!mineTriggered && cellLeft == 0) || flaggedCells == mineCount) {
            status = Status.WON
        }
    }

    // CHECKS IF A MOVE IS VALID & UPDATE THE BOARD
    fun move(choice: Int, x: Int, y: Int): Boolean {

        when {

            // RETURN FALSE IF GAME HAS ALREADY ENDED
            status == Status.LOST || status == Status.WON -> return false

            // CHECK IF CELL CAN BE UNTURNED
            choice == 1 ->
                when {

                    // RETURN FALSE IS CELL ALREADY REVEALED OR IS FLAGGED
                    board[x][y].isRevealed || board[x][y].isMarked -> return false

                    // IF CELL CONTAINS MINE THEN GAME ENDS
                    board[x][y].value == MINE -> {
                        status = Status.LOST
                        return true
                    }

                    // IF CELL IS NOT A MINE THEN RECURSIVELY OPEN CELLS TILL ADJACENT MINES
                    board[x][y].value != MINE -> {
                        val noOfMines = getAdjacentMines(x, y)
                        board[x][y].isRevealed = true
                        if (noOfMines > 0) {
                            board[x][y].value = noOfMines
                        } else {
                            for (r in x - 1..x + 1)
                                for (c in y - 1..y + 1)
                                    if (r in board.indices && c in board[r].indices && !board[r][c].isRevealed)
                                        move(choice, r, c)
                        }
                        checkWin()
                        return true
                    }
                }

            // CHECK IF CELL CAN BE FLAGGED
            choice == 2 -> {
                return when {

                    // RETURN FALSE IS CELL ALREADY REVEALED OR IS FLAGGED
                    board[x][y].isRevealed || board[x][y].isMarked -> false

                    // ELSE MARK THE CELL AS FLAGGED
                    else -> {
                        board[x][y].isMarked = true
                        checkWin()
                        true
                    }
                }
            }
        }

        return false
    }

    companion object {
        const val MINE = -1
    }
}

// MINE CELL DATA CLASS
data class MineCell(
    var value: Int = 0,
    var isRevealed: Boolean = false,
    var isMarked: Boolean = false,
    var isFinal: Boolean = false
)

// GAME'S STATUS ENUM CLASS
enum class Status {
    WON,
    ONGOING,
    LOST
}