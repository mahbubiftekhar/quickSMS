package quick.sms.quicksms.ui

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
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
import java.io.FileNotFoundException
import java.lang.Math.ceil

@Suppress("UNCHECKED_CAST") //Suppresses the unchcked casted on contactsList = bundle.get("contacts") as List<Contact>

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
        MainLayout(contentResolver, nTiles, contacts, backgroundColour, tileColour, tileTextColour,
                showName, ::onClick, ::assignTile, ::createTile, ::deleteTile, textColour)
                .setContentView(this)
        colourCheck()
    }

    private fun createTile() {
        nTiles++
        draw()
    }

    private fun onClick(tileNumber: Int) {
        val contact = contacts[tileNumber]
        if (contact != null) {
            startActivity<TextMessageActivity>("contact" to contact, "tileID" to tileNumber) //Passing in contact info and tileNumber
        } else {
            println("Sorry - No Contact found")
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
        alert("") {
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

    private class MainLayout(val cr: ContentResolver, val nTiles: Int,
                             val alreadyAssigned: Map<Int, Contact>, val backgroundColour: String,
                             val tileColour: String, val textColour: String, val showName: Boolean,
                             val tileCallBack: (Int) -> Unit, val assignCallBack: (Int) -> Unit,
                             val createCallback: () -> Unit, val deleteCallback: (Int) -> Unit, promptTextColour: Any)
        : AnkoComponent<MainActivity> {
        val rows = ceil(nTiles / 2.0).toInt()
        val promptTextColour = promptTextColour as Int

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            relativeLayout {
                backgroundColor = Color.parseColor(backgroundColour)
                if (nTiles == 0) {
                    //If no tiles are set, we should give the user a little prompt to encourage them to add some
                    textView(R.string.add_tile_prompt) {
                        gravity = Gravity.CENTER
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        includeFontPadding = false
                        textSize = sp(10).toFloat()
                        textColor = promptTextColour
                    }.lparams(width = matchParent, height = matchParent)
                } else {
                    scrollView {
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
                    if (text == "+") {
                        textSize = 60.toFloat()
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