package app.avoor.planbot.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import app.avoor.planbot.ui.screens.LoginScreen

class LoginActivity: ComponentActivity() {
    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        setContent {
            Text("LoginScreen(false)")
            Text("Login stub")
        }
    }
}