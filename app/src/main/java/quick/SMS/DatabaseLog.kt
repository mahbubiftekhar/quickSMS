package quick.SMS

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.parseList
import java.sql.Timestamp

class DatabaseLog(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT,message TEXT,recipient_id LONG,recipient_name TEXT,timestamp TEXT)")
        println("this has gotten done")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /*This will actually drop the table completely, slightly different from delete, drop will get rid of the table
        * whilst deleteAll will remove the contents only*/
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertData(type: String, message: String, recipient_id: Long, recipient_name: String) {
        /*THE ID - WHICH IS COL_1 - IS TAKEN CARE OF AUTOMATICALLY, THE TIMESTAMP IS ALSO DONE AT THIS
        * LEVEL IN ALL INSTANCES - UPDATING AND INSERTING - HENCE NO NEED TO WORRY ABOUT IT AT A HIGHER LEVEL*/
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, type)
        contentValues.put(COL_3, message)
        contentValues.put(COL_4, recipient_id)
        contentValues.put(COL_5, recipient_name)
        val timestamp = Timestamp(System.currentTimeMillis())
        contentValues.put(COL_6, timestamp.toString())

        db.insert(TABLE_NAME, null, contentValues)

    }


    fun updateData(id: String, type: String, message: String, recipient_id: Long, recipient_name: String): Boolean {
        /*This function returns true when it is completed - I don't see an immediate use for this, its more having the option*/
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, id)
        contentValues.put(COL_2, type)
        contentValues.put(COL_3, message)
        contentValues.put(COL_4, recipient_id)
        contentValues.put(COL_5, recipient_name)
        val timestamp = Timestamp(System.currentTimeMillis())

        contentValues.put(COL_6, timestamp.toString())
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    fun returnAllLog(recipient_id: Long): List<Map<String,String>> {
        val db = this.writableDatabase
        val res = db.rawQuery("select * from $TABLE_NAME WHERE recipient_id=$recipient_id", null)
        val numbers = res.parseList(object: RowParser<Map<String, String>> {
            override fun parseRow(columns: Array<Any?>) : Map<String, String> {
                val ret = mapOf(
                        "Type" to columns[1] as String,
                        "Recipient" to columns[4] as String,
                        "Date & Time" to columns[5] as String,
                        "Message" to columns[2] as String )
                return ret
            }
        })
        res.close() /* Freeing the cursor */
        return numbers
    }

    fun returnAllLog(): List<Map<String,String>> {
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + TABLE_NAME, null)
        val numbers = res.parseList(object: RowParser<Map<String, String>> {
            override fun parseRow(columns: Array<Any?>) : Map<String, String> {
                val ret = mapOf(
                        "Type" to columns[1] as String,
                        "Recipient" to columns[4] as String,
                        "Date & Time" to columns[5] as String,
                        "Message" to columns[2] as String )
                return ret
            }
        })
        res.close() /* Freeing the cursor */
        return numbers
    }

    fun deleteDataLog(id: String): Int {
        /*Delete a particular log, where the ID matches - please not this is the unique ID,
        * NOT the recipient_id, to do that use deleteRecipient()*/
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id))
    }

    fun closeDatabaseHelperLog() {
        /* This function is simply to close the database - just good practise, plus ensures
         * that all transactions are completed properly */
        val db = this.writableDatabase
        db.close()
    }

    @SuppressWarnings("UNUSED")
    fun deleteEntireDBLog() {
        /* USE THIS FUNCTION WISELY, WITH GREAT POWER COMES GREAT RESPONSIBILITY */
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }

    fun deleteRecipient(recipient_id: Long) {
        /*This function takes a recipient_id and removes all rows with that recipient id*/
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + TABLE_NAME, null) /*Takes O(n) time*/

        while (res.moveToNext()) { /* Takes O(n) time*/
            if (recipient_id.toString() == res.getString(res.getColumnIndex("recipient_id"))) {
                db.delete(TABLE_NAME, "id = ?", arrayOf(res.getString(res.getColumnIndex("id")))) /*Delete that particular row*/
            }
        }
        res.close()
    }

    companion object {
        val DATABASE_NAME = "Log.db"
        val TABLE_NAME = "LogTable"
        val COL_1 = "id"
        val COL_2 = "type"
        val COL_3 = "message"
        val COL_4 = "recipient_id"
        val COL_5 = "recipient_name"
        val COL_6 = "timestamp"
    }
}