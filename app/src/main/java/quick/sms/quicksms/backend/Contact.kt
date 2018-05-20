package quick.sms.quicksms.backend

import Util.Android.toList
import android.content.ContentResolver
import android.content.Context
import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.jetbrains.anko.*

@Parcelize

class Contact(val id: Long, val name: String, private val nullableImage: String?, val numbers: List<String>, var tile: Int?) : Parcelable {

    @IgnoredOnParcel
    val image by lazy { nullableImage ?: "NONE" } // Generate default image URI here

    override fun toString(): String = "Contact(id=$id, name=$name, image=$image, numbers=$numbers)"

    // Anything that uses context must go in here to preserve parcelable
    companion object {
        fun getContacts(ctx: Context, then: (List<Contact>) -> Unit) {
            println(">>>> getting in here getContacts")
            doAsync {
                println(">>>> getting in here doAsync")
                val androidDB = ctx.contentResolver
                println(">>>> 1")
                val tilesDB = DatabaseTiles(ctx)
                println(">>>> 2")
                val numbers = getPhoneNumbers(androidDB) //TODO: We get stuck here Alex, don't know why
                // I can let you teamViewer into my laptop with the Android 4.4 (API 19) phone plugged in if that helps.
                println(">>>> 3")
                val tiles = tilesDB.getAllTiles()
                println(">>>> 4")
                val contacts = lookupContacts(androidDB, numbers, tiles)
                println(">>>> 5")
                println(">>>> finnishing doAsync")
                uiThread {
                    println("getting in here uiThread")
                    then(contacts)
                }
            }
        }

        private fun getPhoneNumbers(db: ContentResolver): Map<Long, List<String>> {
            println(">>>> gettingPhoneNumbers 1")
            val result = db.toList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI) {
                println(">>>> gettingPhoneNumbers 2")
                PhoneNumber(
                        it[ContactsContract.CommonDataKinds.Phone.CONTACT_ID] as Long,
                        it[ContactsContract.CommonDataKinds.Phone.NUMBER] as String
                )
            }
            println(">>>> gettingPhoneNumbers 3")
            val numbers = mutableMapOf<Long, List<String>>()
            println(">>>> gettingPhoneNumbers 4")
            for (number in result) {
                println(">>>> gettingPhoneNumbers 5")
                val id = number.id
                println(">>>> gettingPhoneNumbers 5-2")
                val numlist = numbers.getOrDefault(id, emptyList()) + number.number //TODO: Here Alex is a further breakdown of pericely we get stuck. We never get beyond >>>> gettingPhoneNumbers 5-2
                //TODO: I'm fairly sure that its not permissions, its just we never get the contacts, hence its possible to just freeze and never switch activity
                println(">>>> gettingPhoneNumbers 5-3")
                numbers[id] = numlist
            }
            println(">>>> gettingPhoneNumbers 6")
            return numbers.toMap()
        }

        private fun lookupContacts(db: ContentResolver, numbers: Map<Long, List<String>>, tiles: Map<Long, Int>)
                : List<Contact> {
            val result = db.toList(ContactsContract.Contacts.CONTENT_URI) {
                NullableContact(
                        it[ContactsContract.Contacts._ID] as Long,
                        it[ContactsContract.Contacts.DISPLAY_NAME] as? String,
                        it[ContactsContract.Contacts.PHOTO_URI] as? String,
                        it[ContactsContract.Contacts.HAS_PHONE_NUMBER] as Long
                )
            }.asSequence()
            return result
                    .filter { it.name != null && it.hasNumber == 1L }
                    .map {
                        Contact(it.id, it.name!!, it.image, numbers.getOrDefault(it.id, emptyList()), tiles[it.id])
                    }.sortedBy { it.name }.toList()
        }
    }

    private data class PhoneNumber(val id: Long, val number: String)
    private data class NullableContact(val id: Long, val name: String?, val image: String?,
                                       val hasNumber: Long)
}