package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.LoadingBar
import app.avoor.planbot.ui.components.UserHeading
import app.avoor.planbot.ui.helper.ProfilePictureManager
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.ProfileViewModel
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.ArrowBack
import tech.cataspect.m3x.MaterialList
import tech.cataspect.m3x.settings.RegularSetting

@Composable
fun MainProfileScreen(
    pfpManager: ProfilePictureManager,
    navigator: Navigator
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    val viewState by viewModel.uiState.collectAsState()

    // Provide managers
    viewModel.setManager(pfpManager)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navigator.goBack() }) {
                    Icon(
                        imageVector = Icons.ArrowBack,
                        contentDescription = stringResource(R.string.btn_back)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            if (viewState.progress) {
                LoadingBar()
            }
            UserHeading(user = viewState.user, Modifier.padding(vertical = 16.dp))
            RegularSetting(
                title = stringResource(R.string.profile_setting_pfp_title),
                body = null
            ) {
                viewModel.uploadProfilePicture()
            }

            RegularSetting(
                title = stringResource(R.string.profile_setting_logout_title),
                body = null
            ) {
                viewModel.signOut()
            }

            RegularSetting(
                title = stringResource(R.string.profile_setting_delete_title),
                body = null
            ) {
                navigator.navigate(Screen.DELETE_FEEDBACK)
            }
        }
    }
}