package app.avoor.planbot

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.avoor.planbot.ui.theme.PlanbotTheme

class KillCanary256Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Toast.makeText(applicationContext, "created", Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanbotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "developer",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        Toast.makeText(applicationContext, "destroyed", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlanbotTheme {
        Greeting("Android")
    }
}