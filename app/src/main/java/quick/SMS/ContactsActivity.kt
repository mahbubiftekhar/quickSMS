package quick.SMS

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactsFromIntent = intent.extras.get("contacts")
        // Can't check for erased type, the best we can do it enforce that it's a list
        if (contactsFromIntent is List<*>) {
            val contacts = contactsFromIntent as List<Contact>
            ContactsLayout(contacts).setContentView(this)
        } else {
            throw RuntimeException("Intent supplied incorrect type")
        }
    }
}

class ContactsLayout(val contacts: List<Contact>) : AnkoComponent<ContactsActivity> {

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
                imageView(imageResource = R.drawable.default_image)
                frameLayout {
                    verticalLayout {
                        // Name
                        textView(contact.name) {
                            textSize = sp(10).toFloat()
                        }.lparams(width = matchParent) {
                            leftMargin = dip(10)
                        }
                        for (number in contact.numbers) {
                            // Number
                            textView(number) {
                                textSize = sp(14).toFloat()
                            }.lparams(width = matchParent) {
                                leftMargin = dip(10)
                            }
                        }
                    }.lparams(width = matchParent, height = matchParent) {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }
            }.lparams(width = matchParent)
    }
}