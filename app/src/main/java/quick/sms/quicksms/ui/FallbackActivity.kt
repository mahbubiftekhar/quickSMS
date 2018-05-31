package quick.sms.quicksms.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseMessages
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.context

class FallbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exception = intent.extras.get("exception") as Throwable
        FallbackLayout(::restart, ::resetApp, { sendReport(exception) }).setContentView(this)
    }

    private fun restart() {
        val i = baseContext.packageManager
                .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    private fun sendReport(exception: Throwable) {
        startActivity<BugReportActivity>("exception" to exception)
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
                onClick {
                    alert("This will remove all data such as textMessages and settings") {
                        title = "Are you sure?"
                        positiveButton("Yes") {
                            reset()

                        }
                        negativeButton("No, Cancel") {

                        }
                    }
                }
            }
            button(R.string.send_bug_report) {
                onClick { send() }
            }
        }
    }
}