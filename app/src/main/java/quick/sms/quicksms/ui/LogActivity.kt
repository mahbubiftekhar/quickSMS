package quick.sms.quicksms.ui

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import quick.sms.quicksms.R
import org.jetbrains.anko.*
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.allLogs


var allLogsLocal = ArrayList<DatabaseLog.Log>()

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val tilesDB = DatabaseLog(this)
        val a = tilesDB.returnAll()
        if (a) {
            //If the returnAll function was successful we shall launch the UI
            allLogsLocal = allLogs
            UIcreator()
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

