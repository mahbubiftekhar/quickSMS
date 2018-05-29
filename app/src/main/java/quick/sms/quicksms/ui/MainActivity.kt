package quick.sms.quicksms.ui

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import quick.sms.quicksms.backend.Contact
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseMessages
import quick.sms.quicksms.context
import java.io.FileNotFoundException
import java.lang.Math.ceil

class MainActivity : BaseActivity() {

    private lateinit var contacts: Map<Int, Contact>
    private lateinit var contactsList: List<Contact>
    private lateinit var unassigned: List<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        val bundle: Bundle = savedInstanceState ?: intent.extras
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        super.onCreate(savedInstanceState)
        contactsList = bundle.get("contacts") as List<Contact>
        val (assigned, unassigned) = contactsList.asSequence().partition { it.tile != null }
        this.unassigned = unassigned
        contacts = assigned.associateBy { it.tile!! }
        draw()
    }

    private fun draw() {
        setActionBarColour()
        MainLayout(contentResolver, nTiles, contacts, getBackGroundColour(), gettileColour(),
                getTileTextColour(), showName(), ::onClick, ::assignTile, ::createTile,
                ::deleteTile).setContentView(this)
    }

    fun createTile() {
        nTiles++
        println(nTiles)
        draw()
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
        R.id.sync -> {
            //User wishes to resync so just send them to the splash and let the app do the rest
            finish()
            startActivity<SplashActivity>()
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

    private fun noneSet(){
        relativeLayout(){

        }
    }

    @SuppressLint("ApplySharedPref")
    private fun resetApp() {
        /*This is a very dangerous function, hence why its wrapped around two alerts for security*/
        val contactDB = DatabaseMessages(this)
        val tilesDB = DatabaseTiles(this)
        val log = DatabaseLog(this)
        contactDB.deleteEntireDB()
        tilesDB.deleteEntireDB()
        log.deleteEntireDB()
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit() //Resetting shared preferences
        runOnUiThread {
            //Restart the app programatically
            val i = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

    }

    private fun onClick(tileNumber: Int) {
        val contact = contacts[tileNumber]
        if (contact != null) {
            startActivity<TextMessageActivity>("contact" to contact, "tileID" to tileNumber) //Passing in contact info and tileNumber
        } else {
            println("No Contact found")
        }
    }

    private fun assignTile(tileNumber: Int) {
        startActivityForResult<ContactsActivity>(1,
                "tile_number" to tileNumber,
                "contacts" to contactsList)
    }

    private fun deleteFromContacts(tileNumber: Int) {
        val highestContact = contacts.keys.max()!!
        val mutableContacts = contacts.toMutableMap()
        for (i in tileNumber + 1..highestContact) {
            val contact = mutableContacts[i]
            if (contact != null) {
                mutableContacts[i - 1] = contact
            } else {
                mutableContacts.remove(i - 1)
            }
        }
        mutableContacts.remove(highestContact)
        contacts = mutableContacts.toMap()
    }

    private fun deleteTile(tileNumber: Int) {
        alert("NOTE: This is irreversible") {
            title = "Are you sure you want to delete this tile?"
            positiveButton("Yes, Delete") {
                val contact = contacts[tileNumber]
                if (contact != null) {
                    DatabaseTiles(this@MainActivity).tileDefragmentator(tileNumber)
                    deleteFromContacts(tileNumber)
                }
                nTiles--
                draw()
            }
            negativeButton("Cancel") {
                //Do nothing, the user changed their mind
            }
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            val mutableContacts = contacts.toMutableMap()
            val tileNumber = data.extras.getInt("tile_number", 0)
            val contact = data.extras.get("chosen_contact") as Contact
            contact.tile = tileNumber
            val tilesDB = DatabaseTiles(this)
            tilesDB.insertData(contact.id, tileNumber, "")
            mutableContacts[tileNumber] = contact
            contacts = mutableContacts.toMap()
        }
        draw()
        colourCheckFunction()
    }

    override fun onResume() {
        super.onResume()
        draw()
        colourCheckFunction()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val allContacts = contacts.values + unassigned
        println(contacts)
        outState?.putParcelableArrayList("contacts", ArrayList(allContacts))
    }

    private class MainLayout(val cr: ContentResolver, val nTiles: Int,
                             val alreadyAssigned: Map<Int, Contact>, val backgroundColour: String,
                             val tileColour: String, val textColour: String, val showName: Boolean,
                             val tileCallBack: (Int) -> Unit, val assignCallBack: (Int) -> Unit,
                             val createCallback: () -> Unit, val deleteCallback: (Int) -> Unit)
        : AnkoComponent<MainActivity> {
        val rows = ceil(nTiles / 2.0).toInt()

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            relativeLayout {
                scrollView {
                    backgroundColor = Color.parseColor(backgroundColour)

                    verticalLayout {
                        for (i in 1..rows) {
                            row(i)
                        }
                    }
                }.lparams {
                    alignParentTop()
                    alignParentBottom()
                    alignParentLeft()
                    alignParentRight()
                }
                floatingActionButton {
                    imageResource = android.R.drawable.ic_input_add
                    horizontalGravity = Gravity.END
                    verticalGravity = Gravity.BOTTOM
                    onClick { createCallback() }
                }.lparams {
                    alignParentBottom()
                    alignParentEnd()
                    bottomMargin = dip(60)
                    marginEnd = dip(16)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun _LinearLayout.row(row: Int) {
            verticalLayout {
                linearLayout {
                    tile(row, 1)
                    if (nTiles % 2 == 1 && row == rows) {
                        imageView {
                            backgroundColor = Color.parseColor(backgroundColour)
                        }.lparams(height = matchParent, width = 0) {
                            weight = 1f
                            margin = dip(7)
                        }
                    } else {
                        tile(row, 2)
                    }
                }.lparams(height = dip(180), width = matchParent) {
                    weight = 1f
                    padding = dip(7)
                }
            }
        }

        fun _LinearLayout.tile(row: Int, col: Int) {
            button {
                val index = (row - 1) * 2 + col
                val contact = alreadyAssigned[index]
                val image = contact?.image?.let {
                    try {
                        val inStream = cr.openInputStream(Uri.parse(it))
                        RoundedBitmapDrawableFactory.create(resources, inStream)
                    } catch (e: FileNotFoundException) {
                        null
                    }
                }
                image?.cornerRadius = dip(20).toFloat()
                val name = contact?.name ?: "+"
                if (image == null) {
                    backgroundResource = R.drawable.rounded_corners
                    (background as GradientDrawable).setColor(Color.parseColor(tileColour))
                    text = name
                    if(text=="+"){
                        textSize=60.toFloat()
                    }
                } else {
                    background = image
                    if (showName) {
                        text = name
                    }
                }
                textColor = Color.parseColor(textColour)
                onClick {
                    if (contact != null) {
                        tileCallBack(index)
                    } else {
                        assignCallBack(index)
                    }
                }
                onLongClick {
                    deleteCallback(index)
                }
            }.lparams(height = matchParent, width = 0) {
                weight = 1f
                margin = dip(7)
            }
        }

        // From https://stackoverflow.com/questions/34215129/convert-mainactivity-with-actionbar-toolbar-and-floatingaction-button-to-anko
        fun ViewGroup.floatingActionButton(init: FloatingActionButton.() -> Unit) =
            ankoView({ FloatingActionButton(it) }, theme = 0, init = init)
    }
}
