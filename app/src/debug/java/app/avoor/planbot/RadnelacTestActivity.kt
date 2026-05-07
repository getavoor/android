package app.avoor.planbot

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.avoor.planbot.data.calendar.CalendarEvent
import app.avoor.planbot.ui.theme.PlanbotTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

val ADJECTIVES = listOf(
    "calculating", "calm", "candid", "feline", "capital", "carefree", "careful", "careless",
    "caring", "cautious", "cavernous", "celebrated", "charming", "cheap", "cheerful", "cheery",
    "chief", "chilly", "chubby", "circular", "classic", "clean", "clear", "clear-cut", "clever",
    "close", "closed", "cloudy", "clueless", "clumsy", "cluttered", "coarse", "cold", "colorful",
    "colorless", "colossal", "comfortable", "common", "compassionate", "competent", "complete",
    "complex", "complicated", "composed", "concerned", "concrete", "confused", "conscious",
    "considerate", "constant", "content", "conventional", "cooked", "cool", "cooperative",
    "coordinated", "corny", "corrupt", "costly", "courageous", "courteous", "crafty", "crazy",
    "creamy", "creative", "creepy", "criminal", "crisp", "critical", "crooked", "crowded", "cruel",
    "crushing", "cuddly", "cultivated", "cultured", "cumbersome", "curly", "curvy", "cute", "cylindrical"
)

// test for event add features in CalendarRepository
class RadnelacTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanbotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        Greeting2(
                            name = "developer"
                        )
                        Button(
                            onClick = {
                                createTestEvent()
                            }
                        ) {
                            Text("create test event")
                        }
                        Button(
                            onClick = {
                                rescheduleCurrentEvent()
                            }
                        ) {
                            Text("move current event to be now - now+1h")
                        }
                        Text("WARNING: this uses the currently connected calendar repository, and so might actually edit your calendar unless planbot was built in demo mode")
                    }
                }
            }
        }
    }

    fun rescheduleCurrentEvent() {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                val repo = (application as AvoorApplication).container.calendarRepository

                // get the current event
                val events = repo.getCurrentEvent().take(1).toList()
                if (events.count() >= 0 && events[0] != null) {
                    val event = events[0]!!
                    Log.d("avr#rta", "got event: ${event.name}")

                    // get the current time
                    val now = Clock.System.now()
                    // add an hour to it
                    val end = now.plus(1.hours)

                    // reschedule the event to be between these hours
                    repo.reschedule(event.toCalendarEvent(TimeZone.currentSystemDefault()), now, end)

                    Log.d("avr#rta", "got event: ${event.name}")
                }
            }
            Toast.makeText(this@RadnelacTestActivity, "created", Toast.LENGTH_LONG).show()
        }
    }

    fun createTestEvent() {
        val repo = (application as AvoorApplication).container.calendarRepository

        // get the current time
        val now = Clock.System.now()
        // add an hour to it
        val end = now.plus(1.hours)
        // create a title with a random adjective
        val name = ADJECTIVES[Random.nextInt(0..ADJECTIVES.size)]

        // create an event
        val event = CalendarEvent(
            "test",
            "$name test event",
            now,
            end
        )
        MainScope().launch {
            withContext(Dispatchers.IO) {
                Log.d("avr#rta", "creating event: ${event.name}")
                repo.addEvent(event)
            }
            Toast.makeText(this@RadnelacTestActivity, "created", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello again $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    PlanbotTheme {
        Greeting2("Android")
    }
}