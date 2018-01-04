package quick.SMS

import android.content.Context
import com.nhaarman.mockito_kotlin.*
import org.amshove.kluent.*
import org.junit.*

class DatabaseTest<D: DatabaseHelper>(val factory : (ctx: Context) -> D): AndroidTest() {

}