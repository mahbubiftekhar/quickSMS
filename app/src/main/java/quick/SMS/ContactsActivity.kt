package quick.SMS

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.LinearLayout
import org.jetbrains.anko.*

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contacts = intent.extras.get("contacts") as Map<String, List<String>>
        ContactsLayout(contacts).setContentView(this)
    }
}

class ContactsLayout(val contacts: Map<String, List<String>>) : AnkoComponent<ContactsActivity> {

    override fun createView(ui: AnkoContext<ContactsActivity>) = with(ui) {
        val names = contacts.keys.sorted()
        scrollView {
            verticalLayout {
                for (name in names) {
                    contactView(name)
                }
            }
        }
    }

    fun _LinearLayout.contactView(name: String) {
            linearLayout {
                imageView(imageResource = R.drawable.default_image)
                frameLayout {
                    verticalLayout {
                        // Name
                        textView(name) {
                            textSize = sp(10).toFloat()
                        }.lparams(width = matchParent) {
                            leftMargin = dip(10)
                        }
                        for (number in contacts[name]!!) {
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