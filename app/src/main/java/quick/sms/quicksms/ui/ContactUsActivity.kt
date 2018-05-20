package quick.sms.quicksms.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import quick.sms.quicksms.R
import android.content.Intent
import android.text.TextUtils
import android.widget.EditText
import android.app.Activity
import android.view.View
import android.widget.Button
import java.util.regex.Pattern


class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)


        val your_name = findViewById<View>(R.id.your_name) as EditText
        val your_email = findViewById<View>(R.id.your_email) as EditText
        val your_subject = findViewById<View>(R.id.your_subject) as EditText
        val your_message = findViewById<View>(R.id.your_message) as EditText

        fun isValidEmail(email:String):Boolean {
            val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            val pattern = Pattern.compile(EMAIL_PATTERN)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        val email = findViewById<View>(R.id.post_message) as Button
        email.setOnClickListener(View.OnClickListener {
            val name = your_name.text.toString()
            val email = your_email.text.toString()
            val subject = your_subject.text.toString()
            val message = your_message.text.toString()
            if (TextUtils.isEmpty(name)) {
                your_name.error = "Enter Your Name"
                your_name.requestFocus()
                return@OnClickListener
            }

            var onError: Boolean? = false
            if (!isValidEmail(email)) {
                onError = true
                your_email.error = "Invalid Email"
                return@OnClickListener
            }

            if (TextUtils.isEmpty(subject)) {
                your_subject.error = "Enter Your Subject"
                your_subject.requestFocus()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(message)) {
                your_message.error = "Enter Your Message"
                your_message.requestFocus()
                return@OnClickListener
            }

            val sendEmail = Intent(android.content.Intent.ACTION_SEND)

            /* Fill it with Data */
            sendEmail.type = "plain/text"
            sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("quickSMS@iftekhar.co.uk"))
            sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                    "name:" + name + '\n'.toString() + "Email ID:" + email + '\n'.toString() + "Message:" + '\n'.toString() + message)

            /* Send it off to the Activity-Chooser */
            startActivity(Intent.createChooser(sendEmail, "Send mail..."))
        })
    }


}
