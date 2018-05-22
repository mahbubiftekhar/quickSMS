package quick.sms.quicksms.ui

import Util.Android.BaseApp.Companion.context
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import quick.sms.quicksms.R
import org.jetbrains.anko.*
import quick.sms.quicksms.BaseActivity
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.backend.allLogs


var allLogsLocal = ArrayList<DatabaseLog.Log>()
private var mAdView: AdView? = null

class LogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val tilesDB = DatabaseLog(this)
        val a = tilesDB.returnAll()
        doAsync {
            MobileAds.initialize(applicationContext, "ca-app-pub-2206499302575732~5712613107\n")
            mAdView = findViewById<View>(R.id.adView) as AdView
            val adRequest = AdRequest.Builder().build()
            uiThread {
                mAdView!!.loadAd(adRequest)
            }
        }
        if (a) {
            //If the returnAll function was successful we shall launch the UI
            allLogsLocal = allLogs
            UIcreator()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate your main_menu into the menu
        menuInflater.inflate(R.menu.logactivity, menu)
        // Locate MenuItem with ShareActionProvider
        return true
    }


    private fun clearLog() {
        val tilesDataBase = DatabaseTiles(this)
        tilesDataBase.deleteEntireDB()
    }

    override fun extendedOptions(item: MenuItem) = when (item.itemId) {

        R.id.clearLogButton -> {
            alert("This action is irreversible") {
                title = "Are you sure you want to clear the log?"
                positiveButton("Yes, clear log") {
                    //user wishes to clear the log
                    doAsync {
                        //Asynchronously clear the log, when finnished redraw the activity
                        clearLog()
                        uiThread {
                            //redraw the activity layout
                            setContentView(R.layout.activity_log)
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
    fun UIcreator(): View {
        return scrollView {
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
                            }.lparams(width = wrapContent, height = wrapContent)
                            textView {
                                // tvTime
                                text = allLogsLocal[i].phoneNumber
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
                            }.lparams(width = wrapContent, height = wrapContent)
                            textView {
                                // tvTime
                                text = allLogsLocal[i].receipientName
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
                            }.lparams(width = wrapContent, height = wrapContent) {
                                rightMargin = dip(10)
                            }
                            textView {
                                // tvTime
                                text = allLogsLocal[i].timeStamp
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

