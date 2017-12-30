package quick.SMS

import android.content.Context
import android.provider.ContactsContract
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseList




// nullableImage is inaccessible, image == nullableImage if nullableImage != null
// else image == "NONE", the else part can be changed as appropriate to produce a default image
class Contact(private val ctx: Context, val id: Long, val name: String,
              private val nullableImage: String?) {
    // Abusing lazy for a neat way of producing a Delegate
    val image by lazy { nullableImage ?: "NONE" } // Generate default image URI here
    internal var dateBaseHelper = DatabaseHelper(ctx)
    internal var dataBaseTiles = DatabaseTiles(ctx)

    // These properties are looked up from their respective databases on first access and
    // cached for later use
    val numbers by lazy { getPhoneNumbers() }
    val texts by lazy { getTextsFromDB(id) }
    val tile by lazy { getTileFromDB(id) }

    override fun toString() : String = "Contact(id=$id, name=$name, image=$image, numbers=$numbers)"

    private fun getPhoneNumbers() : List<String> {
        val result = ctx.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id", null, null)
        val numbers = result.parseList(object: MapRowParser<String> {
            override fun parseRow(columns: Map<String, Any?>) : String {
                return columns[ContactsContract.CommonDataKinds.Phone.NUMBER] as String
            }
        })
        return numbers
    }

    private fun getTextsFromDB(receipient_id: Long) : List<String>? {
        val textMessages = dateBaseHelper.returnAll(receipient_id)
        dateBaseHelper.close()
        return textMessages
        // TODO: This should look up the texts in the relevant database
    }

    private fun getTileFromDB(receipient_id: Long) : Int {
        // TODO: This should look up the attached tile from the relavent database
        // TODO: Need a behavior for contacts with no tile
        return dataBaseTiles.getTile(receipient_id)
        /*Get file will return -1 if it cannot find a tile corresponding to that particular user,
        * otherwise it will return the index for that tile*/
    }

}