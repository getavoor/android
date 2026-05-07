package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.SettingsGroupLabel
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.theme.PlanbotTheme
import app.avoor.planbot.ui.viewmodel.StreakViewModel
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.ArrowBack
import app.avoor.symbols.icons.Info
import app.avoor.symbols.icons.Streak

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    navigator: Navigator,
    viewModel: StreakViewModel = viewModel(factory = StreakViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadStreak() },
        modifier = Modifier.fillMaxSize()
    ) {

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
                    IconButton(onClick = { viewModel.showExplainer() }) {
                        Icon(
                            imageVector = Icons.Info,
                            contentDescription = stringResource(R.string.btn_about)
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // current streak
                CurrentStreak(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.currentStreak,
                    subtitle = if (uiState.updatedToday) {
                        stringResource(R.string.streak_updated_today)
                    } else {
                        stringResource(R.string.streak_not_updated)
                    }
                )

                // longest streak
                StatsCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.streak_longest),
                    value = uiState.longestStreak.toString()
                )

                SettingsGroupLabel(stringResource(R.string.streak_freeze_label))

                // streak freeze row
                val nFreezes = minOf(3, uiState.freezeCount)
                val freezeDesc = pluralStringResource(
                    R.plurals.streak_freeze_left,
                    nFreezes
                ).format(nFreezes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = freezeDesc
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StreakFlame(uiState.freezeCount > 3)
                    StreakFlame(uiState.freezeCount > 2)
                    StreakFlame(uiState.freezeCount > 1)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                if (uiState.freezeCount > 0 && !uiState.freezeUsedToday && !uiState.updatedToday) {
                    OutlinedButton(
                        onClick = { viewModel.useStreakFreeze() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.streak_use_freeze))
                    }
                }
            }
        }
    }

    // Info about streaks
    if (uiState.showExplainer) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideExplainer() },
            sheetState = bottomSheetState,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.streak_info_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(R.string.streak_info_desc_l1),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.streak_info_desc_l2),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CurrentStreak(
    value: Int,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom=0.dp)
        )
        Text(
            text = pluralStringResource(
                R.plurals.streak_days,
                value
            ),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = value,
                fontSize = 36.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun StreakFlame(
    active: Boolean
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            Icons.Streak,
            null,
            Modifier
                .size(64.dp)
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
private fun StreakFlamePreview() {
    PlanbotTheme {
        Row {
            StreakFlame(true)
            StreakFlame(false)
        }
    }
}