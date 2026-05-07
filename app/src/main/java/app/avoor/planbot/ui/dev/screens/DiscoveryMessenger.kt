package app.avoor.planbot.ui.dev.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import app.avoor.symbols.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import app.avoor.planbot.ui.dev.viewmodel.DiscoveryMessengerVM
import app.avoor.planbot.ui.dev.viewmodel.RawDiscoveryReqRes
import app.avoor.symbols.icons.ArrowBack
import app.avoor.symbols.icons.ArrowForward

val argTypes = arrayOf(
    "Integer",
    "String",
    "Access token",
    "Refresh token",
    "None"
)

@Composable
fun DiscoveryMessenger(
    viewmodel: DiscoveryMessengerVM = viewModel(factory = DiscoveryMessengerVM.Factory)
) {

    val vmState by viewmodel.uiState.collectAsState()

    var cmd by remember {
        mutableStateOf("")
    }
    var type by remember {
        mutableStateOf("")
    }
    var arg by remember {
        mutableStateOf("")
    }
    LazyColumn(
        Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item {
            Card(
                Modifier.padding(8.dp).windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        value = cmd,
                        onValueChange = {cmd = it},
                        label = { Text(text = "Command")}
                    )
                    TypeDropdown(selectedText = type) {
                        type = it
                    }
                    if (!type.endsWith("token") && type != "None") {
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            value = arg,
                            onValueChange = {arg = it},
                            label = { Text(text = "Argument")}
                        )
                    }
                    else {
                        if (type == "Access token") {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                onClick = {
                                    viewmodel.newToken()
                                }
                            ) {
                                Text("Check login state")
                            }
                            Text(
                                "If the access token has expired (i.e. the server responds with response code 403 to commands that " +
                                        "require authorization), checking the login state will generate a new access token."
                            )
                        }
                        arg = when (type) {
                            "Access token" -> "_xt"
                            "None" -> "_xx"
                            else -> "_rt"
                        }
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = {
                            viewmodel.send(cmd, arg, type)
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        }
        items (vmState.history) {
            ReqResItem(it)
        }
    }
}

@Composable
fun ReqResItem(it: RawDiscoveryReqRes) {
    Column(
        Modifier.padding(8.dp)
    ) {
        Text(
            if (it.isRequest) "Outgoing" else "Incoming",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(it.command, fontFamily = FontFamily.Monospace)
        if (it.argument != "(none)") {
            Text("Argument:")
            Text(it.argument, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun TypeDropdown(
    selectedText: String,
    selectedTextChanged: ((String) -> Unit)
) {

    var mExpanded by remember {
        mutableStateOf(false)
    }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.ArrowBack
    else
        Icons.ArrowForward

    Column(Modifier.padding(8.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = selectedText,
            onValueChange = selectedTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = {Text("Type")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
        ) {
            argTypes.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedTextChanged(label)
                    mExpanded = false
                }, text = {
                    Text(text = label)
                })
            }
        }
    }
}