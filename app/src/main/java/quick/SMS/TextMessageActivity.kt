package quick.SMS

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.telephony.SmsManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_text_message.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.text.InputType
import android.view.Menu
import android.widget.EditText


class textMessageActivity : AppCompatActivity() {
    internal var helper = DatabaseHelper(this)
    var smsManager = SmsManager.getDefault()
    var Messages: LinkedHashMap<Int, String> = linkedMapOf()
    val receipient_id = 0L

    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        val Helper = DatabaseHelper(this)
        Helper.insertData(loadID(), 0L, "Test1")
        incrementID()
        Helper.insertData(loadID(), 0L, "Test1")
        incrementID()
        Helper.insertData(loadID(), 0L, "Test1")
        incrementID()
        (this).supportActionBar!!.title = "Michael Fourman" /*This will programatically set the title, this should be the receipients_name*/

        doAsync {
            /*Asynchronously get the text messages for the particular use*/
            val result = Helper.returnAllHashMap(receipient_id)
            uiThread {
                /*After the ASYNC thread, we should */
                addButtons(result) /*Add buttons to the layout*/
                Messages = result /*Save locally for speedy re-establishment of the buttons should the user change the messages*/

            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_message)

    }
    fun builder() {
        println("we are getting here in the listener")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add message")
        var m_Text = ""
        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Add") { dialog, which -> m_Text = input.text.toString() }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }
    fun loadID(): Int {
        /* Loads a String from Shared Preferences */
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getInt("DBHELPERID", 0) /* DEFAULT AS UNKNOWN */
        return savedValue
    }

    @SuppressLint("ApplySharedPref")
    fun incrementID() {
        /* Saves a String to Shared Preferences */
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt("DBHELPERID", (loadID() + 1))
        editor.commit() /*I am using commit as its essential this is done instantly, as to allow serialisability*/
    }


    fun insertLog() {
        /*This function will unconditionally add sent messages into the log - async of course*/
        doAsync {

        }
    }

    fun callNumber(phoneNumber: String) {
        /*Function calls the number it is passed as a parameter*/
        val dial = Intent()
        dial.action = "android.intent.action.DIAL"
        dial.data = Uri.parse("tel:" + phoneNumber)
        startActivity(dial)
    }


    fun addButtons(textMessages: LinkedHashMap<Int, String>) {
        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout) /* As LinearLayout */
        ll_main.removeAllViews()
        ll_main.removeAllViewsInLayout()
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(1, 35, 1, 0) /* Used to set spaces between each button */
        for ((key, value) in textMessages) { /*creates enough buttons for each song, and says what the buttons do */
            val button_dynamic = Button(this)
            /* setting layout_width and layout_height using layout parameters */
            button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button_dynamic.text = value
            button_dynamic.layoutParams = params
            button_dynamic.id = key
            button_dynamic.setOnClickListener {
                smsManager.sendTextMessage("07552695272", null, "TEST MESSAGE", null, null)
                //smsManager.sendTextMessage(recipient.toString(), null, "TEST MESSAGE", null, null)
            }
            button_dynamic.setOnLongClickListener {
                doAsync {
                    /* Asynchronously delete from database in the background, so not need to worry about it
                     * as will be done in the background */
                    DeleteData(button_dynamic.id.toString())
                }
                try {
                    Messages.remove(button_dynamic.id) /*Remove from local list*/
                } catch (e: NullPointerException) {
                    println("Null pointer occurred, textMessageActivity, line 64, removing from list")
                }
                addButtons(Messages) /*Restablishing the buttons*/
                true
            }
            ll_main.addView(button_dynamic) /*Add button to the layout*/
        }

    }

    fun addData(recipient_id: Long, message: String) {
        val Helper = DatabaseHelper(this)
        vibrate()
        makeSound()
        val databaseID = loadID()
        incrementID() /*Incrememnt the next usable ID*/
        Messages.put(databaseID, message)
        addButtons(Messages) /*Call for the buttons to be updated*/
        doAsync {
            /*Asynchronously get the text messages for the particular use*/
            helper.insertData(databaseID, recipient_id, message)
            helper.close()
        }
    }

    fun UpdateData(id: String, recipient_id: Long, message: String) {
        vibrate()
        makeSound()
        doAsync {
            helper.updateData(id, recipient_id, message)
            helper.close()
        }


    }

    fun DeleteData(id: String) {
        vibrate()
        makeSound()
        doAsync {
            helper.deleteData(id)
            helper.close()
        }
    }


    fun showMessage(title: String, Message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }

    fun makeSound() {
        val sound = true /*Currently set to true, will look up from shared Preference later*/

    }

    fun vibrate() {
        val vibrate = true /*Currently set to true, will look up from shared Preference later*/
        if (vibrate) {
            if (Build.VERSION.SDK_INT > 25) { /*Attempt to not use the deprecated version if possible, if the SDK version is >25, use the newer one*/
                //(getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(300, 10))
            } else {
                /*for backward comparability*/
                @Suppress("DEPRECATION")
                (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300)
            }
        }
    }
}