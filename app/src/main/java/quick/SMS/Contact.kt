package quick.SMS

import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class Contact private constructor(val id: Long, val name: String,
                                  private val nullableImage: String?, val numbers: List<String>,
                                  val tile: Int) : Parcelable {
    // Abusing lazy for a neat way of producing a Delegate
    val image by lazy { nullableImage ?: "NONE" } // Generate default image URI here

    override fun toString(): String = "Contact(id=$id, name=$name, image=$image, numbers=$numbers)"

    // Parcelable code

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readString(),
            source.readString(),
            source.createStringArrayList(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeString(nullableImage)
        writeStringList(numbers)
        writeInt(tile)
    }

    // Anything that uses context must go in here to preserve parcelable
    companion object {
        fun getContacts(ctx: Context, then: (List<Contact>) -> Unit) {
            doAsync {
                // All contacts saved on the device in raw form
                val result = ctx.contentResolver.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null
                ).use {
                    /* Parse into an intermediate form where the name can be null and we don't
                     * know if there are any phone numbers */
                    it.parseList(object : MapRowParser<NullableContact> {
                        override fun parseRow(columns: Map<String, Any?>): NullableContact {
                            return NullableContact(
                                    columns[ContactsContract.Contacts._ID] as Long,
                                    columns[ContactsContract.Contacts.DISPLAY_NAME] as? String,
                                    /* TODO: ContactsContract.Contacts.PHOTO_URI still isn't working,
                                 * this is the actual value */
                                    columns["photo_uri"] as? String,
                                    columns[ContactsContract.Contacts.HAS_PHONE_NUMBER] as Long)
                        }
                    })
                }

                val phoneNumbers = getPhoneNumbers(ctx)

                /* Remove Contacts with null names or no phone number, sort by name and convert to
                 * null safe Contacts */
                val contacts = result
                        .filter { it.name != null && it.hasNumber == 1L }
                        .sortedBy { it.name }
                        .map { Contact.new(ctx, it.id, it.name!!, it.image,
                                phoneNumbers[it.id] ?: emptyList()) }

                // Send them to the callback
                uiThread {
                    then(contacts)
                }
            }
        }

        private fun new(ctx: Context, id: Long, name: String, image: String?,
                        numbers: List<String>): Contact {
            val databaseTiles = DatabaseTiles(ctx)
            val tile = getTile(id, databaseTiles)

            return Contact(id, name, image, numbers, tile)
        }

        private fun getPhoneNumbers(ctx: Context): Map<Long, List<String>> {
            val result = ctx.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null
            ).use {
                it.parseList(object : MapRowParser<PhoneNumber> {
                    override fun parseRow(columns: Map<String, Any?>): PhoneNumber {
                        return PhoneNumber(
                                columns[ContactsContract.CommonDataKinds.Phone.CONTACT_ID] as Long,
                                columns[ContactsContract.CommonDataKinds.Phone.NUMBER] as String
                        )
                    }
                })
            }

            val numbers = mutableMapOf<Long, List<String>>()
            for (number in result) {
                val id = number.id
                val numlist = numbers.getOrDefault(id, listOf<String>()) + number.number
                numbers.put(number.id, numlist)
            }
            return numbers.toMap()
        }

        private fun getTexts(recipient_id: Long, db: DatabaseHelper): List<String> {
            val textMessages = db.returnAll(recipient_id)
            db.close()
            return textMessages ?: emptyList()
        }

        private fun getTile(recipient_id: Long, db: DatabaseTiles): Int {
            /* getTile will return -1 if it cannot find a tile corresponding to that particular user,
             * otherwise it will return the index for that tile */
            return db.getTile(recipient_id) // TODO: Why doesn't this need to be closed
        }

        private data class NullableContact(val id: Long, val name: String?, val image: String?,
                                           val hasNumber: Long)

        @JvmField val CREATOR: Parcelable.Creator<Contact> = object : Parcelable.Creator<Contact> {
            override fun createFromParcel(source: Parcel): Contact = Contact(source)
            override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
        }
    }

    private data class PhoneNumber(val id: Long, val number: String)
}
