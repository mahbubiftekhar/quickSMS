package quick.SMS

import com.nhaarman.mockito_kotlin.*
import org.amshove.kluent.*
import org.junit.*

// From: http://www.singhajit.com/testing-android-database/
class DatabaseHelperTest : AndroidTest() {

    lateinit var database : DatabaseHelper

    @Before
    fun setUp() {
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME)
        database = DatabaseHelper(context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun test() {
        database shouldNotBe null
    }

}