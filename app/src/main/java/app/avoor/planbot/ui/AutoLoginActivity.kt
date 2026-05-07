package app.avoor.planbot.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class AutoLoginActivity: ComponentActivity() {
    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        setContent {
            // this handler is so simple that it doesn't need a viewmodel
            AutoLoginScreen()
        }
    }
}