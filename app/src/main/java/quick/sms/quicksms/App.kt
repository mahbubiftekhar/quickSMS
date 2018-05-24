package quick.sms.quicksms

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager

// From: https://nfrolov.wordpress.com/2014/07/12/android-using-context-statically-and-in-singletons/

// Makes the provided properties globally accessible
val context by lazy { App.context }
val prefs by lazy { App.prefs }
val editor by lazy { App.editor }
val settings by lazy { App.settings }

abstract class App : Application() {

    companion object {
        private val PREF_FILE = "shared_prefs"
        lateinit var context : Context
            private set
        val prefs by lazy { context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE) }
        val editor by lazy { prefs.edit() }
        val settings by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
        val connectivityManager by lazy {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }

    // Get a static reference to the Application Context
    // (Application's onCreate is only called once before anything else)
    override fun onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}