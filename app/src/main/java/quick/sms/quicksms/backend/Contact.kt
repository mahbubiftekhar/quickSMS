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
            doAsync {
                val androidDB = ctx.contentResolver
                val tilesDB = DatabaseTiles(ctx)
                val numbers = getPhoneNumbers(androidDB)
                val tiles = tilesDB.getAllTiles()
                val contacts = lookupContacts(androidDB, numbers, tiles)

                uiThread {
                    then(contacts)
                }
            }
        }

        private fun getPhoneNumbers(db: ContentResolver): Map<Long, List<String>> {
            val result = db.toList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI) {
                PhoneNumber(
                        it[ContactsContract.CommonDataKinds.Phone.CONTACT_ID] as Long,
                        it[ContactsContract.CommonDataKinds.Phone.NUMBER] as String
                )
            }
            val numbers = mutableMapOf<Long, List<String>>()
            for (number in result) {
                val id = number.id
                val numlist = numbers.getOrDefault(id, emptyList()) + number.number
                numbers[id] = numlist
            }
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