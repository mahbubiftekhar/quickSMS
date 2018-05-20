package quick.sms.quicksms.ui

import Util.Android.getPermissions
import android.Manifest
import android.content.pm.PackageManager
import org.jetbrains.anko.*
import android.os.Bundle
import com.google.android.gms.ads.AdView
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact

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

        /*// Setup Ads
        MobileAds.initialize(this, "ca-app-pub-2206499302575732~5712613107")
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-2206499302575732/2755153561"*/

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        val grantResultsBool = grantResults.asSequence()
                .map { it == PackageManager.PERMISSION_GRANTED }.toList()
        if (grantResultsBool.all { it }) {
            sendContactsToMain()
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                // Infinite looping from auto-denied permissions isn't a problem before api 23
                if (! grantResultsBool.zip(permissions)
                                .filter { !it.first }.map { it.second }
                                .all { shouldShowRequestPermissionRationale(it) }) {
                    // shouldShowRequestPermissionRationale returns true if the user has denied a
                    // permission but not ticked the "don't ask again" box, false if they have and
                    // false if they have never denied the permission. The last point is why this
                    // is here and not in getPermissions
                    return
                }
            }
            toast("You must accept all permissions to continue")
            getPermissions(this, requiredPermissions)
        }
    }
}
