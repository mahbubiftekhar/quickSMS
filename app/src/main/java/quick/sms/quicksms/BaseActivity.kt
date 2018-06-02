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
import quick.sms.quicksms.ui.*

open class BaseActivity : AppCompatActivity() {

    // Activities that shouldn't automatically inherit a menu
    private val excludedActivities = setOf("SettingsActivity", "SplashActivity", "FallbackActivity",
            "BugReportActivity", "LogActivity")

    // Shared Preference Accessors

    // Number of tiles currently on screen
    protected var nTiles
        get() = prefs.getInt("nTiles", 0)
        set(value) = editor.putIntAndCommit("nTiles", value)

    // Should a contacts name be displayed on top of their picture
    protected val showName
        get() = settings.getBoolean("ShowName", false)

    // Action bar colour
    private val actionBarColour
        get() = settings.getString("actionbarcolour", "#303F9F")

    // Background colour
    protected val backgroundColour: String
        get() = settings.getString("backgroundcolour", "#217ca3")

    /* Text colour on the tiles (Always on the ones without pictures, on the ones with pictures when
     * showName is true) */
    protected val tileTextColour: String
        get() = settings.getString("TileTextColour", "#000000")

    // Colour of the tiles without pictures
    protected val tileColour: String
        get() = settings.getString("tilecolour", "#ffffff")

    // Should we warn the user if the colours chosen would make text unreadable
    private val warnColourClash
        get() = settings.getBoolean("ColourCombination", true)

    // Should we vibrate when the user sends, adds or updates a message
    protected val shouldVibrate
        get() = settings.getBoolean("Vibrate", false)

    // Should we make a sound when the user sends, adds or updates a message
    protected val shouldMakeSound
        get() = settings.getBoolean("Sound", true)

    // Should we prompt before sending an SMS
    protected val shouldDoubleCheck
        get() = settings.getBoolean("DoubleCheck", false)

    private val textAndTileColoursClash
        get() = tileTextColour == tileColour

    private val tileAndBackgroundColoursClash
        get() = backgroundColour == tileColour

    // Text colour directly on the background, not configurable by the user
    protected val textColour
        get() = when (backgroundColour) {
            // White, light blue, blue, pink, orange, green
            "#ffffff", "#217ca3", "#0000FF", "#f22ee8", "#f1992e", "#008000" -> Color.BLACK
            else -> Color.WHITE
        }

    protected fun resetApp() {
        // Prompt before actually resetting
        alert("Are you sure you wish to reset the app?") {
            positiveButton("Yes") {
                alert("Do you wish to proceed?") {
                    title = "NOTE: This action is IRREVERSIBLE"
                    positiveButton("Yes proceed, RESET APP") {
                        doAsync {
                            // Clear all databases
                            DatabaseMessages(this@BaseActivity).deleteEntireDB()
                            DatabaseTiles(this@BaseActivity).deleteEntireDB()
                            DatabaseLog(this@BaseActivity).deleteEntireDB()
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

    @SuppressLint("ObsoleteSdkInt")
    override fun recreate() {
        // TODO: This must always be false
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            super.recreate()
        } else {
            startActivity(intent)
            finish()
        }
    }

    fun setActionBarColour() {
        // Set the action bar colour
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(actionBarColour)))
    }

    fun colourCheck() {
        // Only warn if the warning hasn't been turned off
        if (warnColourClash) {
            // Generate the correct warning based on the combination of clashes
            val dialogText = if (textAndTileColoursClash && tileAndBackgroundColoursClash) {
                "You are using the same colours for your the background, tiles and text\n"
            } else if (textAndTileColoursClash) {
                "You are using the same colours for your the tiles and text\n"
            } else if (tileAndBackgroundColoursClash) {
                "You are using the same colours for your the background and tiles\n"
            } else {
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
    final override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menu?.let {
            // Inflate anything that should be prepended to the common menu
            menuPrepend(it)
            // For excluded classes the common menu is empty
            if (!excludedActivities.contains(this::class.simpleName)) {
                menuInflater.inflate(R.menu.menu, it)
            }
            // Inflate anything that should be appended to the common menu
            menuAppend(it)
            true
        } ?: false
    }

    final override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        /* Actions for the common menu. None of the extension menus should have id's that clash
         * with these as there is no namespacing, if the common menu isn't inflated these have no
         * adverse effects */
        R.id.menu_item_share -> {
            //Allow the users to share the app to their friends/family
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBodyText = "Check it out, quickSMS saves me so much time! Download it for FREE from the Google Play store! https://play.google.com/store/apps/details?id=quick.sms.quicksmsLaunch"
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check it out! quickSMS")
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText)
            startActivity(Intent.createChooser(sharingIntent, "Sharing Options"))
            true
        }

        R.id.action_settings -> {
            startActivity<SettingsActivity>()
            true
        }

        R.id.faqButton -> {
            startActivity<FaqActivity>()
            true
        }

        R.id.contactLog -> {
            startActivity<LogActivity>()
            true
        }

        R.id.about -> {
            startActivity<AboutDevelopersActivity>()
            true
        }

        R.id.contactButton -> {
            startActivity<ContactUsActivity>()
            true
        }

        R.id.sync -> {
            // Restart splash and let the app do the rest
            finish()
            startActivity<SplashActivity>()
            true
        }

        R.id.resetApp -> {
            resetApp()
            true
        }

        /* Extended options is the method subclasses should override to add actions to their menu
         * extensions */
        else -> extendedOptions(item)
    }

    open fun menuPrepend(menu: Menu) {}
    open fun menuAppend(menu: Menu) {}

    open fun extendedOptions(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}