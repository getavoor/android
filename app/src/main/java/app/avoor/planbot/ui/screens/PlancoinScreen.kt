package app.avoor.planbot.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.api.models.PlancoinReward
import app.avoor.planbot.ui.components.CreateRewardMenu
import app.avoor.planbot.ui.components.RewardBody
import app.avoor.planbot.ui.components.SettingsGroupLabel
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.PlancoinMessage
import app.avoor.planbot.ui.viewmodel.PlancoinViewModel
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.Add
import app.avoor.symbols.icons.ArrowBack
import app.avoor.symbols.icons.Info
import tech.cataspect.m3x.MaterialListStyling
import tech.cataspect.m3x.materialListItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlancoinScreen(
    navigator: Navigator,
    viewModel: PlancoinViewModel = viewModel(factory = PlancoinViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val addBottomState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val editBottomState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    var showRewardContextMenu by remember { mutableStateOf(false) }
    var showRewardEditor by remember { mutableStateOf(false) }
    var selectedReward by remember { mutableStateOf<PlancoinReward?>(null) }

    var addRewardName by remember { mutableStateOf("") }
    var addRewardCost by remember { mutableIntStateOf(0) }

    val haptics = LocalHapticFeedback.current
    val resources = LocalResources.current

    // Show message in snackbar
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            when (message) {
                PlancoinMessage.REWARD_BOUGHT -> {
                    snackbarHostState.showSnackbar(
                        resources.getString(R.string.plancoin_reward_bought)
                    )
                }

                PlancoinMessage.REWARD_ERROR_INSUFFICIENT_FUNDS -> {
                    snackbarHostState.showSnackbar(
                        resources.getString(R.string.plancoin_reward_insufficient_funds)
                    )
                }

                PlancoinMessage.REWARD_DELETED -> {
                    snackbarHostState.showSnackbar(
                        resources.getString(R.string.plancoin_reward_deleted)
                    )
                }

                PlancoinMessage.NO_INTERNET -> {
                    snackbarHostState.showSnackbar(
                        resources.getString(R.string.no_internet)
                    )
                }

                PlancoinMessage.GENERAL_ERROR -> {
                    // check if there is an error description
                    val error = uiState.errorDesc
                    if (error != null) {
                        snackbarHostState.showSnackbar(error)
                    } else {
                        snackbarHostState.showSnackbar(
                            resources.getString(R.string.unknown_error)
                        )
                    }
                }
            }
            viewModel.clearMessage()
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = uiState.plancoins.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 72.sp,
                        modifier = Modifier.padding(bottom=0.dp)
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.plancoins,
                            uiState.plancoins
                        ).lowercase(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                // user-defined rewards
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SettingsGroupLabel(
                            stringResource(R.string.plancoin_reward_group)
                        )
                        IconButton(
                            onClick = {
                                viewModel.showAddReward()
                            }
                        ) {
                            Icon(
                                Icons.Add,
                                stringResource(R.string.plancoin_reward_add),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                materialListItems(
                    uiState.rewards ?: listOf()
                ) {
                    RewardBody(
                        it,
                        uiState.plancoins >= it.cost,
                        Modifier.combinedClickable (
                            onClick = {},
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedReward = it
                                showRewardContextMenu = true
                            },
                            onLongClickLabel = stringResource(R.string.plancoin_reward_edit_desc)
                        )
                    ) {
                        viewModel.buy(it)
                    }
                }
            }
        }
    }

    // Add reward menu
    if (uiState.showAddReward) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideAddReward() },
            sheetState = addBottomState,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CreateRewardMenu(
                    name = addRewardName,
                    onNameChange = {addRewardName = it},
                    cost = addRewardCost,
                    onCostChange = {addRewardCost = it}
                )
                Button(
                    onClick = {
                        viewModel.createReward(addRewardName, addRewardCost)
                    },
                    enabled = !addRewardName.isEmpty() && addRewardCost > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Add")
                }
            }
        }
    }

    // Edit reward screen
    if (showRewardContextMenu) {
        ModalBottomSheet(
            onDismissRequest = { showRewardContextMenu = false },
            sheetState = editBottomState,
        ) {
            selectedReward?.let { reward ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(MaterialListStyling.Default.cornerRadius)
                    ) {
                        RewardBody(
                            reward,
                            false
                        ) {}
                    }
                    Text(
                        "Edit reward",
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    showRewardEditor = true
                                }
                            )
                            .padding(8.dp)
                    )
                    Text(
                        "Delete reward",
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    showRewardContextMenu = false
                                    viewModel.deleteReward(reward)
                                }
                            )
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}