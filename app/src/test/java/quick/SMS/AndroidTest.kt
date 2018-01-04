package quick.SMS

import android.app.Application
import android.content.Context
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

/**
 * From: https://fernandocejas.com/2017/02/03/android-testing-with-kotlin/
 * Base class for Robolectric data layer tests.
 * Inherit from this class to create a test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = AndroidTest.ApplicationStub::class,
        sdk = intArrayOf(19))
abstract class AndroidTest {

    val context by lazy { RuntimeEnvironment.application }

    internal class ApplicationStub : Application()
}
