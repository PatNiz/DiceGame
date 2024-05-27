package com.niziolekp.dicegame

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niziolekp.dicegame.ui.theme.DiceGameTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var appState by rememberSaveable { mutableStateOf(AppState.START) }
                    var winner by rememberSaveable { mutableStateOf("") }
                    var player1Score by rememberSaveable { mutableStateOf(0) }
                    var player2Score by rememberSaveable { mutableStateOf(0) }

                    when (appState) {
                        AppState.START -> StartScreen(
                            onStart = { appState = AppState.GAME },
                            onCreateRoom = { appState = AppState.CREATE_ROOM },
                            onJoinRoom = { appState = AppState.JOIN_ROOM }
                        )
                        AppState.GAME -> {
                            DiceRollerApp { winningPlayer, score1, score2 ->
                                winner = winningPlayer
                                player1Score = score1
                                player2Score = score2
                                appState = AppState.END
                            }
                        }
                        AppState.END -> EndScreen(winner, player1Score, player2Score) {
                            appState = AppState.START
                        }
                        AppState.CREATE_ROOM -> CreateRoomScreen { roomId ->
                            // stan aplikacji na GameState.GAME po utworzeniu pokoju
                            appState = AppState.GAME
                        }
                        AppState.JOIN_ROOM -> JoinRoomScreen { roomId ->
                            // Obsługa dołączania do pokoju
                            // stan aplikacji na GameState.GAME po dołączeniu do pokoju
                            appState = AppState.GAME
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun StartScreen(
    onStart: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Dice Game!", fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onStart) {
            Text("Start Game", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCreateRoom) {
            Text("Create Room", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onJoinRoom) {
            Text("Join Room", fontSize = 24.sp)
        }
    }
}
@Composable
fun CreateRoomScreen(onCreate: (roomId: String) -> Unit) {
    var roomId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Create a Room",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = roomId,
            onValueChange = { roomId = it },
            label = { Text("Room ID") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onCreate(roomId) },
            enabled = roomId.isNotBlank()
        ) {
            Text("Create", fontSize = 24.sp)
        }
    }
}
@Composable
fun JoinRoomScreen(onJoin: (roomId: String) -> Unit) {
    var roomId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Join a Room",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = roomId,
            onValueChange = { roomId = it },
            label = { Text("Room ID") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onJoin(roomId) },
            enabled = roomId.isNotBlank()
        ) {
            Text("Join", fontSize = 24.sp)
        }
    }
}
enum class AppState {
    START, GAME, END, JOIN_ROOM, CREATE_ROOM
}
@Composable
fun EndScreen(winner: String, player1Score: Int, player2Score: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Congratulations $winner!", fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Final Scores:", fontSize = 24.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Player 1: $player1Score", fontSize = 20.sp)
        Text("Player 2: $player2Score", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRestart) {
            Text("Play Again", fontSize = 24.sp)
        }
    }
}
@Composable
fun DiceRollerApp(onEndGame: (String, Int, Int) -> Unit) {
    DiceGame(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center), onEndGame)
}
@Composable
fun DiceGame(modifier: Modifier = Modifier, onEndGame: (String, Int, Int) -> Unit) {
    var players by rememberSaveable { mutableStateOf(mutableListOf(Player(name = "Player 1"), Player(name = "Player 2"))) }
    var currentPlayerIndex by rememberSaveable { mutableStateOf(0) }
    val currentPlayer = players[currentPlayerIndex]
    var diceResults by rememberSaveable { mutableStateOf<List<Int>?>(null) }
    var diceSelection by rememberSaveable { mutableStateOf(List(5) { false }) }
    var selectedCategory by rememberSaveable { mutableStateOf<Category?>(null) }
    var rollRequired by rememberSaveable { mutableStateOf(false) }
    var rollCount by rememberSaveable { mutableStateOf(0) }
    val isRollEnabled by remember { derivedStateOf { !rollRequired || diceResults == null } }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val diceSize = if (isLandscape) (screenHeight / 6) else (screenWidth / 6)
    val imageResources = diceResults?.map { result ->
        when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    } ?: List(5) { R.drawable.dice_1 } // Dummy images if diceResults is empty

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (diceResults != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row {
                                imageResources.take(3).forEachIndexed { index, resource ->
                                    Box(
                                        modifier = Modifier
                                            .size(diceSize)
                                            .background(if (diceSelection[index]) Color.Gray else Color.Transparent)
                                            .clickable {
                                                diceSelection = diceSelection.toMutableList().also { it[index] = !it[index] }
                                            }
                                            .padding(4.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(resource),
                                            contentDescription = diceResults?.get(index).toString(),
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                            Row {
                                imageResources.drop(3).forEachIndexed { index, resource ->
                                    Box(
                                        modifier = Modifier
                                            .size(diceSize)
                                            .background(if (diceSelection[index + 3]) Color.Gray else Color.Transparent)
                                            .clickable {
                                                diceSelection = diceSelection.toMutableList().also { it[index + 3] = !it[index + 3] }
                                            }
                                            .padding(4.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(resource),
                                            contentDescription = diceResults?.get(index + 3).toString(),
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (rollCount < 3) {
                                    diceResults = diceResults?.mapIndexed { index, value ->
                                        if (diceSelection[index]) value else (1..6).random()
                                    }
                                    rollCount++
                                }
                                if (rollCount >= 3 || currentPlayer.rollsLeft <= 0) {
                                    rollRequired = true
                                }
                            },
                            enabled = currentPlayer.rollsLeft > 0 && rollCount < 3,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Text(text = if (rollCount < 3) "Roll Again" else "Roll", fontSize = 24.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                diceResults = List(5) { (1..6).random() }
                                diceSelection = List(5) { false }
                                currentPlayer.rollsLeft -= 1
                                rollRequired = true
                                rollCount = 1
                            },
                            enabled = isRollEnabled,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Text(text = "Roll to Start", fontSize = 24.sp)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Current Player: ${currentPlayer.name}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ScoreTable(
                        currentPlayer = players[currentPlayerIndex],
                        player1 = players[0],
                        player2 = players[1],
                        diceResults = diceResults ?: emptyList(),
                        selectedCategory = selectedCategory,
                        rollRequired = rollRequired
                    ) { category ->
                        if (rollRequired && category != null && currentPlayer.scoreCard[category] == null) {
                            selectedCategory = category
                            currentPlayer.scoreCard[category] = calculateScore(category, diceResults!!)
                            rollRequired = false
                            // Check if the game has ended
                            val allCategoriesFilled = players.all { player ->
                                Category.values().all { category ->
                                    player.scoreCard[category] != null
                                }
                            }
                            if (allCategoriesFilled) {
                                val player1Score = players[0].scoreCard.values.sum()
                                val player2Score = players[1].scoreCard.values.sum()
                                val winner = if (player1Score > player2Score) players[0].name else if (player2Score > player1Score) players[1].name else "No one, it's a tie!"
                                onEndGame(winner, player1Score, player2Score)
                            } else {
                                players = players.toMutableList()
                                currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                                diceResults = null
                                rollCount = 0
                            }
                        }
                    }
                }
            }
        } else {
            // Existing vertical mode logic (not modified)
            Text(
                text = "Current Player: ${currentPlayer.name}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            if (diceResults == null || rollCount == 0) {
                Button(
                    onClick = {
                        diceResults = List(5) { (1..6).random() }
                        diceSelection = List(5) { false }
                        currentPlayer.rollsLeft -= 1
                        rollRequired = true
                        rollCount = 1
                    },
                    enabled = isRollEnabled,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(text = "Roll to Start", fontSize = 24.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                val diceContent = @Composable {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        imageResources.forEachIndexed { index, resource ->
                            Box(
                                modifier = Modifier
                                    .size(diceSize)
                                    .background(if (diceSelection[index]) Color.Gray else Color.Transparent)
                                    .clickable {
                                        diceSelection = diceSelection.toMutableList().also { it[index] = !it[index] }
                                    }
                            ) {
                                Image(
                                    painter = painterResource(resource),
                                    contentDescription = diceResults?.get(index).toString(),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    diceContent()
                }
                Button(
                    onClick = {
                        if (rollCount < 3) {
                            diceResults = diceResults?.mapIndexed { index, value ->
                                if (diceSelection[index]) value else (1..6).random()
                            }
                            rollCount++
                        }
                        if (rollCount >= 3 || currentPlayer.rollsLeft <= 0) {
                            rollRequired = true
                        }
                    },
                    enabled = currentPlayer.rollsLeft > 0 && rollCount < 3,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(text = if (rollCount < 3) "Roll Again" else "Roll", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ScoreTable(
                currentPlayer = players[currentPlayerIndex],
                player1 = players[0],
                player2 = players[1],
                diceResults = diceResults ?: emptyList(),
                selectedCategory = selectedCategory,
                rollRequired = rollRequired
            ) { category ->
                if (rollRequired && category != null && currentPlayer.scoreCard[category] == null) {
                    selectedCategory = category
                    currentPlayer.scoreCard[category] = calculateScore(category, diceResults!!)
                    rollRequired = false
                    // Check if the game has ended
                    val allCategoriesFilled = players.all { player ->
                        Category.values().all { category ->
                            player.scoreCard[category] != null
                        }
                    }
                    if (allCategoriesFilled) {
                        val player1Score = players[0].scoreCard.values.sum()
                        val player2Score = players[1].scoreCard.values.sum()
                        val winner = if (player1Score > player2Score) players[0].name else if (player2Score > player1Score) players[1].name else "No one, it's a tie!"
                        onEndGame(winner, player1Score, player2Score)
                    } else {
                        players = players.toMutableList()
                        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                        diceResults = null
                        rollCount = 0
                    }
                }
            }
        }
    }
}
@Composable
fun ScoreTable(
    currentPlayer: Player,
    player1: Player,
    player2: Player,
    diceResults: List<Int>,
    selectedCategory: Category?,
    rollRequired: Boolean,
    onSelectCategory: (Category?) -> Unit
) {
    val potentialScores = if (rollRequired) calculatePotentialScores(currentPlayer, diceResults) else emptyMap()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Score Table", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(IntrinsicSize.Max)) {
            Text(text = "Category", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
            Text(text = player1.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
            Text(text = player2.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        Divider(color = Color.Gray, thickness = 1.dp)

        Category.values().forEach { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if ((player1.scoreCard[category] == null || player2.scoreCard[category] == null) && rollRequired) onSelectCategory(category) }
                    .padding(horizontal = 16.dp)
                    .height(IntrinsicSize.Max)
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category.displayName, fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
                Text(
                    text = player1.scoreCard[category]?.toString() ?: (if (currentPlayer == player1) potentialScores[category]?.toString() ?: "" else ""),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (player1.scoreCard[category] == null && currentPlayer == player1) Color.Gray else Color.Red,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
                Text(
                    text = player2.scoreCard[category]?.toString() ?: (if (currentPlayer == player2) potentialScores[category]?.toString() ?: "" else ""),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (player2.scoreCard[category] == null && currentPlayer == player2) Color.Gray else Color.Red,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Divider(color = Color.Gray, thickness = 1.dp)

            // Add Bonus after Sixes row immediately after the Sixes category
            if (category == Category.SIXES) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(IntrinsicSize.Max).animateContentSize(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Bonus", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
                    Text(text = player1.calculateBonus().toString(), fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
                    Text(text = player2.calculateBonus().toString(), fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(IntrinsicSize.Max).animateContentSize(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Total Score", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
            Text(text = (player1.totalScoreWithBonus()).toString(), fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxHeight().width(1.dp))
            Text(text = (player2.totalScoreWithBonus()).toString(), fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}

// Assuming Player class has methods to calculate bonus and total score with bonus
fun Player.calculateBonus(): Int {
    val categoriesToSum = listOf(Category.ONES, Category.TWOS, Category.THREES, Category.FOURS, Category.FIVES, Category.SIXES)
    val sum = categoriesToSum.sumOf { scoreCard[it] ?: 0 }
    return if (sum >=63) 35 else 0
}

fun Player.totalScoreWithBonus(): Int {
    val baseScore = scoreCard.values.sum()
    return baseScore + calculateBonus()
}
enum class Category(val displayName: String) {
    ONES("Ones"),
    TWOS("Twos"),
    THREES("Threes"),
    FOURS("Fours"),
    FIVES("Fives"),
    SIXES("Sixes"),
    THREE_OF_A_KIND("3x"),
    FOUR_OF_A_KIND("4x"),
    FULL_HOUSE("Full House"),
    SMALL_STRAIGHT("Small Straight"),
    LARGE_STRAIGHT("Large Straight"),
    YAHTZEE("Yahtzee"),
    CHANCE("Chance");

    companion object {
        fun fromDisplayName(displayName: String): Category? {
            return values().find { it.displayName == displayName }
        }
    }
}
fun calculateScore(category: Category, diceResults: List<Int>): Int {
    val counts = diceResults.groupingBy { it }.eachCount()
    return when (category) {
        Category.ONES -> diceResults.count { it == 1 } * 1
        Category.TWOS -> diceResults.count { it == 2 } * 2
        Category.THREES -> diceResults.count { it == 3 } * 3
        Category.FOURS -> diceResults.count { it == 4 } * 4
        Category.FIVES -> diceResults.count { it == 5 } * 5
        Category.SIXES -> diceResults.count { it == 6 } * 6
        Category.THREE_OF_A_KIND -> if (counts.values.any { it >= 3 }) diceResults.sum() else 0
        Category.FOUR_OF_A_KIND -> if (counts.values.any { it >= 4 }) diceResults.sum() else 0
        Category.FULL_HOUSE -> if (counts.values.contains(3) && counts.values.contains(2)) 25 else 0
        Category.SMALL_STRAIGHT -> if (hasStraight(diceResults, 4)) 30 else 0
        Category.LARGE_STRAIGHT -> if (hasStraight(diceResults, 5)) 40 else 0
        Category.YAHTZEE -> if (counts.values.any { it == 5 }) 50 else 0
        Category.CHANCE -> diceResults.sum()
    }
}
fun hasStraight(dice: List<Int>, length: Int): Boolean {
    val uniqueSortedDice = dice.toSet().sorted()
    var maxLength = 1
    var currentLength = 1
    for (i in 1 until uniqueSortedDice.size) {
        if (uniqueSortedDice[i] == uniqueSortedDice[i - 1] + 1) {
            currentLength++
            if (currentLength > maxLength) {
                maxLength = currentLength
            }
        } else {
            currentLength = 1
        }
    }
    return maxLength >= length
}
data class Player(
    val name: String,
    val scoreCard: MutableMap<Category, Int> = mutableStateMapOf(),
    var rollsLeft: Int = 12
)
fun calculatePotentialScores(player: Player, diceResults: List<Int>): Map<Category, Int> {
    return Category.values().associateWith { category ->
        player.scoreCard[category] ?: calculateScore(category, diceResults)
    }
}
