package com.maliknazeer.mydicegame.ui.screens.gamescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maliknazeer.mydicegame.ui.Screen
import com.maliknazeer.mydicegame.ui.screens.SharedViewModel
import com.maliknazeer.mydicegame.utils.computerDecision
import com.maliknazeer.mydicegame.utils.getDiceImageResource
import com.maliknazeer.mydicegame.utils.performThrow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    var diceValues by rememberSaveable { mutableStateOf(emptyList<Int>()) }
    var humanTotal by rememberSaveable { mutableIntStateOf(0) }
    var computerTotal by rememberSaveable { mutableIntStateOf(0) }
    var isHumanDice by rememberSaveable { mutableStateOf(true) }
    var values by rememberSaveable { mutableStateOf(emptyList<Int>()) }
    var remainingRolls by rememberSaveable { mutableIntStateOf(3) }
    var winningTotal by rememberSaveable { mutableIntStateOf(101) }
    var winner by rememberSaveable { mutableStateOf("") }
    var haveThrown by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showTargetDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = "ComputerDecision") {
        while (true) {
            if (!isHumanDice) {
                val decision =
                    computerDecision(remainingRolls, winningTotal, humanTotal, computerTotal)
                values = decision.result
                remainingRolls = decision.remaining
                isHumanDice = decision.decision
                if (isHumanDice) {
                    remainingRolls = if (winner == "Tie") 1 else 3
                    computerTotal += decision.result.sum()
                    winner = checkWinner(winningTotal, humanTotal, computerTotal)
                    if (winner.isNotEmpty() && winner != "Tie") {
                        showDialog = true
                    }
                }
            }
            delay(2000L)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {

            Column {
                Text(
                    modifier = Modifier.height(100.dp),
                    text = "H:${sharedViewModel.humanWin}/C:${sharedViewModel.robotWin}\nTarget: $winningTotal",
                    style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = { showTargetDialog = true },
                    enabled = humanTotal == 0 && computerTotal == 0
                ) {
                    Text("Update Target")
                }
            }


            Text(
                modifier = Modifier.height(100.dp),
                text = "Your Score: $humanTotal\nComputer Total: $computerTotal",
                style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold
            )

        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.height(100.dp)
            ) {
                values.forEach { diceValue ->
                    Image(
                        painter = painterResource(id = getDiceImageResource(diceValue)),
                        contentDescription = "Dice",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(60.dp)
                            .width(60.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),

                horizontalArrangement = Arrangement.SpaceAround
            ) {

                if (isHumanDice) {
                    if (remainingRolls == 0) {
                        humanTotal += values.sum()
                        isHumanDice = !isHumanDice
                        remainingRolls = if (winner == "Tie") 1 else 3
                        haveThrown = false
                    }
                    Button(
                        onClick = {
                            values = performThrow()
                            remainingRolls--
                            diceValues = values
                            haveThrown = true
                        }) {
                        Text(text = "Throw")
                    }
                    Button(
                        onClick = {
                            if (isHumanDice) {
                                humanTotal += values.sum()
                            }
                            isHumanDice = !isHumanDice
                            remainingRolls = if (winner == "Tie") 1 else 3
                            haveThrown = false
                        },
                        enabled = haveThrown
                    ) {
                        Text(text = "Score")
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Game Finished") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = winner,
                        color = if (winner == "You won!") Color.Green else Color.Red
                    )
                    Text(text = "Click on the Back button to start again")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        if (winner == "You won!")
                            sharedViewModel.humanWin += 1
                        else
                            sharedViewModel.robotWin += 1
                        navController.navigate(Screen.HomeScreen.route)
                    }
                ) {
                    Text(text = "Back")
                }
            }
        )
    }

    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text(text = "Update Target") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = winningTotal.toString(),
                        onValueChange = {
                            if (it.toIntOrNull() != null) {
                                winningTotal = it.toInt()
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showTargetDialog = false
                    }
                ) {
                    Text(text = "Save")
                }
            }
        )
    }
}

fun checkWinner(winningTotal: Int, humanTotal: Int, computerTotal: Int): String {
    if (humanTotal > winningTotal && humanTotal == computerTotal) {
        return "Tie"
    } else if (humanTotal >= winningTotal && humanTotal > computerTotal) {
        return "You won!"
    } else if (computerTotal >= winningTotal) {
        return "You lose!"
    } else return ""
}