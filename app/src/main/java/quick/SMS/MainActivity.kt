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
import android.Manifest
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.net.Uri
import android.content.Context
import android.provider.ContactsContract
import org.jetbrains.anko.ctx
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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

        println("Starting Async Lookup")

        getContacts(ctx) { contacts ->
            // Callback function goes here
            println(contacts)
        }

        println("onCreate continues in the meantime")

    }

    fun getContacts(ctx: Context, then: (List<Contact>)->Unit) {
        doAsync {
            // All contacts saved on the device in raw form
            val result = ctx.contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null)

            // Parse into an intermediate form where the name can be null and we don't know if
            // there are any phone numbers
            val parsed = result.parseList(object: MapRowParser<NullableContact> {
                override fun parseRow(columns: Map<String, Any?>): NullableContact {
                    return NullableContact(
                            columns[ContactsContract.Contacts._ID] as Long,
                            columns[ContactsContract.Contacts.DISPLAY_NAME] as? String,
                            columns[ContactsContract.Contacts.PHOTO_URI] as? String,
                            columns[ContactsContract.Contacts.HAS_PHONE_NUMBER] as Long)
                }
            })

            // Remove Contacts with null names or no phone number, sort by name and convert to
            // null safe Contacts
            val contacts = parsed
                           .filter { it.name != null && it.hasNumber == 1L }
                           .sortedBy { it.name }
                           .map { Contact(ctx, it.id, it.name!!, it.image) }

            // Send them to the callback
            uiThread {
                then(contacts)
            }
        }
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
        val savedValue = sharedPreferences.getString(key, "UNKNOWN") /* DEFAULT AS UNKNOWN */
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
        println("we are getting here")
        var helperTiles = DatabaseTiles(this)
        val intent = Intent(this, textMessageActivity::class.java)
        startActivity(intent)
        helperTiles.insertData(2L, 2)
        val a = helperTiles.getRecipient(2)
        println(a)
        helperTiles.deleteTileAndRecipeintData(2)
        println("should be nothing")
        val b2 = helperTiles.getRecipient(2)
        println(b2)

        /* Deletes whole database, use function wisely oh Alex San, with great power, comes even
        greater responsibility */
        helperTiles.deleteEntireDBTiles()



    }

    data private class NullableContact(val id: Long, val name: String?, val image: String?,
                                       val hasNumber: Long)

        
}
