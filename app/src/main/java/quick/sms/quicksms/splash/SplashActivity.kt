package quick.sms.quicksms.splash

import Util.Android.getPermissions
import android.Manifest
import android.content.pm.PackageManager
import org.jetbrains.anko.*
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact
import quick.sms.quicksms.main.MainActivity

class SplashActivity : BaseActivity() {
    lateinit var mAdView: AdView

    private val requiredPermissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide() //hide actionbar

        // Setup Ads
        MobileAds.initialize(this, "ca-app-pub-2206499302575732~5712613107")
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-2206499302575732/2755153561"

        if (getPermissions(this, requiredPermissions)) {
            // Already got permissions
            doAsync{
                Thread.sleep(10000)
                sendContactsToMain()

            }

        }
    }

    private fun sendContactsToMain() {
        Contact.getContacts(this) {
            startActivity<MainActivity>("contacts" to it)
            finish() //This will prevent the user getting back into this activity
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            sendContactsToMain()
        } else {
            toast("You must accept all permissions to continue")
            getPermissions(this, requiredPermissions)
        }
    }
}
