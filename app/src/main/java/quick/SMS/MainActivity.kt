package quick.SMS

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.net.Uri
/*
* https://antonioleiva.com/databases-anko-kotlin/
* The above link is to do database's, which I think we will need to save and retain each contacts
* common text messages - which will have to be defined by the user
*
* http://sonevalley.blogspot.co.uk/2015/12/retrieving-list-of-contacts-in-android.html
* A link explaining how to pull up the contacts from the user, in java but Kotlin is interoperable
*
*
* https://www.androidhive.info/2017/07/android-implementing-preferences-settings-screen/
* Toggle settings for the user
*
*I have already inserted compile 'org.jetbrains.anko:anko-sqlite:0.10.0' and synced
*Alex, I think you'll have to upgrade to at least android studio 3.0 to support Kotlin,
*I apologise I remember you telling me this is very difficult to do on your computer
*
* http://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
* Drap and drop link above
*
* */
class MainActivity : AppCompatActivity() {
    /*The reason I use a var is so it can be changed later,
    it is good practise to make everything val in Kotlin
    * unless it needs to be var*/
    private var TAG = "SMS" /*The idea to this one was to keep track easily of which tab is selected*/

    fun callNumber(phoneNumber: String){
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse(Manifest.permission.CALL_PHONE)
        try{startActivity(callIntent)}
        catch(e:SecurityException){
            requestPermission(Manifest.permission.CALL_PHONE)

        }
    }
    fun loadString(key: String): String {
        /*Function to load a shared preference String*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedValue = sharedPreferences.getString(key, "UNKNOWN") /*DEFAULT AS UNKNOWN*/
        return savedValue
    }
    fun saveString(key: String, value: String) {
        /*Function to save a shared preference String*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    private fun requestPermission(permission: String) {
        /*Function to request permission, takes a parameter of the permission we need need
        * which in this case will be SMS and CALLS permissions. This will only ask for it iff
        * we do not have the permission, otherwise nothing will happen*/
        try {
            val permissions = arrayOf(permission)
            ActivityCompat.requestPermissions(this, permissions, 1)
        } catch(e: SecurityException) {
            /*Catch any Security Exceptions*/
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.SMS -> {
                TAG = "SMS" /*Setting tag parameter appropriately*/
                /* QUICK SMS*/
                return@OnNavigationItemSelectedListener true
            }
            R.id.CALL -> {
                TAG = "CALL" /*Setting tag parameter appropriately*/
                /* QUICK CALL */
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    fun onClick(tileNumber: Int){
        /*The idea for this function is to do the bulk of the work when the user clicks the tile
        * just using a function to reduce the amount of code */

        if(TAG=="SMS"){

        } else {


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission(Manifest.permission.SEND_SMS) /*Request permission to send SMS*/
        requestPermission(Manifest.permission.CALL_PHONE) /*Request permission to make calls*/
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
}
