package quick.sms.quicksms

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

    protected var nTiles
    get() = prefs.getInt("nTiles", 0)
    set(value) = editor.putIntAndCommit("nTiles", value)

    private val excludedActivities = setOf("SettingsActivity", "SplashActivity", "MainActivity",
            "FallbackActivity")

    fun getBackGroundColour(): String {
        //gets the users selected background colour
        return settings.getString("backgroundcolour", "#217ca3")
    }

    fun showName(): Boolean {
        //gets whether the user wants the contacts name for contacts with images
        return settings.getBoolean("ShowName", false)
    }

    fun setActionBarColour() {
        //gets the users selected actionbar colour
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(getActionBarColour())))
    }

    fun getTileTextColour(): String {
        //gets the users selected text colour
        return settings.getString("TileTextColour", "#000000")
    }

    fun soundBool(): Boolean {
        //Check if user wants sound
        return settings.getBoolean("Sound", true)
    }


    fun colourWarning(): Boolean {
        //Check if user wants the colour warning, this is a warning if conflicting colours are selected
        return settings.getBoolean("ColourCombination", true)
    }

    fun textAndTileColour(): Boolean {
        //This fucntion is to say if the user has both tileColour and text color the same
        return getTileTextColour() == gettileColour()
    }

    fun tileAndBackground(): Boolean {
        //This fucntion is to say if the user has both tileColour and backgorund color the same
        return getBackGroundColour() == gettileColour()
    }

    fun colourCheckFunction() {
        if (colourWarning()) { //If the user wants this wanrning
            if (textAndTileColour() && tileAndBackground()) {
                //Warn the user that they are using the same colour for background, textColour and tileColour
                alertDialogPopUo(1)
            } else if (textAndTileColour()) {
                //Warn the user if they are using same tile and text colour
                alertDialogPopUo(2)
            } else if (tileAndBackground()) {
                //Warn the user if they are using the same background and tile colour
                alertDialogPopUo(3)
            } else {
                //Do nothing, the colours are fine
            }
        }
    }

    fun alertDialogPopUo(dialogVersion: Int) {
        //This function shows dialogs dependent on the use as per colourCheckFunction
        val dialogText: String = when (dialogVersion) {
            1 -> {
                "You are using the same colours for your the background, tiles and text \n"
            }
            2 -> {
                "You are using the same colours for your the tiles and text \n"
            }
            else -> {
                "You are using the same colours for your the background and tiles \n"
            }
        }
        alert("You can disable this warning in settings") {
            title = dialogText
            positiveButton("Change settings") {
                startActivity<SettingsActivity>()
            }
        }.show()

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
        return settings.getString("tilecolour", "#ffffff")
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