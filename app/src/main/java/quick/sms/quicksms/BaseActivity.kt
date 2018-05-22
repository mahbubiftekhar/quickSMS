package quick.sms.quicksms

import Util.Android.settings

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.startActivity
import quick.sms.quicksms.ui.SettingsActivity

open class BaseActivity : AppCompatActivity() {

    private val excludedActivities = setOf("SettingsActivity", "SplashActivity", "MainActivity")

    open fun getBackGroundColour(): String {
        return settings.getString("backgroundcolour", "#217ca3")
    }


    open fun soundBool(): Boolean {
        return settings.getBoolean("Sound", true)
    }

    override fun onResume() {
        //Setting the action bar colour
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
    }

    fun changeActionBarColour() {

    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        println(getActionBarColour())
        super.onCreate(savedInstanceState, persistentState)
    }


    open fun vibrateBool(): Boolean {
        return settings.getBoolean("Vibrate", true)
    }

    open fun doubleCheckBool(): Boolean {
        return settings.getBoolean("DoubleCheck", true)
    }

    open fun getActionBarColour(): String {
        return settings.getString("actionbarcolour", "#303F9F")
    }

    open fun gettileColour(): String {
        return settings.getString("tilecolour", "#303F9F")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (excludedActivities.contains(this::class.simpleName)) {
            return true
        }
        return menu?.let {
            menuPrepend(it)
            menuInflater.inflate(R.menu.menu, it)
            menuAppend(it)
            true
        } ?: false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity<SettingsActivity>()
            true
        }
        else -> extendedOptions(item)
    }

    open fun menuPrepend(menu: Menu) {}
    open fun menuAppend(menu: Menu) {}

    open fun extendedOptions(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}