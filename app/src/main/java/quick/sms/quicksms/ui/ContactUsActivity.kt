package quick.sms.quicksms.ui

import android.os.Bundle
import quick.sms.quicksms.R
import android.content.Intent
import android.text.TextUtils
import android.widget.EditText
import android.view.View
import android.widget.Button
import quick.sms.quicksms.BaseActivity
import java.util.regex.Pattern


class ContactUsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBarColour()
        setContentView(R.layout.activity_contact_us)


        val yourName = findViewById<View>(R.id.your_name) as EditText
        val yourEmail = findViewById<View>(R.id.your_email) as EditText
        val yourSubject = findViewById<View>(R.id.your_subject) as EditText
        val yourMessage = findViewById<View>(R.id.your_message) as EditText

        fun isValidEmail(email: String): Boolean {
            val emailPattern = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            val pattern = Pattern.compile(emailPattern)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        val email = findViewById<View>(R.id.post_message) as Button
        email.setOnClickListener(View.OnClickListener {
            val name = yourName.text.toString()
            val email2 = yourEmail.text.toString()
            val subject = yourSubject.text.toString()
            val message = yourMessage.text.toString()
            if (TextUtils.isEmpty(name)) {
                yourName.error = "Enter Your Name"
                yourName.requestFocus()
                return@OnClickListener
            }

            if (!isValidEmail(email2)) {
                yourEmail.error = "Invalid Email"
                return@OnClickListener
            }

            if (TextUtils.isEmpty(message)) {
                yourMessage.error = "Enter Your Message"
                yourMessage.requestFocus()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(subject)) {
                yourSubject.error = "Enter Your Subject"
                yourSubject.requestFocus()
                return@OnClickListener
            }



            val sendEmail = Intent(android.content.Intent.ACTION_SEND)

            /* Fill it with Data */
            sendEmail.type = "plain/text"
            sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("quickSMS@iftekhar.co.uk"))
            sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,
                    "name:" + name + '\n'.toString() + "Email ID:" + email2 + '\n'.toString() + "Message:" + '\n'.toString() + message)

            /* Send it off to the Activity-Chooser */
            startActivity(Intent.createChooser(sendEmail, "Send mail...")) //Starts the activity to send an email to us
        })
    }


}
