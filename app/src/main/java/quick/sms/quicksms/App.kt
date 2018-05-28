package quick.sms.quicksms

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import org.jetbrains.anko.bundleOf

// From: https://nfrolov.wordpress.com/2014/07/12/android-using-context-statically-and-in-singletons/

// Makes the provided properties globally accessible
val context by lazy { App.context }
val prefs by lazy { App.prefs }
val editor by lazy { App.editor }
val settings by lazy { App.settings }

class App : Application() {

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

    private fun isUIThread() = Looper.getMainLooper().thread == Thread.currentThread()

    private fun spawnFallback(exception: Throwable) {
        val intent = Intent()
        intent.action = "quick.sms.quicksms.UNCAUGHT_EXCEPTION"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtras(bundleOf("exception" to exception))
        println("Starting Fallback")
        startActivity(intent)
    }

    // Get a static reference to the Application Context
    // (Application's onCreate is only called once before anything else)
    override fun onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // Setup a global uncaught exception handler (Adapted from: https://stackoverflow.com/questions/19897628/need-to-handle-uncaught-exceptions-and-send-to-log-file)
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            println("Caught")
            if (isUIThread()) {
                spawnFallback(exception)
            } else {
                Handler(Looper.getMainLooper()).post { spawnFallback(exception) }
            }
            System.exit(1)
        }
    }
}