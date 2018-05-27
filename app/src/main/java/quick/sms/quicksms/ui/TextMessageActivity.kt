package quick.sms.quicksms.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.telephony.SmsManager
import android.text.InputType
import android.view.*
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.*
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.*
import quick.sms.quicksms.editor
import quick.sms.quicksms.prefs

@Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")
class TextMessageActivity : BaseActivity() {

    private lateinit var contactDB: DatabaseMessages
    private lateinit var logDB: DatabaseLog
    private lateinit var tilesDB: DatabaseTiles
    private lateinit var messages: LinkedHashMap<Int, String>
    private var receipientID: Long = 1L
    private lateinit var recipientName: String
    private lateinit var phoneNumber: String
    private var sound = false
    private val SENT = "SMS_SENT"
    private val DELIVERED = "SMS_DELIVERED"
    private val MAX_SMS_MESSAGE_LENGTH = 160
    lateinit var Spinner: Spinner
    private var tileID = -1
    lateinit var contact: Contact

    private var mAdView: AdView? = null

    private fun returnNoSpaces(input: String): String {
        //This function should return the input with all the spaces
        return input.replace("\\s".toRegex(), "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBarColour()
        setContentView(R.layout.activity_text_message)
        contactDB = DatabaseMessages(this)
        logDB = DatabaseLog(this)
        tilesDB = DatabaseTiles(this)
        contact = intent.extras.get("contact") as Contact
        tileID = intent.getIntExtra("tileID", 0)
        phoneNumber = tilesDB.getPreferedNum(tileID)
        if(phoneNumber.length>1){
            phoneNumber = phoneNumber.removeRange(0, 1) //Remove the leading p
        }
        println(">>> getting in here")
        if (contact is Contact) {
            println(">>> Contact numbers" + contact.numbers)
            recipientName = contact.name
            receipientID = contact.id
            if (phoneNumber == "") {
                //If no prefered number is set, set it as the first number
                phoneNumber = contact.numbers[0]
            }
            updateTitle() //updates the users name on the action bar
            doAsync {
                val result = contactDB.returnAllHashMap(receipientID)
                uiThread {
                    addButtons(result)
                    messages = result
                }
            }
        }
        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107\n")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate your main_menu into the menu
        menuInflater.inflate(R.menu.textmessage_extras, menu)
        // Locate MenuItem with ShareActionProvider
        return true
    }

    private fun getPhoneNumber(): String {
        return returnNoSpaces(phoneNumber)
    }

    override fun extendedOptions(item: MenuItem) = when (item.itemId) {
        R.id.make_call -> {
            try {
                makeCall(returnNoSpaces(phoneNumber))
            } catch (e: Exception) {

            }
            true
        }
        R.id.add_message -> {
            try {
                popUpAddMessage()
            } catch (e: Exception) {

            }
            true
        }
        R.id.info -> {
            //Show the user the current phone number that has been set
            toast("Current phone number set: $phoneNumber")
            true
        }
        R.id.selectNumber -> {
            val phoneNumbers = mutableListOf<String>()
            phoneNumbers.add(returnNoSpaces(phoneNumber))
            println(">>>> all numbers" + contact.numbers)
            for (i in 0 until contact.numbers.size) {
                println(">>>>" + contact.numbers[i])
                if (returnNoSpaces(returnNoSpaces(contact.numbers[i])) != returnNoSpaces(returnNoSpaces(phoneNumber)) && !phoneNumbers.contains(contact.numbers[i])) {
                    /*Only add it if its not the current prefered numbers and doesn't already contain this phone number */
                    phoneNumbers.add(returnNoSpaces(contact.numbers[i]))
                }
            }

            selector("Which phone number would you like to send messages to?", phoneNumbers, { _, z ->
                try {
                    println("<<<<" + returnNoSpaces(phoneNumbers[z]))
                    updatePreferedNum("p" + returnNoSpaces(phoneNumbers[z]))
                } catch (e: Exception) {

                }
            })

            true
        }
        else -> {
            super.extendedOptions(item)
        }
    }

    private fun updatePreferedNum(PreferedNumber: String) {
        println(">>>> new prefered number is $PreferedNumber")
        phoneNumber = PreferedNumber.removeRange(0,1)
        val tiles = DatabaseTiles(this)
        tiles.insertData(receipientID, tileID, PreferedNumber)
    }

    private fun updateTitle() {
        this.supportActionBar?.title = recipientName
    }

    private fun getrecipientId(): Long {
        return receipientID
    }

    private fun popUpAddMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add message")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val mText = input.text.toString()
            if (mText == "Type Message" || mText == "") {
                toast("Invalid input, Please try again")
            } else {
                addData(getrecipientId(), mText)
                toast("Message added successfully")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun addToLog(recipient_id: Long, message: String, receipientName: String, phoneNumber: String) {
        logDB.insertData(recipient_id, message, receipientName, phoneNumber)
    }

    private fun loadID() = prefs.getInt("DBHELPERID", 0)

    fun incrementID() {
        editor.putIntAndCommit("DBHELPERID", loadID() + 1)
    }

    fun addButtons(textMessages: LinkedHashMap<Int, String>) {

        val llMain = findViewById<LinearLayout>(R.id.ll_main_layout)
        llMain.removeAllViews()
        llMain.removeAllViewsInLayout()
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(1, 35, 1, 0)
        for ((key, value) in textMessages) {
            val buttonDynamic = Button(this)
            buttonDynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            buttonDynamic.text = value
            buttonDynamic.layoutParams = params
            buttonDynamic.id = key
            buttonDynamic.setBackgroundResource(R.drawable.rounded_corners)
            buttonDynamic.setOnClickListener {
                fun sendMessage() {
                    try {
                        val sent = "SMS_SENT"
                        val piSent = PendingIntent.getBroadcast(applicationContext, 0, Intent(SENT), 0)
                        val piDelivered = PendingIntent.getBroadcast(applicationContext, 0, Intent(DELIVERED), 0)
                        registerReceiver(object : BroadcastReceiver() {
                            override fun onReceive(arg0: Context, arg1: Intent) {
                                if (resultCode === Activity.RESULT_OK) {
                                    doAsync {
                                        addToLog(receipientID, buttonDynamic.text as String, recipientName, phoneNumber)
                                    }
                                    Toast.makeText(baseContext, "SMS sent successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    toast("Sorry, couldn't send SMS")
                                }
                            }
                        }, IntentFilter(sent))
                        val smsManager = SmsManager.getDefault()
                        val length = (buttonDynamic.text as String).length
                        return if (length > MAX_SMS_MESSAGE_LENGTH) {
                            val messagelist = smsManager.divideMessage(buttonDynamic.text as String)
                            smsManager.sendMultipartTextMessage(phoneNumber, null, messagelist, null, null)

                        } else {
                            smsManager.sendTextMessage(phoneNumber, null, buttonDynamic.text as String, piSent, piDelivered)
                        }
                    } catch (e: Exception) {
                        toast("Sorry, Message not sent")
                    }

                    vibrate()
                    makeSound()
                    doAsync {
                        if (receipientID != 1L) {
                            //Here we are adding to the log, checking for not 1L to not add during development
                            addToLog(receipientID, buttonDynamic.text as String, recipientName, phoneNumber)
                        }
                    }
                }
                if (doubleCheckBool()) {
                    //User wishes for double check
                    alert(value) {
                        title = "Are you sure you want to send the message"
                        positiveButton("Send") {
                            sendMessage()
                        }
                        negativeButton("Cancel") {

                        }
                    }.show()
                } else {
                    //The user has doubleCheck off, so just send anyways. Whats the worse than can happen?
                    sendMessage()
                }
            }
            buttonDynamic.setOnLongClickListener {
                alert(value) {
                    title = "What would you like to do to this message?"
                    positiveButton("Delete") {
                        doAsync {
                            deleteData(buttonDynamic.id.toString())
                        }
                        //Basically during testing I passed invalid removes at some point by accident and I got fed up on nullpointers
                        try {
                            messages.remove(buttonDynamic.id)
                        } catch (e: NullPointerException) {
                            println("NullPointerException, TextMessageActivity")
                        }
                        addButtons(messages)
                    }
                    negativeButton("Edit") {
                        //buttonDynamic.text IS THE TEXT
                        //buttonDynamic.id.toString() IS THE button id
                        editDataBuilder(buttonDynamic.text as String, buttonDynamic.id.toString())
                    }
                }.show()
                true
            }
            llMain.addView(buttonDynamic)
        }
    }

    fun editDataBuilder(text: String, buttonID: String) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.setText(text, TextView.BufferType.EDITABLE)
        builder.setView(input)
        builder.setPositiveButton("Save changes") { _, _ ->
            val mText = input.text.toString()
            updateData(buttonID, getrecipientId(), mText)
        }
        builder.setNegativeButton("Discard changes") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
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
        if (vibrateBool()) {
            vibrate()
        }
        if (soundBool()) {
            makeSound()
        }
        messages[id.toInt()] = message
        addButtons(messages)
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