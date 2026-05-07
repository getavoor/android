package app.avoor.planbot.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.avoor.symbols.Icons
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import app.avoor.planbot.BuildConfig
import app.avoor.planbot.R
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.icons.Info
import tech.cataspect.m3x.settings.RegularSetting
import tech.cataspect.m3x.settings.SettingsPadding
import androidx.core.net.toUri
import app.avoor.planbot.UriUtils
import app.avoor.planbot.data.prefs.PomodoroPreset
import app.avoor.planbot.data.prefs.PreferenceStore
import app.avoor.planbot.pomodoro.PomodoroService
import app.avoor.planbot.ui.components.NumberPicker
import app.avoor.planbot.ui.components.SettingsGroupLabel
import app.avoor.symbols.icons.ArrowBack
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tech.cataspect.m3x.settings.SettingsDividerPadding


@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainSettingsScreen(
    context: Context,
    navigator: Navigator,
    prefStore: PreferenceStore
) {
    val size = remember {
        mutableLongStateOf(
            (context.imageLoader.diskCache?.size ?: 0) + (context.imageLoader.memoryCache?.size ?: 0)
        )
    }

    var showGroupJoinDialog by remember {mutableStateOf(false)}

    if (showGroupJoinDialog) {
        JoinGroupDialog(navigator) {
            showGroupJoinDialog = false
        }
    }

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
            Modifier.verticalScroll(rememberScrollState()).padding(innerPadding)
        ) {
            RegularSetting(
                title = stringResource(R.string.setting_clearCache_title),
                body = stringResource(R.string.setting_clearCache_desc).format(
                    showStorage(
                        size.longValue,
                        context
                    )
                )
            ) {
                context.imageLoader.diskCache?.clear()
                context.imageLoader.memoryCache?.clear()
                size.longValue = (context.imageLoader.diskCache?.size
                    ?: 0) + (context.imageLoader.memoryCache?.size ?: 0)
                Toast.makeText(
                    context,
                    context.getString(R.string.setting_clearCache_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }

            // TODO this is a temporary location, this setting should be in the profile page
            RegularSetting(
                title = stringResource(R.string.profile_setting_delete_title),
                body = null
            ) {
                navigator.navigate(Screen.DELETE_FEEDBACK)
            }

            SettingsGroupLabel(
                stringResource(R.string.setting_pomo_group),
                Modifier.padding(horizontal = SettingsPadding, vertical = SettingsDividerPadding)
            )

            // timer mode setting
            var pomoRegularMode by remember { mutableStateOf(PomodoroService.isRegularTimer) }
            Text(
                text = stringResource(R.string.setting_pomoMode_title),
                modifier = Modifier.padding(
                    top = SettingsDividerPadding,
                    start = SettingsPadding,
                    end = SettingsPadding,
                    bottom = SettingsDividerPadding
                ),
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                Modifier.padding(horizontal = SettingsPadding).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                ToggleButton(
                    checked = pomoRegularMode,
                    onCheckedChange = {
                        MainScope().launch {
                            prefStore.setPomodoroRegularMode(it)
                            PomodoroService.isRegularTimer = it
                        }
                        pomoRegularMode = it
                    },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
                ) {
                    Text(stringResource(R.string.setting_pomoMode_reg_title))
                }
                ToggleButton(
                    checked = !pomoRegularMode,
                    onCheckedChange = {
                        MainScope().launch {
                            prefStore.setPomodoroRegularMode(!it)
                            PomodoroService.isRegularTimer = !it
                        }
                        pomoRegularMode = !it
                    },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
                ) {
                    Text(stringResource(R.string.setting_pomoMode_pomo_title))
                }
            }
            // description for this setting
            Text(
                modifier = Modifier.padding(
                    horizontal = SettingsPadding,
                    vertical = SettingsDividerPadding
                ).fillMaxWidth(),
                text = if (pomoRegularMode) {
                    stringResource(R.string.setting_pomoMode_reg_desc)
                } else {
                    stringResource(R.string.setting_pomoMode_pomo_desc)
                },
                style = MaterialTheme.typography.bodyMedium
            )

            if (!pomoRegularMode) {
                // timer duration settings
                var pomoBreakDur by remember { mutableIntStateOf((PomodoroService.TIME_BREAK / 60).toInt()) }
                var pomoWorkDur by remember { mutableIntStateOf((PomodoroService.TIME_WORK / 60).toInt()) }
                var pomoPreset by remember { mutableStateOf(PomodoroService.preset) }
                Text(
                    text = stringResource(R.string.setting_pomoDur_title),
                    modifier = Modifier.padding(
                        top = SettingsPadding,
                        start = SettingsPadding,
                        end = SettingsPadding,
                        bottom = SettingsDividerPadding
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
                LazyRow(
                    Modifier.padding(horizontal = SettingsPadding).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    itemsIndexed(PomodoroPreset.entries) { i, preset ->
                        ToggleButton(
                            checked = (pomoPreset == preset),
                            onCheckedChange = {
                                if (it) {
                                    MainScope().launch {
                                        prefStore.setPomodoroPreset(preset)
                                        PomodoroService.applyPreset(preset)
                                    }
                                    pomoPreset = preset
                                }
                            },
                            modifier = Modifier.semantics { role = Role.RadioButton },
                            shapes =
                                when (i) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    PomodoroPreset.entries.size - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                }
                        ) {
                            Text(stringResource(preset.resID))
                        }
                    }
                }
                // description for this setting
                if (pomoPreset != PomodoroPreset.CUSTOM) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = SettingsPadding,
                            vertical = SettingsDividerPadding
                        ).fillMaxWidth(),
                        text = stringResource(R.string.setting_pomoDur_option_desc)
                            .format(pomoPreset.workTime, pomoPreset.breakTime),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // for custom, show input fields
                else {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = SettingsPadding,
                            vertical = SettingsDividerPadding
                        ).fillMaxWidth()
                    ) {
                        NumberPicker(
                            pomoWorkDur
                        ) {
                            pomoWorkDur = it
                            PomodoroService.TIME_WORK = it * 60L
                        }
                        Spacer(Modifier.width(16.dp))
                        NumberPicker(
                            pomoBreakDur
                        ) {
                            pomoBreakDur = it
                            PomodoroService.TIME_BREAK = it * 60L
                        }
                    }
                }
            }

            // developer tools
            if (BuildConfig.DEBUG) {
                Text("Developer tools", Modifier.padding(SettingsPadding))
                RegularSetting(
                    title = "Restart verify complete",
                    body = "Restart part 2 of nux"
                ) {
                    navigator.navigate(Screen.VERIFY_COMPLETE)
                }
                RegularSetting(
                    title = "Discovery socket messenger",
                    body = "Send arbitrary messages to the discovery socket."
                ) {
                    navigator.navigate(Screen.DEV_MESSENGER)
                }
                Text(
                    "Early features - warning: they are very unstable",
                    Modifier.padding(SettingsPadding)
                )
                RegularSetting(
                    title = "Start group discovery",
                    body = "Start a group discovery session."
                ) {
                    navigator.navigate(Screen.DISCOVERY_GROUP)
                }
                RegularSetting(
                    title = "Join group discovery",
                    body = "Join a group discovery session by entering its join link."
                ) {
                    showGroupJoinDialog = true
                }
            }
        }
    }
}

