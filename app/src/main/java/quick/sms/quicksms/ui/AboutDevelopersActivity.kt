package quick.sms.quicksms.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import quick.sms.quicksms.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_about_developers.*


class AboutDevelopersActivity : AppCompatActivity() {
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_developers)

        MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107")
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-2206499302575732/2755153561"


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
