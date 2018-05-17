package quick.sms.quicksms.contacts

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import org.jetbrains.anko.*
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contacts = intent.extras.get("contacts") as List<Contact>
        ContactsLayout(contacts).setContentView(this)
    }

    private class ContactsLayout(val contacts: List<Contact>): AnkoComponent<ContactsActivity> {

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
            imageView(imageResource=R.drawable.default_image)
            frameLayout {
                verticalLayout {
                    textView(contact.name) {
                        textSize = sp(10).toFloat()
                    }.lparams(width=matchParent) {
                        leftMargin=dip(10)
                    }
                    for (number in contact.numbers) {
                        textView(number) {
                            textSize = sp(14).toFloat()
                        }.lparams(width= matchParent) {
                            leftMargin = dip(10)
                        }
                    }
                }.lparams(width=matchParent, height=matchParent) {
                    gravity = Gravity.CENTER_VERTICAL
                }
            }.lparams(width=matchParent)
        }
    }
}
