package quick.SMS

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text_message.*

private var recipient = null

class textMessageActivity : AppCompatActivity() {
    var MESSAGE_LIST: MutableList<String>? = null
    internal var helper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_message)


        butongoster.setOnClickListener {
            addData(1.toLong(),"test1")
            addData(1.toLong(),"test2")
            addData(1.toLong(),"test3")
            addData(2.toLong(),"should not get this")
            var messages = helper.returnAll(1.toLong())
            for(i in 1..messages!!.size-1){
                println("&&&"+messages[i])
            }
            println("&&& should print nothing here on in")
            helper.deleteRecipient(1.toLong())
            messages = helper.returnAll(1.toLong())
            for(i in 1..messages!!.size-1){
                println("&&&"+messages[i])
            }


        }
    }

    fun addData(recipient_id: Long, message: String) {
        /*No id, this is because we are auto incrementing this one*/
        helper.insertData(recipient_id, message)

    }

    fun UpdateData(id: String, recipient_id: Long, message: String) {
        helper.updateData(id, recipient_id, message)
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