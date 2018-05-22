package quick.sms.quicksms.ui

import Util.Android.getPermissions
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import org.jetbrains.anko.*
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R
import quick.sms.quicksms.backend.Contact

class SplashActivity : BaseActivity() {
    private val requiredPermissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
    )
    private var mAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide() //hide actionbar
        doAsync {
            requestPermissions()
        }
        //This part makes the add programatically
        MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107\n")
        mAdView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= 23
        ) {
            getPermissions(this, requiredPermissions)
        } else {
            sendContactsToMain()
        }
    }

    private fun sendContactsToMain() {
        println(5)
        Contact.getContacts(this) {
            startActivity<MainActivity>("contacts" to it)
            finish() //This will prevent the user getting back into this activity
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val grantResultsBool = grantResults.asSequence().map { it == PackageManager.PERMISSION_GRANTED }.toList()
        if (grantResultsBool.all { it }) {
            println(6)
            sendContactsToMain()
        } else {
            println(7)
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                println(8)
                // Infinite looping from auto-denied permissions isn't a problem before api 23
                if (!grantResultsBool.zip(permissions)
                                .filter { !it.first }.map { it.second }
                                .all { shouldShowRequestPermissionRationale(it) }) {
                    /* shouldShowRequestPermissionRationale returns true if the user has denied a
                    permission but not ticked the "don't ask again" box, false if they have and
                    false if they have never denied the permission. The last point is why this
                    is here and not in getPermissions */
                    return
                }
            }
            println(9)
            toast("You must accept all permissions to use quickSMS")
            getPermissions(this, requiredPermissions)
        }
    }
}
