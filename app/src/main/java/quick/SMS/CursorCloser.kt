
package quick.SMS

import android.database.Cursor


/* It seems there's a bug in use, this fixes it: */
/* https://discuss.kotlinlang.org/t/why-cursor-use-does-not-compiles/5454/4 */
public inline fun <T : Cursor?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            this?.close()
        } catch (closeException: Exception) {
        }
        throw e
    } finally {
        if (!closed) {
            this?.close()
        }
    }
}