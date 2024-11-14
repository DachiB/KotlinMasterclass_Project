package main

import kotlin.system.exitProcess

class Game {
    private val board = MutableList<Cell>(size = 9) {Cell.Empty}
    private var status: Status = Status.Idle
    private lateinit var player: Player


    fun start() {
        status = Status.Running
        println("----------------")
        println("Welcome To TicTacToe!")
        println("Choose a number from 1-9")
        println("----------------")
        getName()
        while (status is Status.Running) {
            getCell()
        }
    }

    private fun getName() {
        println("Choose Your Name: ")
        val name = readlnOrNull()
        try {
            require(value = name != null)
            player = Player(name = name, symbol = 'X')
            println("It's your move $name")
            printBoard()
        } catch (e: Throwable) {
        println("Enter Valid Name")
        }
    }

    private fun getCell() {
        val input = readlnOrNull()
        try {
            require(value = input != null)
            val cellNumber = input.toInt()
            require(cellNumber in 1..9) {println("Choose Number from 1-9")}
            setCell(cellNumber = cellNumber - 1)
        } catch (e: Throwable) {

        }
    }

    private fun setCell(cellNumber: Int) {
    val cell = board[cellNumber]
        if (cell is Cell.Empty) {
            board.set(
                index = cellNumber,
                element = Cell.Filled(player)
            )
            generateComputerMove()
            checkTheBoard()
            printBoard()
        } else {
            println("Cell Taken, Choose another!")
        }
    }

    private fun generateComputerMove() {
        try {
            val availableCells = mutableListOf<Int>()
            board.forEachIndexed { index, cell ->
                if (cell is Cell.Empty) availableCells.add(index)
                }
            if (availableCells.isNotEmpty()) {
                val  randomCell = availableCells.random()
                board.set(
                    index = randomCell,
                    element = Cell.Filled(Player())
                )
            }
        } catch (e: Throwable) {
        println("Error: ${e.message}")
        }
    }

    private fun printBoard() {
        println()
        println("--------")
        println("| ${board[0].placeHolder} ${board[1].placeHolder} ${board[2].placeHolder} |")
        println("| ${board[3].placeHolder} ${board[4].placeHolder} ${board[5].placeHolder} |")
        println("| ${board[6].placeHolder} ${board[7].placeHolder} ${board[8].placeHolder} |")
        println("--------")
        println()
    }
    private fun checkTheBoard() {
        val winningCombinations = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0,3,6),
            listOf(1,4,7),
            listOf(2,5,8),
            listOf(0,4,8),
            listOf(2,4,6),
        )
        val player1Cells = mutableListOf<Int>()
        val player2Cells = mutableListOf<Int>()
        board.forEachIndexed { index, cell ->
            if (cell.placeHolder == 'X') {
                player1Cells.add(index)
            }
            if (cell.placeHolder == 'O') {
                player2Cells.add(index)
            }
        }
        println("Player MOVES: $player1Cells")
        println("Computer MOVES: $player2Cells")

        run CombinationLoop@{
            winningCombinations.forEach { combination ->
                if (player1Cells.containsAll(combination)) {
                    won()
                    return@CombinationLoop
                }
                if (player2Cells.containsAll(combination)) {
                    lost()
                    return@CombinationLoop
                }
            }
        }
        if (board.none{it is Cell.Empty} && status is Status.Running) {
            draw()
        }
        if (status is Status.GameOver) {
        finish()
playAgain()
        }

    }

    private fun finish() {
        status = Status.Idle
    board.replaceAll { Cell.Empty }
    }

    private fun playAgain(){
        println("Play Again?")
        val input = readlnOrNull()
        try {
            require(value = input != null)
            val capitalizedInput = input.replaceFirstChar(Char::titlecase)
            val positive = capitalizedInput.contains("Y")
            val negative = capitalizedInput.contains("N")
            require(value = positive || negative)
            if (positive) {
                start()
            }
            else {
                exitProcess(0)
            }
        } catch (e: Exception) {
            println("Wrong Option")
            playAgain()
        }
    }

    private fun won() {
        status = Status.GameOver
        printBoard()
        println("Victory")
    }

    private fun lost() {
        status = Status.GameOver
        printBoard()
        println("Loss")
    }
    private fun draw() {
        status = Status.GameOver
        printBoard()
        println("Draw")
    }
}


data class Player(
    val name: String = "Computer",
    val symbol: Char = 'O'
)

sealed class Status {
    object Idle: Status()
    object Running: Status()
    object GameOver: Status()
}

sealed class Cell(val placeHolder: Char) {
    object Empty: Cell(placeHolder = '_')
    data class Filled (val player: Player): Cell(placeHolder = player.symbol)
}