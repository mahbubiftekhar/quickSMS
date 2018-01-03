package quick.SMS

import android.content.ContentResolver
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Parcel
import android.provider.ContactsContract
import com.nhaarman.mockito_kotlin.*
import org.amshove.kluent.*
import org.junit.*

class ContactTest : AndroidTest() {

    lateinit var simpleContact : Contact
    lateinit var withNullImage : Contact

    val correctId = 0L
    val correctName = "Name"
    val correctImage = "Image"
    val correctNumbers = listOf("Number1", "Number2")
    val correctTile = 0

    val phoneCursor = MatrixCursor(arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    ))
    val phoneDB = mock<ContentResolver> {
        on { query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
        ) } doReturn phoneCursor
    }

    val contactCursor = MatrixCursor(arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            // TODO: ContactsContract.Contacts.PHOTO_URI doesn't work here either
            "photo_uri",
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    ))
    val contactDB = mock<ContentResolver> {
        on { query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
        ) } doReturn contactCursor
    }

    @Before
    fun setup() {
        simpleContact = Contact(correctId, correctName, correctImage, correctNumbers, correctTile)
        withNullImage = Contact(correctId, correctName, null, correctNumbers, correctTile)

        phoneCursor.addRow(arrayOf(1L, "Number1"))
        phoneCursor.addRow(arrayOf(2L, "Number2"))
        phoneCursor.addRow(arrayOf(1L, "Number3"))

        contactCursor.addRow(arrayOf(1L, "Name1", "PhotoURI1", 1L))
        contactCursor.addRow(arrayOf(2L, "Name2", "PhotoURI2", 1L))
        contactCursor.addRow(arrayOf(3L, "Name3", "PhotoURI3", 0L))
        contactCursor.addRow(arrayOf(4L, null, "PhotoURI4", 1L))
        contactCursor.addRow(arrayOf(5L, "Name5", null, 1L))
    }

    @Test
    fun constructorTest() {
        simpleContact.id shouldEqual correctId
        withNullImage.id shouldEqual correctId
        simpleContact.name shouldEqual correctName
        withNullImage.name shouldEqual correctName
        simpleContact.numbers shouldEqual correctNumbers
        withNullImage.numbers shouldEqual correctNumbers
        simpleContact.tile shouldEqual correctTile
        withNullImage.tile shouldEqual correctTile
        simpleContact.image shouldEqual correctImage
        withNullImage.image shouldEqual "NONE"
    }

    @Test
    fun parcelableTest() {
        val parcel = Parcel.obtain()
        simpleContact.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val fromParcel = Contact.CREATOR.createFromParcel(parcel)
        fromParcel shouldEqual simpleContact
    }

    @Test
    fun testPhoneNumberParsing() {
        val expected = mapOf(
                1L to listOf("Number1", "Number3"),
                2L to listOf("Number2")
        )
        val result = Contact.getPhoneNumbers(phoneDB)
        result shouldEqual expected
    }

    @Test
    fun testContactParsing() {
        val expected = listOf(
                Contact(1L, "Name1", "PhotoURI1", emptyList(), -1),
                Contact(2L, "Name2", "PhotoURI2", emptyList(), -1),
                Contact(5L, "Name5", "NONE", emptyList(), -1)
        )
        val result = Contact._getContactsBase(contactDB, emptyMap(), emptyMap())
        result shouldEqual expected
    }
}