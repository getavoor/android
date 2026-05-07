package app.avoor.planbot

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import app.avoor.liboc.ShuffleUtils
import app.avoor.planbot.ui.theme.PlanbotTheme

class JniShufflerTestActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val nums = arrayOf(2, 3, 5, 8, 13, 21, 34)
        val utils = ShuffleUtils()
        utils.shuffle(nums)

        setContent {
            PlanbotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "developer, these are your numbers: ${nums.joinToString(",")}. This has been JNI, signing out",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}