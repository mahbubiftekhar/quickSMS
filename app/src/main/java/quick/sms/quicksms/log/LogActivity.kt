package quick.sms.quicksms.log

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import quick.sms.quicksms.R
import org.jetbrains.anko.*
import quick.sms.quicksms.backend.DatabaseLog


class LogActivity : AppCompatActivity() {
    var sizeOfLog = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val tilesDB = DatabaseLog(this)
        val log = tilesDB.returnAll()
        UIcreator()

    }

    fun UIcreator(): View {
        return scrollView{
            verticalLayout {
                for (i in 0..15) {
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
                                text = "Unknown"
                                textSize = sp(15).toFloat()
                                // Can't find textStyle="bold"
                            }.lparams(width = wrapContent, height = wrapContent) {
                                topPadding = dip(5)
                                rightMargin = dip(5)
                            }
                            textView {
                                // textView2
                                text = "No: "
                            }.lparams(width = wrapContent, height = wrapContent)
                            textView {
                                // tvTime
                                text = "Number"
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
                                text = "Date"
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

