package quick.sms.quicksms.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact

@Suppress("DEPRECATION")
class ContactsActivity : BaseActivity() {

    private var tileNumber = 0
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contacts = (intent.extras.get("contacts") as List<Contact>).sortedBy { it.name }
        tileNumber = intent.getIntExtra("tile_number", 0)
        ContactsLayout(contentResolver, contacts) { selectContact(it) }.setContentView(this)

        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107\n")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
    }

    private fun selectContact(contact: Contact) {
        val returnIntent = Intent()
        returnIntent.putExtra("tile_number", tileNumber)
        returnIntent.putExtra("chosen_contact", contact)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private class ContactsLayout(val cr: ContentResolver, val contacts: List<Contact>,
                                 val selectContact: (Contact) -> Unit)
        : AnkoComponent<ContactsActivity> {

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