@Composable
fun JoinGroupDialog(
    navigator: Navigator,
    onDismiss: () -> Unit
) {
    var url by remember {
        mutableStateOf("")
    }
    var placeholder by remember {
        mutableStateOf("URL")
    }
    AlertDialog(
        icon = {
            Icon(Icons.Info, null /*this icon isn't necessary to understand the dialog*/)
        },
        title = {
            Text(text = "Join discovery group")
        },
        text = {
            Column {
                Text("Make sure that all devices are on the same server. Then, " +
                        "paste the group's join link here.")
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(placeholder) },
                    isError = placeholder != "URL"
                )
                if (BuildConfig.DEBUG)
                Text("Tip: if the server has an IP address (i.e. a local dev server), " +
                        "the URL won't work here. To join a group on such a server, replace " +
                        "http://(IP address) with https://avoor.app. The app will try to " +
                        "join a group through the server it's connected to regardless of hostname.")
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    placeholder = joinGroupDiscovery(
                        url.toUri(),
                        navigator,
                        onDismiss
                    )
                }
            ) {
                Text(
                    stringResource(R.string.ok)
                )
            }
        }
    )
}

private fun joinGroupDiscovery(uri: Uri?, navigator: Navigator, onDismiss: () -> Unit): String {
    // If there is a URL:
    if (uri != null) {
        Log.d("avr#ma", uri.path.toString())
        // Check if it is a valid Planbot URL
        if (UriUtils.isAvoorUrl(uri)) {
            if (uri.path?.startsWith("/join") == true) {
                // Get the path segments
                val segments = uri.pathSegments
                // Check if there is a group ID in the arguments
                if (segments.size == 2) {
                    onDismiss()
                    // The second argument is the ID - provide it
                    navigator.navigate(Screen.JOIN_GROUP_DISCOVERY, segments[1])
                    return "URL"
                }
            } else return "Unsupported URL"
        } else return "Invalid URL"
    }
    return "No URL"
}

fun showStorage(size: Long, context: Context): String {
    // If the size is at least 1 GB, show the size in GB
    if (size > 1_000_000_000) {
        return context.getString(R.string.storage_gb).format(size.toFloat() / 1_000_000_000)
    }
    // If it is <1 GB, but at least 1 MB, show it in MB
    else if (size > 1_000_000) {
        return context.getString(R.string.storage_mb).format(size.toFloat() / 1_000_000)
    }
    // If it is <1 MB, but at least 1 KB, show it in KB
    else if (size > 1_000) {
        return context.getString(R.string.storage_kb).format(size.toFloat() / 1_000)
    }
    // Otherwise, show it in bytes
    return context.getString(R.string.storage_b).format(size.toFloat())
}
