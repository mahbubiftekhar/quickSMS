package quick.SMS

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_text_message.*



private var receipient = null

class textMessageActivity : AppCompatActivity() {
    var MESSAGE_LIST: MutableList<String>? = null
    internal var helper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_message)


        butongoster.setOnClickListener {
            addData("1","test1")
            addData("1","test2")
            addData("1","test3")
            addData("2","should not get this")
            val messages = helper.returnAll("1")
            for(i in 1..messages!!.size-1){
                println("&&&"+messages[i])
            }
            println("should print nothing here on in")
            helper.deleteReceipient("1")
            for(i in 1..messages!!.size-1){
                println("&&&"+messages[i])
            }


        }
    }

    fun addData(receipient_id: String, message: String) {
        /*No id, this is because we are auto incrementing this one*/
        helper.insertData(receipient_id, message)

    }

    fun UpdateData(id: String, receipient_id: String, message: String) {
        val isUpdate = helper.updateData(id, receipient_id, message)
    }


    fun DeleteData(id: String) {
        helper.deleteData(id)

    }


    fun showMessage(title: String, Message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }
}