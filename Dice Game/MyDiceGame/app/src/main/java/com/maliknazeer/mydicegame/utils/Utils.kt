package com.maliknazeer.mydicegame.utils

import com.maliknazeer.mydicegame.R
import com.maliknazeer.mydicegame.data.model.ComputerDecision
import kotlin.random.Random


fun computerDecision(
    remainingRolls: Int,
    winningTotal: Int,
    humanTotal: Int,
    computerTotal: Int
): ComputerDecision {
    val result = performThrow()
    val rolls = remainingRolls - 1

    val currentScore = computerTotal + result.sum()
    if (currentScore >= winningTotal)
        return ComputerDecision(result = result, remaining = rolls, decision = true)
    if (rolls == 0)
        return ComputerDecision(result = result, remaining = rolls, decision = true)
    if (currentScore > humanTotal)
        return ComputerDecision(result = result, remaining = rolls, decision = true)

    return ComputerDecision(result = result, remaining = rolls, decision = false)
}


fun getDiceImageResource(diceValue: Int): Int {
    return when (diceValue) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> throw IllegalArgumentException("Invalid dice value: $diceValue")
    }
}

fun performThrow(): List<Int> {
    return List(5) { Random.nextInt(1, 7) }
}