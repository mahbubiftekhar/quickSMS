package quick.sms.quicksms

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import quick.sms.quicksms.backend.DatabaseLog
import quick.sms.quicksms.backend.DatabaseMessages
import quick.sms.quicksms.backend.DatabaseTiles
import quick.sms.quicksms.backend.putIntAndCommit
import quick.sms.quicksms.ui.SettingsActivity

open class BaseActivity : AppCompatActivity() {
    @SuppressLint("ApplySharedPref")
    protected fun resetApp() {
        /*This is a very dangerous function, hence why its wrapped around two alerts for security*/
        val contactDB = DatabaseMessages(this)
        val tilesDB = DatabaseTiles(this)
        val log = DatabaseLog(this)
        contactDB.deleteEntireDB()
        tilesDB.deleteEntireDB()
        log.deleteEntireDB()

        nTilesReset = 0 //Rest the shared preference
        runOnUiThread {
            //Restart the app programatically
            val i = baseContext.packageManager
                    .getLaunchIntentForPackage(baseContext.packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

    }

    // Activities that shouldn't automatically inherit a menu
    private val excludedActivities = setOf("SettingsActivity", "SplashActivity", "MainActivity",
            "FallbackActivity")

    // Shared Preference Accessors
    protected var nTiles
        get() = prefs.getInt("nTiles", 0)
        set(value) = editor.putIntAndCommit("nTiles", value)

    private var nTilesReset
        get() = 0
        set(value) = editor.putIntAndCommit("nTiles", 0)

    private fun loadInt(key: String): Int {
        /*Function to load an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences.getInt(key, 1)
    }

    protected val showName
        get() = settings.getBoolean("ShowName", false)

    private val actionBarColour
        get() = settings.getString("actionbarcolour", "#303F9F")

    protected val backgroundColour: String
        get() = settings.getString("backgroundcolour", "#217ca3")

    protected val tileTextColour: String
        get() = settings.getString("TileTextColour", "#000000")

    protected val shouldVibrate
        get() = settings.getBoolean("Vibrate", false)

    protected val shouldDoubleCheck
        get() = settings.getBoolean("DoubleCheck", false)

    protected val tileColour: String
        get() = settings.getString("tilecolour", "#ffffff")

    protected val shouldMakeSound
        get() = settings.getBoolean("Sound", true)

    private val warnColourClash
        get() = settings.getBoolean("ColourCombination", true)


    private val textAndTileColoursClash
        get() = tileTextColour == tileColour

    private val tileAndBackgroundColoursClash
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

    protected fun getTextColour(backgroundColour: String): Int {
        //This function dependent on what the background color is, returns a color for the text so the user can see the text
        return when (backgroundColour) {
        // White, light blue, blue, pink, orange, green
            "#ffffff", "#217ca3", "#0000FF", "#f22ee8", "#f1992e", "#008000" -> {
                Color.BLACK
            }
            else -> {
                Color.WHITE
            }
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