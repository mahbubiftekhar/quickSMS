package quick.sms.quicksms.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.R

class BugReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exception = intent.extras.get("exception") as Throwable
        BugReportLayout(exception).setContentView(this)
    }
}

private class BugReportLayout(val exception: Throwable) : AnkoComponent<BugReportActivity> {

    override fun createView(ui: AnkoContext<BugReportActivity>) = with(ui) {
        verticalLayout {
            textView(R.string.bug_report_text)
            val extras = editText()
            button("Send") {
                onClick {
                    val contents = """
                        Device Information:
                        Manufacturer: ${android.os.Build.MANUFACTURER}
                        Model: ${android.os.Build.MODEL}
                        API Level: ${android.os.Build.VERSION.SDK_INT}

                        Added by the user:
                        ${extras.text}

                        Exception:
                        Message:
                        ${exception.message}

                        Stack Trace:
                        ${exception.stackTrace.toList()}
                    """.trimIndent()
                    email("quickSMS@iftekhar.co.uk", "quickSMS Bug Report", contents)
                }
            }
        }
    }
}
