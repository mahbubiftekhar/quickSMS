package quick.sms.quicksms.ui

import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R

class SettingsActivity : BaseActivity() {
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBarColour()
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()

        //adverts
        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107\n")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.app_preferences)
        }
    }
}
