package quick.SMS

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.ViewGroup
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.*


class textMessageActivity : AppCompatActivity() {
    internal var helper = DatabaseHelper(this)
    var smsManager = SmsManager.getDefault()
    var Messages: LinkedHashMap<Int, String> = linkedMapOf()
    var receipient_id = 0L
    var recipient_name = "NULL"
    var phoneNumber = "NULL"


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    fun makeCall() {
        /* Calls a phone number */
        if (phoneNumber != "NULL") {
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK /*This line means you don't have to confirm the number
            in the dialer, suprisingly difficult to find online*/
                startActivity(callIntent)
                doAsync {
                    /*Asynchronously add to the log about the call*/
                    insertLog("CALL", "N/A", receipient_id, recipient_name)
                }
            } catch(e: SecurityException) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE))
            }
            /*THIS IS THE CORRECT VERSION*/
        }
    }

    private fun requestPermissions(permissions: Array<String>) {
        /* Request Permission if required */
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.addButton -> {
                makeCall()
                return true
            }
            R.id.action_settings -> {
                popUpAddMessage()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    fun updateTitle(){
        (this).supportActionBar!!.title = recipient_name /*This will programatically set the title, this should be the receipients_name*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val contact = intent.extras.get("contact")
        if (contact is Contact) {
            phoneNumber = contact.numbers[0]
            recipient_name = contact.name
            updateTitle()
            val Helper = DatabaseHelper(this)
            /* Helper.insertData(loadID(), 0L, "Test1")
             incrementID()
             Helper.insertData(loadID(), 0L, "Test1")
             incrementID()
             Helper.insertData(loadID(), 0L, "Test1")
             incrementID() */

            doAsync {
                /*Asynchronously get the text messages for the particular use*/
                val result = Helper.returnAllHashMap(receipient_id)
                uiThread {
                    /*After the ASYNC thread, we should */
                    addButtons(result) /*Add buttons to the layout*/
                    Messages = result /*Save locally for speedy re-establishment of the buttons should the user change the messages*/

                }
            }
        } else {
            println("Error happenede")
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_message)

    }

    fun popUpAddMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add message")
        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Add") {
            dialog, _ ->
            val m_Text = input.text.toString()
            if (m_Text == "Type Message" || m_Text == "") {
                /*Do nothing should the user not enter anything*/
                Toast.makeText(this@textMessageActivity, "Invalid input, Please try again", Toast.LENGTH_SHORT).show()
            } else {
                /*Should */
                addData(receipient_id, m_Text)
                Toast.makeText(this@textMessageActivity, "Message added!!", Toast.LENGTH_SHORT).show()
            }
        }
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
        editor.commit() /*I am using commit as its essential this is done instantly and not in the background, as to allow serialisability*/
    }


    fun insertLog(type: String, message: String, recipient_id: Long, recipient_name: String) {
        /*This function will unconditionally add sent messages into the log - async of course*/
        val Loggyy = DatabaseLog(this)
        doAsync {
            Loggyy.insertData(type, message, recipient_id, recipient_name)
            Loggyy.close()
        }
    }

    @SuppressLint("SetTextI18n")
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
                smsManager.sendTextMessage("07552695272", null, value, null, null)
                Toast.makeText(this@textMessageActivity, "Message sent", Toast.LENGTH_SHORT).show()
                doAsync { insertLog("MESSAGE", value, receipient_id, recipient_name) /*Adds the SMS to the log*/ }
            }
            button_dynamic.setOnLongClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete this message?")
                val input = EditText(this)
                input.setText(button_dynamic.text, TextView.BufferType.EDITABLE)
                builder.setView(input)
                /* Set up the buttons */
                builder.setPositiveButton("Yes") {
                    _, _ ->
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
                }
                builder.setNegativeButton("No") { dialog, _ -> dialog.cancel() }

                builder.show()
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
        incrementID() /*Increment the next usable ID*/
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

    fun makeSound() {
        val sound = true /* Currently set to true, will look up from shared Preference later */

    }

    @SuppressLint("ServiceCast")
    fun vibrate() {
        if (Build.VERSION.SDK_INT > 25) { /*Attempt to not use the deprecated version if possible, if the SDK version is >25, use the newer one*/
            // (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300L)
        } else {
            /*for backward comparability*/
            @Suppress("DEPRECATION")
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(300)
        }
    }
}
