package quick.sms.quicksms.log

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import quick.sms.quicksms.R
import android.widget.Button
import android.widget.LinearLayout
import quick.sms.quicksms.backend.DatabaseLog


class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val tilesDB = DatabaseLog(this)
        //dbID: Int, recipientId: Long, message: String, receipientName: String, phoneNumber: String
        tilesDB.insertData(Math.random().toLong(), "MESSAGE", "NAME", "0712356789")
        val log = tilesDB.returnAll()
        /* This will execute upon the async task being finnished */
        val ll_main: LinearLayout = findViewById(R.id.ll_main_layout) /*as LinearLayout */
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(1, 35, 1, 0) /* Used to set spaces between each button */
        /*creates a button for each song, and says what the buttons do */
        for (i in 0 until log!!.size) {
            val buttonDynamic = Button(this)
            /* setting layout_width and layout_height using layout parameters */
            buttonDynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            buttonDynamic.text = log[i]
            buttonDynamic.layoutParams = params
            buttonDynamic.setOnClickListener {
            }
            ll_main.addView(buttonDynamic) /*Add each button*/
        }

    }


}
