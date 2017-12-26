package quick.SMS

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT,receipient_id LONG,message TEXT)")
        println("this has gotten done")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertData(receipient_id: Long, message: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, receipient_id)
        contentValues.put(COL_3, message)
        db.insert(TABLE_NAME, null, contentValues)

    }
    val allData: Cursor
        get() {
            val db = this.writableDatabase
            val res = db.rawQuery("select * from " + TABLE_NAME, null)
            return res
        }


    fun updateData(id: String, receipient_id: Long, message: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, id)
        contentValues.put(COL_2, receipient_id)
        contentValues.put(COL_3, message)
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    fun returnAll(receipient_id:Long): List<String>? {
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + TABLE_NAME, null)
        var messages: MutableList<String> = mutableListOf()
        while(res.moveToNext()){
            if(receipient_id.toString() == res.getString(res.getColumnIndex("receipient_id"))){
                /* So if the row is for this particular receipient_id, then we get the message and
                * we shall add it to the messages list */
                messages.add(res.getString(res.getColumnIndex("message"))) /*Adding to messages*/
            }
        }
        return messages
    }

    fun deleteData(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id))
    }


    fun deleteReceipient(receipient_id: Long){
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + TABLE_NAME, null)
        var toRemove: MutableList<String> = mutableListOf() /*Stores the id's of the messages we want to remove*/
        while(res.moveToNext()){
            if(receipient_id.toString() == res.getString(res.getColumnIndex("receipient_id"))){
                /*So if the row is for this particular receipient_id, then we get the message and
                * we shall add it to the messages list */
                println("$$$" + res.getString(res.getColumnIndex("id")))
                toRemove.add(res.getString(res.getColumnIndex("id")))
            }
        }
        for(i in 0..toRemove!!.size-1){
            /*remove the messages from the database completely*/
            deleteData(toRemove[i])
        }
    }
    companion object {
        val DATABASE_NAME = "Messages.db"
        val TABLE_NAME = "databasetable"
        val COL_1 = "id"
        val COL_2 = "receipient_id"
        val COL_3 = "message"
    }
}