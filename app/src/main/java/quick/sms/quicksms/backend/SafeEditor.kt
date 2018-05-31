@file:Suppress("unused")

package quick.sms.quicksms.backend

import android.content.SharedPreferences

fun SharedPreferences.Editor.putBooleanAndCommit(key: String, data: Boolean) {
    this.putBoolean(key, data)
    this.commit()
}

fun SharedPreferences.Editor.putFloatAndCommit(key: String, data: Float) {
    this.putFloat(key, data)
    this.commit()
}

fun SharedPreferences.Editor.putIntAndCommit(key: String, data: Int) {
    this.putInt(key, data)
    this.commit()
}

fun SharedPreferences.Editor.putLongAndCommit(key: String, data: Long) {
    this.putLong(key, data)
    this.commit()
}

fun SharedPreferences.Editor.putStringAndCommit(key: String, data: String) {
    this.putString(key, data)
    this.commit()
}

fun SharedPreferences.Editor.putStringSetAndCommit(key: String, data: Set<String>) {
    this.putStringSet(key, data)
    this.commit()
}

fun SharedPreferences.Editor.removeAndCommit(key: String) {
    this.remove(key)
    this.commit()
}