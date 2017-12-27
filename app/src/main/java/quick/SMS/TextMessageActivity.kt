package quick.SMS

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk19.coroutines.onClick

private var recipient = null

class TextMessageActivity : AppCompatActivity() {
    lateinit var messages: List<String>
    internal var helper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Get messages here
        messages = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        // First lambda is for the call button, second is for all of the text buttons
        TextMessageLayout(messages, { println("Calling") }, { println(it) }).setContentView(this)
        //setContentView(R.layout.activity_text_message)


        /*butongoster.setOnClickListener {
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
        }*/
    }

    fun addData(recipient_id: Long, message: String) {
        /* No id, this is because we are auto incrementing this one */
        helper.insertData(recipient_id, message)

    }

    fun updateData(id: String, recipient_id: Long, message: String) {
        val isUpdate = helper.updateData(id, recipient_id, message)
    }


    fun deleteData(id: String) {
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

class TextMessageLayout(val messages: List<String>, val callCallback: () -> Unit,
                        val textCallback: (String) -> Unit) : AnkoComponent<TextMessageActivity> {

    override fun createView(ui: AnkoContext<TextMessageActivity>) = with(ui) {
        verticalLayout {
            // Call button
            button("Phone") {
                onClick {
                    callCallback()
                }
            }
            scrollView {
                verticalLayout {
                    // Text buttons
                    for (message in messages) {
                        button(message) {
                            onClick {
                                textCallback(message)
                            }
                        }
                    }
                }
            }
        }
    }
}

