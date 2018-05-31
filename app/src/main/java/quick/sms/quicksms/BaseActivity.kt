package quick.sms.quicksms

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import quick.sms.quicksms.backend.*
import quick.sms.quicksms.ui.SettingsActivity

open class BaseActivity : AppCompatActivity() {
    
    @SuppressLint("ApplySharedPref")
    protected fun resetApp() {
        // Prompt before actually resetting
        alert("Are you sure you wish to reset the app?") {
            positiveButton("Yes") {
                alert("Do you wish to proceed?") {
                    title = "NOTE: This action is IRREVERSIBLE"
                    positiveButton("Yes proceed, RESET APP") {
                        doAsync {
                            // Clear all databases
                            val contactDB = DatabaseMessages(this@BaseActivity).deleteEntireDB()
                            val tilesDB = DatabaseTiles(this@BaseActivity).deleteEntireDB()
                            val log = DatabaseLog(this@BaseActivity).deleteEntireDB()
                            editor.clearAndCommit() // Clear all shared preferences
                            uiThread {
                                // Restart the app programmatically
                                val i = baseContext.packageManager
                                        .getLaunchIntentForPackage(baseContext.packageName)
                                i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(i)
                            }
                        }
                    }
                    negativeButton("No, cancel") {}
                }.show()
            }
            negativeButton("No") {}
        }.show()
    }

    // Activities that shouldn't automatically inherit a menu
    private val excludedActivities = setOf("SettingsActivity", "SplashActivity", "MainActivity",
            "FallbackActivity")

    // Shared Preference Accessors
    protected var nTiles
        get() = prefs.getInt("nTiles", 0)
        set(value) = editor.putIntAndCommit("nTiles", value)

    protected val showName
        get() = settings.getBoolean("ShowName", false)

    //get the action bar colour
    private val actionBarColour
        get() = settings.getString("actionbarcolour", "#303F9F")

    //get the background colour
    protected val backgroundColour: String
        get() = settings.getString("backgroundcolour", "#217ca3")

    //get the tile text colour
    protected val tileTextColour: String
        get() = settings.getString("TileTextColour", "#000000")

    //get boolean about weather the phone should vibrate
    protected val shouldVibrate
        get() = settings.getBoolean("Vibrate", false)

    //get boolean about weather the user wants a double check before sending SMS's
    protected val shouldDoubleCheck
        get() = settings.getBoolean("DoubleCheck", false)

    //get tileColour the user wants
    protected val tileColour: String
        get() = settings.getString("tilecolour", "#ffffff")

    //get bool about weather the use wants sound when they send a message, add a message or update a message
    protected val shouldMakeSound
        get() = settings.getBoolean("Sound", true)

    //get bool weather the user wants to be warned if the colour combination is bad
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