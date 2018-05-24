package quick.sms.quicksms.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.google.android.gms.ads.AdRequest
import quick.sms.quicksms.R
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import quick.sms.quicksms.BaseActivity
import android.content.Intent
import android.net.Uri


class AboutDevelopersActivity : BaseActivity() {
    private var mAdView: AdView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBarColour()
        setContentView(R.layout.activity_about_developers)
        this.supportActionBar?.title = "About quickSMS" //Adding the action bar title programatically

        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
        scrollView {
            verticalLayout {
                textView {
                    text = "About quickSMS"
                    textSize = 100f
                    textColor = Color.GREEN
                    textAlignment = View.TEXT_ALIGNMENT_CENTER //CENTER can be INHERIT GRAVITY TEXT_START TEXT_END VIEW_START VIEW_END
                }
                imageView(R.drawable.logo) {
                    backgroundColor = Color.TRANSPARENT //Removes gray border
                    horizontalPadding = dip(10)
                    verticalPadding = dip(15)
                }
                textView {
                    text = "quickSMS has been designed to make sending text messages quicker and easier. \n It was developed by two students whilst at the University of Edinburgh"
                    textSize = 30f
                    textColor = Color.RED
                    textAlignment = View.TEXT_ALIGNMENT_CENTER //CENTER can be INHERIT GRAVITY TEXT_START TEXT_END VIEW_START VIEW_END
                }
                textView {
                    textSize = 10f

                }
                textView {
                    text = "Developers"
                    textSize = 40f
                    textColor = Color.BLACK
                    textAlignment = View.TEXT_ALIGNMENT_CENTER //CENTER can be INHERIT GRAVITY TEXT_START TEXT_END VIEW_START VIEW_END

                }


                textView {
                    text = "Alex Shand - Android Developer"
                    textSize = 30f
                    textColor = Color.BLACK
                    textAlignment = View.TEXT_ALIGNMENT_CENTER //CENTER can be INHERIT GRAVITY TEXT_START TEXT_END VIEW_START VIEW_END
                    onClick {
                        val websites = listOf("GitHub", "LinkEdin")
                        selector("Would you like to visit Alex's GitHub repository or LinkedIn?", websites, { _, i ->
                            try {
                                when {
                                /*websites[i] == "Website" -> //If the user selected website
                                    launchWeb("https://www.mahbubiftekhar.co.uk/")*/
                                    websites[i] == "LinkEdin" -> {
                                        launchWeb("https://www.linkedin.com/in/alex-shand-3a9993150/")
                                    }
                                    else -> //Else send them to Github
                                        launchWeb("https://github.com/Alex-Shand")
                                }
                            } catch (e: Exception) {

                            }
                        })
                    }
                }
                textView {
                    text = "Mahbub Iftekhar - Android Developer"
                    textSize = 30f
                    textColor = Color.BLACK
                    textAlignment = View.TEXT_ALIGNMENT_CENTER //CENTER can be INHERIT GRAVITY TEXT_START TEXT_END VIEW_START VIEW_END
                    onClick {
                        val websites = listOf("Website", "GitHub", "LinkEdin")
                        selector("Would you like to visit Mahbub's Website, GitHub repository or LinkedIn?", websites, { _, i ->
                            try {
                                when {
                                    websites[i] == "Website" -> //If the user selected website
                                        launchWeb("https://www.mahbubiftekhar.co.uk/")
                                    websites[i] == "LinkEdin" -> {
                                        launchWeb("https://www.linkedin.com/in/mahbub-iftekhar/")
                                    }
                                    else -> //Else send them to Github
                                        launchWeb("https://github.com/mahbubiftekhar")
                                }
                            } catch (e: Exception) {

                            }
                        })
                    }
                }
            }
        }
        colourCheckFunction()

    }

    private fun launchWeb(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
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
