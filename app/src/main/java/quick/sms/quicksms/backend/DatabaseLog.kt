package quick.sms.quicksms.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class DatabaseLog(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY UNIQUE,recipient_id LONG,message TEXT,receipientName TEXT,phoneNumber TEXT,timeStamp TEXT )")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(dbID: Int, recipientId: Long, message: String, receipientName: String, phoneNumber: String) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            contentValues.put(COL_1, dbID)
            contentValues.put(COL_2, recipientId)
            contentValues.put(COL_3, message)
            contentValues.put(COL_4, receipientName)
            contentValues.put(COL_5, phoneNumber)
            contentValues.put(COL_6, System.currentTimeMillis())
            db.insert(TABLE_NAME, null, contentValues)
            db.setTransactionSuccessful()
        } catch (e: SQLException) {

        } finally {
            db.endTransaction()
        }

    }

    fun returnAll(): List<String>? {
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            val messages = mutableListOf<String>()
            while (it.moveToNext()) {
                    messages.add("Message: "+it.getString(it.getColumnIndex("message")) +"\n"+"TimeStamp: "+it.getString(it.getColumnIndex("timeStamp"))
                            +"\n"+"Receipient Name: "+it.getString(it.getColumnIndex("receipientName"))
                            +"\n"+"phoneNumber: "+it.getString(it.getColumnIndex("phoneNumber"))
                    )
            }
            return messages
        }
    }

    fun deleteData(id: String) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, "id = ?", arrayOf(id))
            db.setTransactionSuccessful()
        } catch (e:SQLException){

        } finally {
            db.endTransaction()
        }
    }

    fun deleteEntireDB() {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, null, null)
            db.setTransactionSuccessful()
        } catch (e: SQLException) {

        } finally {
            db.endTransaction()
        }
    }

    fun deleteRecipient(recipientId: Long) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.rawQuery("select * from $TABLE_NAME", null).use {
                while (it.moveToNext()) {
                    if (recipientId.toString() == it.getString(it.getColumnIndex("recipient_id"))) {
                        db.delete(TABLE_NAME, "id = ?", arrayOf(it.getString(it.getColumnIndex("id"))))
                    }
                }
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // do some error handling
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        private const val DATABASE_NAME = "databaseLog.db"
        private const val TABLE_NAME = "databaseLogTable"
        private const val COL_1 = "id"
        private const val COL_2 = "recipient_id"
        private const val COL_3 = "message"
        private const val COL_4 = "receipientName"
        private const val COL_5 = "phoneNumber"
        private const val COL_6 = "timeStamp"
    }
}