package app.avoor.planbot.ui.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


/**
 * A profile picture session.
 */
class PPMSession(
    /**
     * The callback to call at the end of this session.
     *
     * The argument is the user's selected and cropped profile picture.
     * If the URI is null, either:
     * - the user closed the media picker without selecting a photo,
     * - the user closed the cropping window.
     */
    val callback: (Uri?) -> Unit,
)

/**
 * Helper class that manages requesting and cropping a profile picture.
 */
class ProfilePictureManager(
    val activity: ComponentActivity,
) {

    /**
     * An ActivityResultLauncher that launches a media picker.
     */
    private var mediaPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null

    /**
     * An ActivityResultLauncher that launches the cropping activity and waits for a result.
     */
    private var cropResultLauncher: ActivityResultLauncher<Intent>? = null

    init {
        // Registers a photo picker activity launcher
        mediaPickerLauncher = activity.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            pickerCallback(uri)
        }
        // Register a crop activity launcher
        cropResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("PPM", "Done!")
                // Get the resulting Intent
                val intent: Intent? = result.data
                // If there is no intent, end the session
                if (intent == null) {
                    endSession()
                }
                else {
                    // Retrieve the Intent's data (the URI)
                    val uri = intent.data
                    // End the session with that URI
                    endSession(uri)
                }
            }
            // If it's cancelled:
            else {
                Log.d("PPM", "Cancelled!")
                // End the session without specifying a URI
                endSession()
            }
        }
    }

    /**
     * The current session.
     */
    private var activeSession: PPMSession? = null

    /**
     * Ends the current session.
     *
     * @param uri the URI to call the callback with.
     */
    private fun endSession(uri: Uri? = null) {
        // If there is a session:
        if (activeSession != null) {
            // Notify the session of the URI
            activeSession!!.callback.invoke(uri)
            // Clear the session
            activeSession = null
        }
    }

    /**
     * Called when a photo is picked or the media picker is closed.
     */
    private fun pickerCallback(uri: Uri?) {
        // If there is no session, return
        if (activeSession == null) {
            Log.d("PPM", "Returning as there is no session.")
            return
        }
        if (uri != null) {
            Log.d("PPM", "Selected URI: $uri")
            // Start cropping with this URI
            startCrop(uri)
        } else {
            Log.d("PPM", "No media selected")
            // End the session without specifying a URI
            endSession()
        }
    }

    private fun startCrop(uri: Uri) {
        // Copy the image to the app's files directory
        val tempFile = copyImage(uri)
        // Get the temporary image's URI
        val tempUri = FileProvider.getUriForFile(activity,"app.avoor.planbot.imageprovider",tempFile)
        // Create and start an intent to crop this image
        val intent = getCropIntent(tempUri)
        cropResultLauncher!!.launch(intent)
    }

    private fun getCropIntent(uri: Uri): Intent {
        // Create an output URI
        val tempFile = File(activity.filesDir, "out.jpg")
        val tempUri = FileProvider.getUriForFile(activity,"app.avoor.planbot.imageprovider",tempFile)
        // Create an Intent
        val intent = Intent("com.android.camera.action.CROP")
        intent.setData(uri)
        intent.putExtra("crop", "true")
        // Give permission to photo editors to open the photo
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        // Set the aspect ratio to 1:1 (square)
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // Set the resolution to 500x500
        intent.putExtra("outputX", 500)
        intent.putExtra("outputY", 500)
        // Tell it where to save the file
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)

        // Get all apps that can crop a JPEG file
        val resInfoList: List<ResolveInfo> = activity.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        // Grant access to the temp output file to all of them
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            activity.grantUriPermission(
                packageName,
                tempUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        // Return the intent
        return intent
    }

    private fun copyImage(uri: Uri): File {
        val inputStream = activity.contentResolver.openInputStream(uri)
        val tempFile = File(activity.filesDir, "temp_image.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }

    /**
     * Request a profile picture.
     *
     * @throws SecurityException if a session is already running.
     * @throws NullPointerException if there is no media picker launcher.
     */
    fun requestProfilePicture(session: PPMSession) {
        // Throw an exception if there already is a running session
        if (activeSession != null) {
            throw SecurityException("A session is already running.")
        }
        activeSession = session
        // Request media
        mediaPickerLauncher!!.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /**
     * Delete any temporary files used in the profile picture process.
     */
    fun cleanUp() {
        // delete temporary image
        val tempFile = File(activity.filesDir, "temp_image.jpg")
        if (tempFile.exists()) {
            tempFile.delete()
        }
        // delete out.jpg (crop output)
        val cropFile = File(activity.filesDir, "out.jpg")
        if (cropFile.exists()) {
            cropFile.delete()
        }
    }
}