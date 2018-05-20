package quick.sms.quicksms.ui

import Util.Android.editor
import Util.Android.prefs
import Util.Android.putIntAndCommit
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.telephony.SmsManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseMessages

@Suppress("DEPRECATION")
class TextMessageActivity : BaseActivity() {

    private lateinit var contactDB: DatabaseMessages
    private lateinit var tilesDB: DatabaseLog
    private val smsManager = SmsManager.getDefault()
    private lateinit var messages: LinkedHashMap<Int, String>
    private val recipientId = 0L
    private var receipientID: Long = 1L
    private lateinit var recipientName: String
    private lateinit var phoneNumber: String
    private var sound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_message)
        // reading settings preferences
        contactDB = DatabaseMessages(this)
        tilesDB = DatabaseLog(this)
        val contact = intent.extras.get("contact")
        if (contact is Contact) {
            phoneNumber = contact.numbers[0]
            recipientName = contact.name
            updateTitle()
            doAsync {
                val result = contactDB.returnAllHashMap(recipientId)
                uiThread {
                    addButtons(result)
                    messages = result
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate your main_menu into the menu
        menuInflater.inflate(R.menu.textmessageactivity, menu)
        // Locate MenuItem with ShareActionProvider
        return true
    }

    override fun extendedOptions(item: MenuItem) = when (item.itemId) {
        R.id.make_call -> {
            println(1)
            makeCall(phoneNumber)
            true
        }
        R.id.add_message -> {
            println(2)
            popUpAddMessage()
            true
        }
        else -> {
            println(3)
            super.extendedOptions(item)
        }
    }

    private fun updateTitle() {
        this.supportActionBar?.title = recipientName
    }

    private fun popUpAddMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add message")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val mText = input.text.toString()
            if (mText == "Type Message" || mText == "") {
                toast("Invalid input, Please try again")
            } else {
                addData(recipientId, mText)
                toast("MESSAGE ADDED")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun addToLog(recipient_id: Long, message: String, receipientName: String, phoneNumber: String) {
        tilesDB.insertData(recipient_id, message, receipientName, phoneNumber)
    }

    private fun loadID() = prefs.getInt("DBHELPERID", 0)
    fun incrementID() {
        editor.putIntAndCommit("DBHELPERID", loadID() + 1)
    }

    fun addButtons(textMessages: LinkedHashMap<Int, String>) {
        val llMain = findViewById<LinearLayout>(R.id.ll_main_layout)
        llMain.removeAllViews()
        llMain.removeAllViewsInLayout()
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(1, 35, 1, 0)
        for ((key, value) in textMessages) {
            val buttonDynamic = Button(this)
            buttonDynamic.layoutParams = LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
            buttonDynamic.text = value
            buttonDynamic.layoutParams = params
            buttonDynamic.id = key
            buttonDynamic.setOnClickListener {
                smsManager.sendTextMessage(phoneNumber, null, value,
                        null, null)
                this@TextMessageActivity.toast("Message sent")
                doAsync {
                    if (receipientID != 1L) {
                        //Here we are adding to the log, checking for not 1L to not add during development
                        addToLog(receipientID, buttonDynamic.text as String, recipientName, phoneNumber)
                    }
                }
            }
            buttonDynamic.setOnLongClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete this message?")
                val input = EditText(this)
                input.setText(buttonDynamic.text, TextView.BufferType.EDITABLE)
                builder.setView(input)
                builder.setPositiveButton("Yes") { _, _ ->
                    doAsync {
                        deleteData(buttonDynamic.id.toString())
                    }
                    // TODO: Why does this throw?
                    //Basically during testing I passed invalid removes at some point by accident and I got fed up on nullpointers
                    try {
                        messages.remove(buttonDynamic.id)
                    } catch (e: NullPointerException) {
                        println("NullPointerException, TextMessageActivity")
                    }
                    addButtons(messages)
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()
                true
            }
            llMain.addView(buttonDynamic)
        }
    }

    fun addData(recipientId: Long, message: String) {
        vibrate()
        makeSound()
        val databaseID = loadID()
        incrementID()
        messages[databaseID] = message
        addButtons(messages)
        doAsync {
            contactDB.insertData(databaseID, recipientId, message)
        }
    }

    fun updateData(id: String, recipientId: Long, message: String) {
        vibrate()
        makeSound()
        doAsync {
            contactDB.updateData(id, recipientId, message)
        }
    }

    private fun deleteData(id: String) {
        vibrate()
        makeSound()
        doAsync {
            contactDB.deleteData(id)
        }
    }

    private fun makeSound() {
        if (sound) {
            try {
                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(applicationContext, notification)
                r.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(300)
        }
    }
}
