package quick.SMS

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT,recipient_id LONG,message TEXT)")
        println("this has gotten done")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertData(recipient_id: Long, message: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, recipient_id)
        contentValues.put(COL_3, message)
        db.insert(TABLE_NAME, null, contentValues)

    }

    fun updateData(id: String, recipient_id: Long, message: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, id)
        contentValues.put(COL_2, recipient_id)
        contentValues.put(COL_3, message)
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    fun returnAll(recipient_id:Long): List<String>? {
        /* This returns all the data, with the messages, in the form of a list
         * for a particular recipient */
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + TABLE_NAME, null)
        val messages: MutableList<String> = mutableListOf()
        while(res.moveToNext()){
            if(recipient_id.toString() == res.getString(res.getColumnIndex("recipient_id"))){
                /* So if the row is for this particular recipient_id, then we get the message and
                 * we shall add it to the messages list */
                messages.add(res.getString(res.getColumnIndex("message"))) /*Adding to messages*/
            }
        }
        res.close() /* Freeing the cursor */
        return messages
    }

    fun deleteData(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id))
    }

    fun closeDatabaseHelper(){
        /* This function is simply to close the database - just good practise, plus ensures
         * that all transactions are completed properly */
        val db = this.writableDatabase
        db.close()
    }

    @SuppressWarnings("UNUSED")
    fun deleteEntireDB() {
        /* USE THIS FUNCTION WISELY, WITH GREAT POWER COMES GREAT RESPONSIBILITY */
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }

    fun deleteRecipient(recipient_id: Long){
        /*This function takes a recipient_id and removes all rows with that recipient id*/
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + TABLE_NAME, null)

        while(res.moveToNext()){
          if(recipient_id.toString() == res.getString(res.getColumnIndex("recipient_id"))){
              println("will remove" + res.getString(res.getColumnIndex("id")))
              /*So if the row is for this particular recipient_id, then we get the message and we shall add it to the messages list */
              db.delete(TABLE_NAME, "id = ?", arrayOf(res.getString(res.getColumnIndex("id"))))
          }
        }
        res.close()
    }
    companion object {
        val DATABASE_NAME = "Messages.db"
        val TABLE_NAME = "databasetable"
        val COL_1 = "id"
        val COL_2 = "recipient_id"
        val COL_3 = "message"
    }
}