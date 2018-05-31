@file:Suppress("unused")

package quick.sms.quicksms.backend

import android.content.SharedPreferences

fun SharedPreferences.Editor.putBooleanAndCommit(key: String, data: Boolean) {
    putBoolean(key, data)
    commit()
}

fun SharedPreferences.Editor.putFloatAndCommit(key: String, data: Float) {
    putFloat(key, data)
    commit()
}

fun SharedPreferences.Editor.putIntAndCommit(key: String, data: Int) {
    putInt(key, data)
    commit()
}

fun SharedPreferences.Editor.putLongAndCommit(key: String, data: Long) {
    putLong(key, data)
    commit()
}

fun SharedPreferences.Editor.putStringAndCommit(key: String, data: String) {
    putString(key, data)
    commit()
}

fun SharedPreferences.Editor.putStringSetAndCommit(key: String, data: Set<String>) {
    putStringSet(key, data)
    commit()
}

fun SharedPreferences.Editor.removeAndCommit(key: String) {
    remove(key)
    commit()
}

fun SharedPreferences.Editor.clearAndCommit() {
    clear()
    commit()
}