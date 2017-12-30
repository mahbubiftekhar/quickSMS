package quick.SMS

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        verticalLayout {
            for (name in names) {
                textView(name)
            }
        }
    }
}