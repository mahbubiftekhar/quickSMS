package quick.SMS

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import org.jetbrains.anko.toast
import android.annotation.SuppressLint
import org.jetbrains.anko.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import quick.SMS.R.layout.activity_splash


class SplashActivity : AppCompatActivity(),ActivityCompat.OnRequestPermissionsResultCallback {

    @SuppressLint("SetTextI18n", "PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_splash)
        supportActionBar?.hide() //hide actionbar
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111")
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        fun correctPermissions():Boolean {
            val smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            val callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            val readContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            return smsPermission == PackageManager.PERMISSION_GRANTED && callPermission == PackageManager.PERMISSION_GRANTED&& readContactsPermission== PackageManager.PERMISSION_GRANTED
        }
        fun requestPermissions(permissions: Array<String>) {
            /* Request Permission if required */
            ActivityCompat.requestPermissions(this, permissions, 1)
        }

        if(!correctPermissions()){
            requestPermissions(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS))
        } else {
            contactsLookUp()
            startActivity<MainActivity>() //need to pass intents
            finish()
        }
    }

    private fun contactsLookUp() {
        //We need to look up the contacts so the next screen can be done without any delay
    }
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
       val smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
       val callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
       val readContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)

       fun requestPermissions(permissions: Array<String>) {
           /* Request Permission if required */
           ActivityCompat.requestPermissions(this, permissions, 1)

       }

       if(smsPermission == PackageManager.PERMISSION_GRANTED && callPermission == PackageManager.PERMISSION_GRANTED&& readContactsPermission== PackageManager.PERMISSION_GRANTED){
           contactsLookUp()
           startActivity<MainActivity>() //need to pass intents
           finish()
       } else {
           toast("You must accept permissions for app to work")
           requestPermissions(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS))
       }
    }
}


