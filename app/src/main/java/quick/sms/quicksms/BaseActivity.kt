package quick.sms.quicksms

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.startActivity
import quick.sms.quicksms.settings.SettingsActivity

open class BaseActivity : AppCompatActivity() {

    private val excludedActivities = setOf("SettingsActivity", "SplashActivity")

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