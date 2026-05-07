package app.avoor.planbot.ui.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

fun startEmailApp(parentActivity: Activity) {
    val resolveIntent = Intent(Intent.ACTION_SENDTO)
    resolveIntent.setData(Uri.parse("mailto:meow@avoor.app"))
    val resolveInfoList = parentActivity.packageManager.queryIntentActivities(resolveIntent, PackageManager.MATCH_DEFAULT_ONLY)
    val intents = resolveInfoList.mapNotNull {
        info -> parentActivity.packageManager.getLaunchIntentForPackage(info.activityInfo.packageName)
    }.toMutableList()
    if(intents.isEmpty()) {
        //no mail client installed. Prompt user or throw exception
    } else if(intents.size == 1) {
        //one mail client installed, start that
        parentActivity.startActivity(intents.first())
    } else {
        //multiple mail clients installed, let user choose which one to start
        val chooser = Intent(Intent.ACTION_CHOOSER)
        chooser.putExtra(Intent.EXTRA_INTENT, intents.removeAt(0))
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        parentActivity.startActivity(chooser)
    }
}