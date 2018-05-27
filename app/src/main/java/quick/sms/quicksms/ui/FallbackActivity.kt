package quick.sms.quicksms.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseMessages
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.context

class FallbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exception = intent.extras.get("exception") as Throwable
        FallbackLayout(::restart, ::resetApp, {}).setContentView(this)
    }

    private fun restart() {
        val i = baseContext.packageManager
                .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    @SuppressLint("ApplySharedPref")
    private fun resetApp() {
        val contactDB = DatabaseMessages(this)
        val tilesDB = DatabaseTiles(this)
        val log = DatabaseLog(this)
        contactDB.deleteEntireDB()
        tilesDB.deleteEntireDB()
        log.deleteEntireDB()
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit() //Resetting shared preferences
        restart()
    }
}

private class FallbackLayout(val restart: () -> Unit, val reset: () -> Unit, val send: () -> Unit)
    : AnkoComponent<FallbackActivity> {

    override fun createView(ui: AnkoContext<FallbackActivity>) = with(ui) {
        verticalLayout {
            textView(R.string.fallback_text)
            button(R.string.restart_app) {
                onClick { restart() }
            }
            button(R.string.reset_app) {
                onClick { reset() }
            }
            button(R.string.send_bug_report) {
                onClick { send() }
            }
        }
    }
}