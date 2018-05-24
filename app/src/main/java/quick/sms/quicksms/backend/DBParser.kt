package quick.sms.quicksms.backend

import android.content.ContentResolver
import android.net.Uri
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList

inline fun <T : Any> ContentResolver.toList(uri : Uri, crossinline parse : (Map<String, Any?>) -> T)
        : List<T> {
    return this.query(uri, null, null, null, null).use {
        it.parseList(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>) : T {
                return parse(columns)
            }
        })
    }
}
