package quick.SMS

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class Contact private constructor(val id: Long, val name: String,
                                  private val nullableImage: String?, val numbers: List<String>,
                                  val texts: List<String>, val tile: Int) : Parcelable {
    // Abusing lazy for a neat way of producing a Delegate
    val image by lazy { nullableImage ?: "NONE" } // Generate default image URI here

    override fun toString(): String = "Contact(id=$id, name=$name, image=$image, numbers=$numbers)"

    // Parcelable code

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readString(),
            source.readString(),
            source.createStringArrayList(),
            source.createStringArrayList(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeString(nullableImage)
        writeStringList(numbers)
        writeStringList(texts)
        writeInt(tile)
    }

    // Anything that uses context must go in here to preserve parcelable
    companion object {
        fun getContacts(ctx: Context, then: (List<Contact>) -> Unit) {
            doAsync {
                // All contacts saved on the device in raw form
                val result = ctx.contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null)

                /* Parse into an intermediate form where the name can be null and we don't know if
                 * there are any phone numbers */
                val parsed = result.parseList(object : MapRowParser<NullableContact> {
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

                /* Remove Contacts with null names or no phone number, sort by name and convert to
                 * null safe Contacts */
                val contacts = parsed
                        .filter { it.name != null && it.hasNumber == 1L }
                        .sortedBy { it.name }
                        .map { Contact.new(ctx, it.id, it.name!!, it.image) }

                // Send them to the callback
                uiThread {
                    then(contacts)
                }
            }
        }

        private fun new(ctx: Context, id: Long, name: String, image: String?): Contact {

            val databaseHelper = DatabaseHelper(ctx)
            val databaseTiles = DatabaseTiles(ctx)

            // TODO: Note to self, if this isn't fast enough it should be possible to make these lookups async in this function or inside the object
            val numbers = getPhoneNumbers(ctx, id)
            val texts = getTexts(id, databaseHelper)
            val tile = getTile(id, databaseTiles)

            return Contact(id, name, image, numbers, texts, tile)
        }

        private fun getPhoneNumbers(ctx: Context, id: Long): List<String> {
            val result = ctx.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id", null, null
            )
            val numbers = result.parseList(object : MapRowParser<String> {
                override fun parseRow(columns: Map<String, Any?>): String {
                    return columns[ContactsContract.CommonDataKinds.Phone.NUMBER] as String
                }
            })
            return numbers
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
}
