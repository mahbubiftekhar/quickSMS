package quick.SMS

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            //HERE: When compiling fragmentManager is unresolved reference which makes no sense
            if (fragmentManager.findFragmentById(android.R.id.content) == null) {
                fragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
            }
        } catch (e:Exception){

        }

    }


    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.app_preferences)
        }
    }
}
