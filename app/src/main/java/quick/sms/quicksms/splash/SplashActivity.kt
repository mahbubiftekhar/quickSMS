package quick.sms.quicksms.splash

import Util.Android.getPermissions
import android.Manifest
import android.content.pm.PackageManager
import org.jetbrains.anko.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact
import quick.sms.quicksms.main.MainActivity

class SplashActivity : AppCompatActivity() {

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
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111")
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        if (getPermissions(this, requiredPermissions)) {
            // Already got permissions
            sendContactsToMain()
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
