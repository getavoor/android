package app.avoor.planbot.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.avoor.planbot.AvoorApplication
import app.avoor.planbot.api.models.simplify
import app.avoor.planbot.data.api.PlanbotApiRepository
import app.avoor.planbot.domain.UserManager
import app.avoor.planbot.ui.helper.PPMSession
import app.avoor.planbot.ui.helper.ProfilePictureManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    val userManager: UserManager,
    val apiRepo: PlanbotApiRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    var pfpManager: ProfilePictureManager? = null

    init {
        _uiState.update {
            it.copy(
                user = userManager.getCurrentUser()
            )
        }
    }

    fun setManager(pfpManager: ProfilePictureManager) {
        this.pfpManager = pfpManager
    }

    fun uploadProfilePicture() {
        val sess = PPMSession { uri ->
            if (uri != null) {
                viewModelScope.launch {
                    uploadPfpCallback(uri)
                }
            }
        }
        pfpManager!!.requestProfilePicture(sess)
    }

    private suspend fun uploadPfpCallback(uri: Uri) {
        _uiState.update {
            it.copy(
                progress = true
            )
        }
        val resp = apiRepo.uploadProfilePicture(uri)
        // clean up after the image has been uploaded
        pfpManager!!.cleanUp()
        _uiState.update {
            it.copy(
                progress = false,
                user = resp.user
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userManager.signOut()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AvoorApplication)
                val appCtr = application.container
                ProfileViewModel(
                    userManager = appCtr.userManager,
                    apiRepo = appCtr.planbotApiRepository
                )
            }
        }
    }
}