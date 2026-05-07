package app.avoor.planbot.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import app.avoor.symbols.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.BuildConfig
import app.avoor.planbot.OnLifecycleEvent
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.DiscoveryUserListItem
import app.avoor.planbot.ui.components.FailedActionDialog
import app.avoor.planbot.ui.components.OutdatedDialog
import app.avoor.planbot.ui.components.animatedMaterialListItems
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.GroupDiscoveryViewModel
import app.avoor.symbols.icons.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDiscoveryScreen(
    ctx: Activity,
    navigator: Navigator,
    viewModel: GroupDiscoveryViewModel = viewModel(factory = GroupDiscoveryViewModel.Factory)
) {
    val viewState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    viewModel.initIfNecessary()

    if (!viewState.compatible) {
        // TODO if switching to KMP: remove references to activities
        OutdatedDialog {
            try {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")),
                    null
                )
            } catch (e: ActivityNotFoundException) {
                ctx.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")),
                    null
                )
            }
        }
    }

    if (viewState.showActionError) {
        FailedActionDialog(
            mainAction = {viewModel.hideActionError()},
            onDismiss = {viewModel.hideActionError()}
        )
    }

    OnLifecycleEvent { _, event ->
        // when the activity is restarted, join and continue discovery again
        if (event == Lifecycle.Event.ON_RESUME) { viewModel.onResume() }
        // when it is destroyed, disconnect
        else if (event == Lifecycle.Event.ON_DESTROY) { viewModel.disconnect() }
    }

    AnimatedVisibility(visible = viewState.link == null) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // indeterminate (no progress)
            CircularProgressIndicator()
            Text(stringResource(R.string.dsc_group_progress))
        }
    }
    AnimatedVisibility(visible = viewState.link != null) {
        Scaffold (
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.dsc_group_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.leaveGroup()
                            navigator.goBack()
                        }) {
                            Icon(
                                imageVector = Icons.ArrowBack,
                                contentDescription = stringResource(R.string.btn_back)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { padding ->
            LazyColumn(
                Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                item {
                    Text(
                        stringResource(R.string.dsc_group_created),
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {

                                // TODO if switching to KMP: remove references to android stuff
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        ctx.getString(R.string.share_group_link)
                                            .format(viewState.link)
                                    )
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                ctx.startActivity(shareIntent, null)
                            },
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.btn_share))
                        }
                        OutlinedButton(
                            onClick = {
                                copyText(viewState.link!!, ctx)
                            },
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.btn_copy))
                        }
                    }
                }

                item {
                    AnimatedVisibility(
                        viewState.requesters.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            stringResource(R.string.dsc_group_guests),
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                animatedMaterialListItems(items = viewState.requesters) { user ->
                    DiscoveryUserListItem(
                        user = user,
                        approved = false,
                        modifier = Modifier.fillMaxWidth(),
                        onAccept = { viewModel.approve(user) }
                    )
                }

                item {
                    AnimatedVisibility(
                        viewState.members.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            stringResource(R.string.dsc_group_members),
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                animatedMaterialListItems(viewState.members) { user ->
                    DiscoveryUserListItem(
                        user = user,
                        approved = true,
                        modifier = Modifier.fillMaxWidth(),
                        onAccept = null
                    )
                }
            }
        }
    }
}

fun copyText(textCopied:String, context: Context) {
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    // When setting the clipboard text.
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", textCopied))
    // Only show a toast for Android 12 and lower.
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
        Toast.makeText(context, context.getString(R.string.dlg_copied), Toast.LENGTH_SHORT).show()
}
