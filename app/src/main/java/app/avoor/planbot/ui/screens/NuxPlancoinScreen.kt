package app.avoor.planbot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.R
import app.avoor.planbot.ui.components.CreateRewardMenu
import app.avoor.planbot.ui.navigator.Navigator
import app.avoor.planbot.ui.viewmodel.NuxPlancoinViewModel
import app.avoor.planbot.ui.viewmodel.Screen
import app.avoor.symbols.Icons
import app.avoor.symbols.icons.ArrowBack
import app.avoor.symbols.icons.Plancoin
import tech.cataspect.m3x.TwoButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuxPlancoinScreen(
    navigator: Navigator
) {
    val viewModel: NuxPlancoinViewModel = viewModel(factory = NuxPlancoinViewModel.Factory)
    val viewState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val addBottomState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    var addRewardName by remember { mutableStateOf("") }
    var addRewardCost by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.nux_plancoin_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
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
        },
        bottomBar = {
            TwoButtons(
                positiveAction = {
                    viewModel.showAddReward()
                },
                positiveText = stringResource(R.string.plancoin_reward_add),
                negativeAction = {
                    navigator.goBack()
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.nux_plancoin_body))
            Spacer(Modifier.height(32.dp))
            Icon(
                imageVector = Icons.Plancoin,
                modifier = Modifier.size(96.dp),
                contentDescription = null
            )
        }
    }


    // Add reward menu
    if (viewState.showAddReward) {
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
                        viewModel.createReward(addRewardName, addRewardCost) {
                            navigator.navigate(Screen.NUX_END)
                        }
                    },
                    enabled = !addRewardName.isEmpty() && addRewardCost > 0,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Add")
                }
            }
        }
    }
}