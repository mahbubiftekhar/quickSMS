package quick.sms.quicksms.ui

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import quick.sms.quicksms.backend.Contact
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseMessages

var backgroundColour = ""

class MainActivity : BaseActivity() {

    private lateinit var contacts: Map<Int, Contact>
    private lateinit var contactsList: List<Contact>
    private lateinit var unassigned: List<Contact>
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val bundle : Bundle = savedInstanceState ?: intent.extras
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        super.onCreate(savedInstanceState)
        backgroundColour = getBackGroundColour()
        contactsList = bundle.get("contacts") as List<Contact>
        val (assigned, unassigned) = contactsList.asSequence().partition { it.tile != null }
        this.unassigned = unassigned
        contacts = assigned.associateBy { it.tile!! }
        verticalLayout {
            include<View>(R.xml.advertxml) {}
        }
        draw()

        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
    }

    private fun draw() {
        setActionBarColour()
        MainLayout(contentResolver, 5, 2, contacts, gettileColour(), { onClick(it) },
                { assignTile(it) }).setContentView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate your main_menu into the menu
        menuInflater.inflate(R.menu.mainactivity, menu)
        // Locate MenuItem with ShareActionProvider
        return true
    }

    override fun extendedOptions(item: MenuItem) = when (item.itemId) {

        R.id.action_settings -> {
            startActivity<SettingsActivity>()
            true
        }
        R.id.menu_item_share -> {
            //Allow the users to share the app to their friends/family
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBodyText = "Check it out, quickSMS saves me so much time! Download it from the Google Play store!"
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check it out! quickSMS")
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText)
            startActivity(Intent.createChooser(sharingIntent, "Sharing Options"))
            true
        }
        R.id.about -> {
            //About the app and developers
            startActivity<AboutDevelopersActivity>()
            true
        }
        R.id.contactButton -> {
            //Contact form
            startActivity<ContactUsActivity>()
            true
        }
        R.id.faqButton -> {
            //Contact form
            startActivity<FaqActivity>()
            true
        }
        R.id.contactLog -> {
            //View the log activity
            startActivity<LogActivity>()
            true
        }
        R.id.resetApp -> {
            alert("Are you sure you wish to reset the app?") {
                positiveButton("Yes") {
                    alert("Do you wish to proceed?") {
                        title = "NOTE: This action is IRREVERSIBLE"
                        positiveButton("Yes proceed, RESET APP") {
                            println(">>>WE ARE GETTING HERE")
                            doAsync {
                                resetApp()
                            }
                        }
                        negativeButton("No, cancel") {

                        }

                    }.show()
                }
                negativeButton("No") {

                }

            }.show()
            true
        }

        else -> {
            super.extendedOptions(item)
        }
    }

    private fun resetApp() {
        /*This is a very dangerous function, hence why its wrapped around two alerts for security*/
        println(">>>> reset App function")
        val contactDB = DatabaseMessages(this)
        val tilesDB = DatabaseTiles(this)
        val log = DatabaseLog(this)
        contactDB.deleteEntireDB()
        tilesDB.deleteEntireDB()
        log.deleteEntireDB()
        runOnUiThread {
            //Restart the app programatically
            println(">>>>> Restarting the device")
            val i = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

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
            contact.tile = tileNumber
            val tilesDB = DatabaseTiles(this)
            tilesDB.insertData(contact.id, tileNumber, 0)
            mutableContacts[tileNumber] = contact
            contacts = mutableContacts.toMap()
        }
        draw()
    }

    override fun onResume() {
        super.onResume()
        draw()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val allContacts = contacts.values + unassigned
        println(contacts)
        outState?.putParcelableArrayList("contacts", ArrayList(allContacts))
    }

    private class MainLayout(val cr: ContentResolver, val rows: Int, val cols: Int,
                             val alreadyAssigned: Map<Int, Contact>, val tileColour : String,
                             val tileCallBack: (Int) -> Unit, val assignCallBack: (Int) -> Unit) : AnkoComponent<MainActivity> {


        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            scrollView {
                backgroundColor = Color.parseColor(backgroundColour)

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
                val contact = alreadyAssigned[index]
                val image = contact?.image?.let {
                    val inStream = cr.openInputStream(Uri.parse(it))
                    Drawable.createFromStream(inStream, it)
                }
                if (image == null) {
                    backgroundColor = Color.parseColor(tileColour)
                } else {
                    background = image
                }
                text = contact?.name
                onClick {
                    tileCallBack(index)
                }
                onLongClick {
                    assignCallBack(index)
                }
            }.lparams(height = matchParent, width = 0) {
                weight = 1f
                margin = dip(7)
            }
        }
    }
}
