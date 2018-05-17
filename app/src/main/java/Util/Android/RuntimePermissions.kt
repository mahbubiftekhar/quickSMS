package Util.Android

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

fun getPermissions(activity : Activity, permissions : Array<String>) : Boolean {
    val requiredPermissions = permissions.filter {
        val permissionGranted = ContextCompat.checkSelfPermission(activity, it)
        permissionGranted != PackageManager.PERMISSION_GRANTED
    }.toTypedArray()
    if ( ! requiredPermissions.isEmpty()) {
        ActivityCompat.requestPermissions(activity, requiredPermissions, 1)
        return false
    }
    return true
}