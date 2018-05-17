package quick.sms.quicksms.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseMessages(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    // TODO: See DatabaseTiles
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY UNIQUE,recipient_id LONG,message TEXT)")
    }

    // TODO: Same here
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(dbID: Int, recipientId: Long, message: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, dbID)
        contentValues.put(COL_2, recipientId)
        contentValues.put(COL_3, message)
        db.insert(TABLE_NAME, null, contentValues)
    }

    // TODO: Why does this return a boolean
    fun updateData(id: String, recipientId: Long, message: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, id)
        contentValues.put(COL_2, recipientId)
        contentValues.put(COL_3, message)
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    // TODO: This doesn't need to be nullable
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
                if (recipientId.toString() == it.getString(it.getColumnIndex("recipient_id"))) {
                    messages[it.getInt(it.getColumnIndex("id"))] =
                            it.getString(it.getColumnIndex("message"))
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
        db.delete(TABLE_NAME, null, null)
    }

    fun deleteRecipient(recipientId: Long){
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                if (recipientId.toString() == it.getString(it.getColumnIndex("recipient_id"))) {
                    db.delete(TABLE_NAME, "id = ?",
                            arrayOf(it.getString(it.getColumnIndex("id"))))
                }
            }
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