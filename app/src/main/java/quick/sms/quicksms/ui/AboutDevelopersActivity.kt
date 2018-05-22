package quick.sms.quicksms.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RelativeLayout
import com.google.android.gms.ads.AdRequest
import quick.sms.quicksms.R
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import quick.sms.quicksms.BaseActivity


class AboutDevelopersActivity : BaseActivity() {
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_developers)

        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
    }

    // Called when leaving the activity
    public override fun onPause() {
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        super.onDestroy()
    }
}
