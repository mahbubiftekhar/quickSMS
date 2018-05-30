package quick.sms.quicksms

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import quick.sms.quicksms.backend.putIntAndCommit
import quick.sms.quicksms.ui.SettingsActivity

open class BaseActivity : AppCompatActivity() {

    // Activities that shouldn't automatically inherit a menu
    private val excludedActivities = setOf("SettingsActivity", "SplashActivity", "MainActivity",
            "FallbackActivity")

    // Shared Preference Accessors
    protected var nTiles
        get() = prefs.getInt("nTiles", 0)
        set(value) = editor.putIntAndCommit("nTiles", value)

    protected val showName
        get() = settings.getBoolean("ShowName", false)

    protected val actionBarColour
        get() = settings.getString("actionbarcolour", "#303F9F")

    protected val backgroundColour
        get() = settings.getString("backgroundcolour", "#217ca3")

    protected val tileTextColour
        get() = settings.getString("TileTextColour", "#000000")

    protected val shouldVibrate
        get() = settings.getBoolean("Vibrate", false)

    protected val shouldDoubleCheck
        get() = settings.getBoolean("DoubleCheck", false)

    protected val tileColour
        get() = settings.getString("tilecolour", "#ffffff")

    protected val shouldMakeSound
        get() = settings.getBoolean("Sound", true)

    protected val warnColourClash
        get() = settings.getBoolean("ColourCombination", true)


    protected val textAndTileColoursClash
        get() = tileTextColour == tileColour

    protected val tileAndBackgroundColoursClash
        get() = backgroundColour == tileColour

    @SuppressLint("ObsoleteSdkInt")
    override fun recreate() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            super.recreate()
        } else {
            startActivity(intent)
            finish()
        }
    }

    fun setActionBarColour() {
        //gets the users selected actionbar colour
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(actionBarColour)))
    }

    fun colourCheck() {
        if (warnColourClash) { //If the user wants this warning
            val dialogText = if (textAndTileColoursClash && tileAndBackgroundColoursClash) {
                //Warn the user that they are using the same colour for background, textColour and tileColour
                "You are using the same colours for your the background, tiles and text \n"
            } else if (textAndTileColoursClash) {
                //Warn the user if they are using same tile and text colour
                "You are using the same colours for your the tiles and text \n"
            } else if (tileAndBackgroundColoursClash) {
                //Warn the user if they are using the same background and tile colour
                "You are using the same colours for your the background and tiles \n"
            } else {
                //Do nothing, the colours are fine
                null
            }
            dialogText?.let {
                alert("You can disable this warning in settings") {
                    title = it
                    positiveButton("Change settings") {
                        startActivity<SettingsActivity>()
                    }
                }.show()
            }
        }
    }

    // Adhoc inheritance for menus
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