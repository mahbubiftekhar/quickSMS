package quick.sms.quicksms.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import quick.sms.quicksms.R
import org.jetbrains.anko.*
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.R.layout.activity_log
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.allLogs


var allLogsLocal = ArrayList<DatabaseLog.Log>()
private var mAdView: AdView? = null

class LogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBarColour()
        setContentView(activity_log)

        val tilesDB = DatabaseLog(this)
        val a = tilesDB.returnAll()
        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
        if (a && allLogs.size > 0) {
            //If the returnAll function was successful we shall launch the UI
            allLogsLocal = allLogs
            createUI(backgroundColour, tileTextColour)
        } else {
            println(">>>>> in the iff condition")
            //If their is no logs, we need to display to the user this so they arent confused
            allLogsLocal = allLogs
            noLogsToDisplay(backgroundColour, tileTextColour)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate your main_menu into the menu
        menuInflater.inflate(R.menu.logactivity, menu)
        // Locate MenuItem with ShareActionProvider
        return true
    }


    private fun clearLog() {
        //This function will delete the entire log database
        val tilesDataBase = DatabaseLog(this)
        tilesDataBase.deleteEntireDB()
        recreate() // This will recreate the activity
    }

    override fun extendedOptions(item: MenuItem) = when (item.itemId) {

        R.id.clearLogButton -> {
            //Button to allow the user to clear the log
            alert("This action is irreversible") {
                title = "Are you sure you want to clear the log?"
                positiveButton("Yes, clear log") {
                    //user wishes to clear the log
                    doAsync {
                        //Asynchronously clear the log, when finnished redraw the activity
                        clearLog()
                        uiThread {
                            //redraw the activity layout
                            setContentView(activity_log)
                        }
                    }
                }
                negativeButton("Cancel") {
                    //User changed their mind
                }

            }.show()
            true
        }
        else -> {
            super.extendedOptions(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun noLogsToDisplay(backgroundColour: String, textColour: String): View {
        return verticalLayout {
            backgroundColor = Color.parseColor(backgroundColour) //Setting the background colour
            textView {
                this.gravity = Gravity.CENTER
                text = "SMS Log is empty"
                textSize = 40f
                textColor = Color.parseColor(textColour)
                textAlignment = View.TEXT_ALIGNMENT_CENTER //CENTER can be INHERIT GRAVITY TEXT_START TEXT_END VIEW_START VIEW_END
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun createUI(backgroundColour: String, textColour: String): View {
        return scrollView {
            backgroundColor = Color.parseColor(backgroundColour) //Setting the background colour
            verticalLayout {
                allLogsLocal.reverse() // reverse the log
                for (i in 0 until allLogsLocal.size) {
                    lparams {
                        width = matchParent
                        height = matchParent
                    }
                    padding = dip(5)
                    verticalLayout {
                        lparams {
                            width = matchParent
                            height = wrapContent
                        }
                        padding = dip(5)
                        linearLayout {
                            lparams {
                                width = wrapContent
                                height = matchParent
                            }
                            leftPadding = dip(10)
                            textView {
                                // tvNameMain
                                text = allLogsLocal[i].message
                                textSize = sp(9).toFloat()
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent) {
                                topPadding = dip(5)
                            }
                        }
                        linearLayout {
                            lparams {
                                width = matchParent
                                height = wrapContent
                                leftPadding = dip(10)
                            }
                            textView {
                                // textView2
                                text = "Number: "
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent)
                            textView {
                                // tvTime
                                text = allLogsLocal[i].phoneNumber
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent) {
                                rightMargin = dip(10)
                            }
                            textView {
                                // tvTime
                            }.lparams(width = wrapContent, height = wrapContent)
                        }
                        linearLayout {
                            lparams {
                                width = matchParent
                                height = wrapContent
                                leftPadding = dip(10)
                            }
                            textView {
                                // textView2
                                text = "Recipient: "
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent)
                            textView {
                                // tvTime
                                text = allLogsLocal[i].receipientName
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent) {
                                rightMargin = dip(10)
                            }
                            textView {
                                // tvTime
                            }.lparams(width = wrapContent, height = wrapContent)
                        }
                        linearLayout {
                            lparams {
                                width = matchParent
                                height = wrapContent
                                leftPadding = dip(10)
                            }
                            textView {
                                // tvDate
                                text = "Date & Time:"
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent) {
                                rightMargin = dip(10)
                            }
                            textView {
                                // tvTime
                                text = allLogsLocal[i].timeStamp
                                textColor = Color.parseColor(textColour)
                            }.lparams(width = wrapContent, height = wrapContent) {
                                rightMargin = dip(10)
                            }
                            textView {
                                // tvType
                            }.lparams(width = wrapContent, height = wrapContent)
                        }
                    }
                }
            }
        }
    }
}

