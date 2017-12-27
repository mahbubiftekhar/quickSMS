package quick.SMS

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_test)
        UI(true) {
            verticalLayout {
                val user = editText {
                    hint = "Username"
                }
                val pass = editText {
                    hint = "Password"
                }
                button("Login") {
                    setOnClickListener {
                        longToast("User: ${user.text}, Pass: ${pass.text}")
                    }
                }
            }
        }
    }
}
