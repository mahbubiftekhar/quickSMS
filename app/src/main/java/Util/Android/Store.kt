package Util.Android

import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

inline fun <reified S, reified T> storeFor(key : String) : Store<S, T> {
    return Store<S, T>(key, S::class.java, T::class.java)
}

/* Serializer for Collections. Requires a concrete implementation of the desired
 * collection type (e.g ArrayList not List). Be aware get() returns a generic
 * type (e.g ArrayList not ArrayList<String>). For use to be typechecked this
 * needs to be assigned to a typed variable, that may cause a warning */

class Store<S, T> (private val key : String,
                   private val base : Class<S>,
                   private val param : Class<T>) {

    private val gson = Gson()

    var contents
        get() : S {
            val json = prefs.getString(key, "")
            // The CollectionOfJson tells Gson what type the element of the collection should be
            val collection : S = gson.fromJson(json, CollectionOfJson<S, T>(base, param))
            return collection ?: makeNew(base)
        }
        set(collection : S) {
            val json = gson.toJson(collection, CollectionOfJson<S, T>(base, param))
            editor.putStringAndCommit(key, json)
        }

    fun wipe() {
        editor.removeAndCommit(key)
    }

    private fun makeNew(base : Class<S>) : S {
        return base.newInstance()
    }

    // From https://stackoverflow.com/a/26394917
    private class CollectionOfJson<U, V>(private val container : Class<U>,
                                         private val contained : Class<V>) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(contained)
        }

        override fun getRawType(): Type {
            return container
        }

        override fun getOwnerType(): Type {
            return Store::class.java
        }
    }
}
