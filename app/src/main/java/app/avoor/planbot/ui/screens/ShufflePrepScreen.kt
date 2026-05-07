package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import app.avoor.planbot.R
import app.avoor.planbot.ShuffleImpl
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun ShufflePrepScreen(
    shuffleImpl: ShuffleImpl,
    navigator: Navigator
) {
    val splashes = stringArrayResource(R.array.shuffle_loading)
    // shuffle the calendar in the background
    LaunchedEffect(null) {
        shuffleImpl.shuffleEvents()
        // wait a bit to ensure that the transition is not jarring
        delay(2000)
        // once done, navigate to the timer
        navigator.replace(Screen.TIMER)
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(splashes[Random.nextInt(0..splashes.size)])
    }
}