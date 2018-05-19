package quick.sms.quicksms.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import quick.sms.quicksms.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class AboutDevelopersActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_developers)

        MobileAds.initialize(this, "ca-app-pub-7643266345625929~4795636158")

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}
