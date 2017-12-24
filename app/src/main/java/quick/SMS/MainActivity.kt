package quick.SMS

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList

/*
 * https://antonioleiva.com/databases-anko-kotlin/
 * The above link is to do database's, which I think we will need to save and retain each contacts
 * common text messages - which will have to be defined by the user
 *
 * http://sonevalley.blogspot.co.uk/2015/12/retrieving-list-of-contacts-in-android.html
 * A link explaining how to pull up the contacts from the user, in java but Kotlin is interoperable
 *
 * https://www.androidhive.info/2017/07/android-implementing-preferences-settings-screen/
 * Toggle settings for the user
 *
 * http://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
 * Drap and drop link above
 *
 */
class MainActivity : AppCompatActivity() {

    /* The idea to this one was to keep track easily of which tab is selected */
    private var TAG = "SMS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Request required permissions
        requestPermissions(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS))
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        tile_1.setOnClickListener {
            onClick(1)
        }
        tile_2.setOnClickListener {
            onClick(2)
        }
        tile_3.setOnClickListener {
            onClick(3)
        }
        tile_4.setOnClickListener {
            onClick(4)
        }
        tile_5.setOnClickListener {
            onClick(5)
        }
        tile_6.setOnClickListener {
            onClick(6)
        }
        tile_7.setOnClickListener {
            onClick(7)
        }
        tile_8.setOnClickListener {
            onClick(8)
        }
        tile_9.setOnClickListener {
            onClick(9)
        }
        tile_10.setOnClickListener {
            onClick(10)
        }
    }

    fun getContacts(ctx : Context) : List<Contact> {
        // Query into one of androids internal databases, returns a cursor which is a R/W view into
        // the returned rows
        val contacts = ctx.contentResolver.query(ContactsContract.Contacts.CONTENT_URI
                , null, null, null, null)

        // The row parser gets each row in turn from the cursor and can turn it into an object, the
        // result is then a list of these objects (I'm not sure why but it only works when the
        // parameter is called object)
        val parsedContacts = contacts.parseList(object : MapRowParser<NullableContact> {
            override fun parseRow(columns: Map<String, Any?>): NullableContact {
                return NullableContact(columns[ContactsContract.Contacts.DISPLAY_NAME] as? String,
                                       columns[ContactsContract.Contacts.PHOTO_URI] as? String)
            }
        })

        // Remove Contacts with null names, sort by name and convert to null safe Contacts
        return parsedContacts
                .filter { it.name != null }
                .sortedBy { it.name }
                .map { Contact(it.name!!, it.image) }
    }

    fun callNumber(phoneNumber: String) {
        // Calls a phone number
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse(Manifest.permission.CALL_PHONE)
        try{startActivity(callIntent)}
        catch(e:SecurityException){
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE))

        }
    }
    fun loadString(key: String): String {
        /* Loads a String from Shared Preferences */
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getString(key, "UNKNOWN") /*DEFAULT AS UNKNOWN*/
        return savedValue
    }
    fun saveString(key: String, value: String) {
        /* Saves a String to Shared Preferences */
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    private fun requestPermissions(permissions: Array<String>) {
        /* Request Permission if required */
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.SMS -> {
                TAG = "SMS" /* Setting tag parameter appropriately */
                /* QUICK SMS */
                return@OnNavigationItemSelectedListener true
            }
            R.id.CALL -> {
                TAG = "CALL" /* Setting tag parameter appropriately */
                /* QUICK CALL */
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    fun onClick(tileNumber: Int){
        /* The idea for this function is to do the bulk of the work when the user clicks the tile
         * just using a function to reduce the amount of code */

        if(TAG=="SMS"){

        } else {

        }
    }

    // nullableImage is inaccessable, image == nullableImage if nullableImage != null
    // else image == "NONE", the else part can be changed as appropriate to produce a default image
    class Contact(val name: String, private val nullableImage: String?) {
        // Abusing lazy for a neat way of producing a Delegate
        val image by lazy { nullableImage ?: "NONE" } // Generate default image URI here
        override fun toString() : String = "Contact(name=$name, image=$image)"
    }
    
    data private class NullableContact(val name: String?, val image: String?)

}
