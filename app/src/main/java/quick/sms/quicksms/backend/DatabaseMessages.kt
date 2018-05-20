package quick.sms.quicksms.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class DatabaseMessages(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY UNIQUE,recipient_id LONG,message TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(dbID: Int, recipientId: Long, message: String) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            contentValues.put(COL_1, dbID)
            contentValues.put(COL_2, recipientId)
            contentValues.put(COL_3, message)
            db.insert(TABLE_NAME, null, contentValues)
            db.setTransactionSuccessful()
        } catch (e: SQLException) {

        } finally {
            db.endTransaction()
        }
    }

    fun updateData(id: String, recipientId: Long, message: String) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            contentValues.put(COL_1, id)
            contentValues.put(COL_2, recipientId)
            contentValues.put(COL_3, message)
            print(">>> $message")
            db.update(TABLE_NAME, contentValues, "id=$id", arrayOf(id))
            db.close()
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // do some error handling
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun returnAll(recipientId: Long): List<String>? {
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            val messages = mutableListOf<String>()
            while (it.moveToNext()) {
                if (recipientId.toString() == it.getString(it.getColumnIndex("recipient_id"))) {
                    messages.add(it.getString(it.getColumnIndex("message")))
                }
            }
            return messages
        }
    }

    fun returnAllHashMap(recipientId: Long): LinkedHashMap<Int, String> {
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            val messages: LinkedHashMap<Int, String> = linkedMapOf()
            while (it.moveToNext()) {
                println(">>>>id  "+it.getInt(it.getColumnIndex("id")))
                println(">>>>recipient_id  "+it.getLong(it.getColumnIndex("recipient_id")))
                println(">>>>message  "+it.getString(it.getColumnIndex("message")))
                if (recipientId.toString() == it.getString(it.getColumnIndex("recipient_id"))) {
                    messages[it.getInt(it.getColumnIndex("id"))] = it.getString(it.getColumnIndex("message"))
                }
            }
            return messages
        }
    }

    fun deleteData(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id))
    }

    fun deleteEntireDB() {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, null, null)
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // do some error handling
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
                        db.delete(TABLE_NAME, "id = ?",
                                arrayOf(it.getString(it.getColumnIndex("id"))))
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
        private const val DATABASE_NAME = "Messages.db"
        private const val TABLE_NAME = "databasetable"
        private const val COL_1 = "id"
        private const val COL_2 = "recipient_id"
        private const val COL_3 = "message"
    }
}