package quick.sms.quicksms.ui

import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R

class FaqActivity : BaseActivity() {
    private var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBarColour()
        setContentView(R.layout.activity_faq_actovoty)
        colourCheckFunction()
        val faq = mutableMapOf<String, String>()
        faq["If I delete a user from a tile, do the messages get deleted?"] = "No, the messages will remain, so if you change your mind you can pick up where you left off!"
        faq["Can I customise the app?"] = "Yes, you can customize many aspects of the app in the settings"
        faq["I keep accidentally sending messages"] = "You can turn on 'Confirm before sending' and you will be prompted to double confirm before sending"
        faq["Can I see messages I've sent?"] = "Yes, you can see this in the app 'SMS LOG'"
        faq["I have some suggestions, how can I share them?"] = "We're more than happy to hear you suggestions! User the contact table, accessible from 'options'"
        faq["When I send a message, it says 'Sorry, couldn't send SMS'"] = "This may be due to many reasons e.g. No reception, out of PAYG credit"
        faq["Can I add my own messages for the contact?"] = "Yes, simply click on the receipient's tile, click 'options', 'add a message' then type your message and press 'add', its that easy!"
        faq["Can I remove a recipient?"] = "Yes, simply press on the recipient's tile for 3-4 seconds, then just confirm to delete"
        faq["My recipient has multiple numbers, can I choose which one I send messages to?"] = "Of course you can! Simply click the tile for that recipient, click 'options', then 'Select Number' and pick the number you want to call and send SMS's to, its that easy!"
        faq["Can I view the number I have set for a recipient"] = "Of course you can! Simply click the tile for that recipient, click 'optons', then 'Info', a pop up will appear with their number"
        faq["Can I delete a message I sent?"] = "Unfortunately you cannot un-send a SMS"
        faq["Can I delete a pre-set message"] = "Yes, press on that message for 3-4 seconds, hit 'delete' and that's it!"

        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107\n")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }

        scrollView {
            verticalLayout {
                for (f in faq) {
                    button {
                        text = f.key
                        onClick {
                            alert(f.value) {
                                title = f.key
                                positiveButton("") {
                                }
                            }.show()

                        }
                    }
                }
            }
        }
    }
}
