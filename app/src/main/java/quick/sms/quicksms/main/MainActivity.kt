package quick.sms.quicksms.main

import android.content.Intent
import android.os.Bundle
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import quick.sms.quicksms.backend.Contact
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.contacts.ContactsActivity
import quick.sms.quicksms.textmessage.TextMessageActivity
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.log.LogActivity

class MainActivity : BaseActivity() {

    private lateinit var contacts: Map<Int, Contact>
    private lateinit var contactsList: List<Contact>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: There should be a better way to do this
        contactsList = intent.extras.get("contacts") as List<Contact>
        contacts = contactsList.asSequence().filter { it.tile != null }.associateBy { it.tile!! }
        MainLayout(5, 2, { onClick(it) }, { assignTile(it) }).setContentView(this)
        startActivity<LogActivity>()
    }

    private fun onClick(tileNumber: Int) {
        val contact = contacts[tileNumber]
        if (contact != null) {
            startActivity<TextMessageActivity>("contact" to contact)
        } else {
            println("No Contact found")
        }
    }

    private fun assignTile(tileNumber: Int) {
        println(tileNumber)
        startActivityForResult<ContactsActivity>(1,
                "tile_number" to tileNumber,
                "contacts" to contactsList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val mutableContacts = contacts.toMutableMap()
            val tileNumber = data.extras.getInt("tile_number", 0)
            val contact = data.extras.get("chosen_contact") as Contact
            val tilesDB = DatabaseTiles(this)
            tilesDB.insertData(contact.id, tileNumber, 0)
            mutableContacts[tileNumber] = contact
            contacts = mutableContacts.toMap()
        }
    }

    private class MainLayout(val rows: Int, val cols: Int, val tileCallBack: (Int) -> Unit,
                             val assignCallBack: (Int) -> Unit) : AnkoComponent<MainActivity> {

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            scrollView {
                verticalLayout {
                    for (i in 1..rows) {
                        row(cols, i)
                    }
                }
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

        fun _LinearLayout.tile(row: Int, col: Int, rowLen: Int) {
            button {
                val index = (row - 1) * rowLen + col
                onClick {
                    tileCallBack(index)
                }
                onLongClick {
                    assignCallBack(index)
                }
            }.lparams(height = matchParent, width = 0) {
                weight = 1f
            }
        }

        // Ad stuff
        /*
<com.google.android.gms.ads.AdView
        xmlns:ads="http://schemas.android.com/apk/res-auto"
        android:id="@+id/adView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:layout_alignParentBottom="true"
        ads:adSize="BANNER"
        ads:adUnitId="ca-app-pub-3940256099942544/6300978111">
    </com.google.android.gms.ads.AdView>

The above needs to be done in Anko, I have no idea how to do this
 */

        /*
        val adView = AdView(this)
    adView.adSize = AdSize.BANNER
    adView.adUnitId = "ca-app-pub-3940256099942544/6300978111" //Sample id, need to change to ours
         */
    }
}
