package quick.SMS

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.activity_settings.*

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        setSupportActionBar(toolbar)


        /* val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, SongTitles)
         Spinner.adapter = adapter
         /*set click listener*/
         Spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SongTitles)
         Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
             override fun onNothingSelected(parent: AdapterView<*>?) {
                 /*Do nothing on nothing selected*/
             }

             override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =

         }


 */
    }

}
