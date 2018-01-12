package quick.SMS

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.sdk19.coroutines.onClick

/*
 * https://antonioleiva.com/databases-anko-kotlin/
 * The above link is to do database's, which I think we will need to save and retain each contacts
 * common text messages - which will have to be defined by the user
 *
 * https://www.androidhive.info/2017/07/android-implementing-preferences-settings-screen/
 * Toggle settings for the user
 *
 * http://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
 * Drap and drop link above
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainLayout(5, 2) { onClick(it) }.setContentView(this)

        // Request required permissions
        requestPermissions(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS))

        println("Starting Async Lookup")

        Contact.getContacts(ctx) {
            println(it)
        }

        println("onCreate continues in the meantime")
        val intent = Intent(this, textMessageActivity()::class.java)
        startActivity(intent)
    }

    fun contactsTest(contacts: List<Contact>) {
        startActivity<ContactsActivity>("contacts" to contacts)
    }

    fun callNumber(phoneNumber: String) {
        /* Calls a phone number */
        /*THIS IS THE CORRECT VERSION*/
        try {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK /*This line means you don't have to confirm the number
            in the dialer, suprisingly difficult to find online*/
            startActivity(callIntent)
        } catch(e: SecurityException) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE))
        }
        /*THIS IS THE CORRECT VERSION*/
    }


    private fun requestPermissions(permissions: Array<String>) {
        /* Request Permission if required */
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    fun onClick(tileNumber: Int) {

    }
}

class MainLayout(val rows: Int, val cols: Int, val tileCallback: (Int) -> Unit)
    : AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        scrollView {
            verticalLayout {
                for (i in 1..rows) {
                    row(cols, i)
                }
            }
        }
    }

    fun _LinearLayout.tile(row: Int, col: Int, rowLen: Int) {
        button {
            onClick {
                val index = (row - 1) * rowLen + col
                tileCallback(index)
            }
        }.lparams(height = matchParent, width = 0) {
            weight = 1f
        }
    }

    fun _LinearLayout.row(nTiles: Int, row: Int) {
        linearLayout {
            for (i in 1..nTiles) {
                tile(row, i, nTiles)
            }
        }.lparams(height = dip(180), width = matchParent) {
            weight = 1f
            padding = dip(7)
        }
    }
}
