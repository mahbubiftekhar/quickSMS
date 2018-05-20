package quick.sms.quicksms.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact

class ContactsActivity : BaseActivity() {

    private var tileNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contacts = intent.extras.get("contacts") as List<Contact>
        tileNumber = intent.getIntExtra("tile_number", 0)
        ContactsLayout(contacts) { selectContact(it) }.setContentView(this)
    }

    private fun selectContact(contact: Contact) {
        val returnIntent = Intent()
        returnIntent.putExtra("tile_number", tileNumber)
        returnIntent.putExtra("chosen_contact", contact)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private class ContactsLayout(val contacts: List<Contact>, val selectContact: (Contact) -> Unit) : AnkoComponent<ContactsActivity> {

        override fun createView(ui: AnkoContext<ContactsActivity>) = with(ui) {
            scrollView {
                verticalLayout {
                    for (contact in contacts) {
                        contactView(contact)
                    }
                }
            }
        }

        fun _LinearLayout.contactView(contact: Contact) {
            linearLayout {
                imageView(imageResource = R.drawable.default_image).lparams() {
                    gravity = Gravity.START
                }
                textView(contact.name) {
                    textSize = sp(10).toFloat()
                }.lparams(width = matchParent) {
                    leftMargin = dip(10)
                    gravity = Gravity.END
                }
                onClick {
                    selectContact(contact)
                }
            }.lparams(width = matchParent)
        }
    }
}
