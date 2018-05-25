package quick.sms.quicksms.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class DatabaseTiles(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (recipient_id LONG,tileid INTEGER PRIMARY KEY, prefered_number INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getPreferedNum(receipientID: Long): String {
        // Given the users ID, return the users prefered phone number
        //This is being serarched linearly which is slow, but given that the the list will be probably at most 15, it is good for the job
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.rawQuery("select * from $TABLE_NAME", null).use {
                while (it.moveToNext()) {
                        if(it.getLong(it.getColumnIndex("recipient_id")) == receipientID){ //If the receipientID matches
                            return it.getInt(it.getColumnIndex("prefered_number")).toString() //Return the prefered number, else continue
                        }
                    }
                }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {

        } finally {
            db.endTransaction()
        }
        return ""
    }

    fun insertData(recipient_id: Long, tileid: Int, prefered_number: Int) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            contentValues.put(COL_1, recipient_id)
            contentValues.put(COL_2, tileid)
            contentValues.put(COL_3, prefered_number)
            val a = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
            if (a == (-1).toLong()) {
                contentValues.put(COL_1, recipient_id)
                contentValues.put(COL_2, tileid)
                contentValues.put(COL_3, prefered_number)
                db.update(TABLE_NAME, contentValues, "tileid = ?", arrayOf(tileid.toString()))
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {

        } finally {
            db.endTransaction()
        }
    }


    fun tileDefragmentator(deletedTile: Int) {
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            //Code to do the deed
            db.delete(TABLE_NAME, "tileid = ?", arrayOf(deletedTile.toString()))
            db.rawQuery("select * from $TABLE_NAME", null).use {
                while (it.moveToNext()) {
                    if (it.getInt(it.getColumnIndex("tileid")) > deletedTile) {
                        val contentValues = ContentValues()
                        contentValues.put(COL_1, it.getLong(it.getColumnIndex("recipient_id")))
                        contentValues.put(COL_2, it.getInt(it.getColumnIndex("tileid")) - 1)
                        contentValues.put(COL_3, it.getInt(it.getColumnIndex("prefered_number")))
                        val a = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
                        if (a == (-1).toLong()) {
                            contentValues.put(COL_1, it.getLong(it.getColumnIndex("recipient_id")))
                            contentValues.put(COL_2, (it.getInt(it.getColumnIndex("tileid")) - 1))
                            contentValues.put(COL_3, it.getInt(it.getColumnIndex("prefered_number")))
                            db.updateWithOnConflict(TABLE_NAME, contentValues, "tileid = ?", arrayOf((it.getInt(it.getColumnIndex("tileid")) - 1).toString()), SQLiteDatabase.CONFLICT_IGNORE)
                        }
                        db.delete(TABLE_NAME, "tileid = ?", arrayOf(it.getInt(it.getColumnIndex("tileid")).toString()))
                    }
                }
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {

        } finally {
            db.endTransaction() //Ending the transaction applies the changes iff the transaction was set successful
        }
    }

    fun getAllTiles(): Map<Long, Int> {
        //Returns all tile mappings to reccipient_id's
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            val tiles = linkedMapOf<Long, Int>()
            while (it.moveToNext()) {
                tiles[it.getLong(it.getColumnIndex("recipient_id"))] = it.getInt(it.getColumnIndex("tileid"))
            }
            return tiles
        }
    }

    fun deleteTile(tileid: Int) {
        //This function will delete a single tile
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, "tileid = ?", arrayOf(tileid.toString()))
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // do some error handling
        } finally {
            db.endTransaction()
        }
    }

    fun deleteEntireDB() {
        //This function will delete the entire database
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

    companion object {
        private const val DATABASE_NAME = "TilesMapping.db"
        private const val TABLE_NAME = "TilesMappingTable"
        private const val COL_1 = "recipient_id"
        private const val COL_2 = "tileid"
        private const val COL_3 = "prefered_number"
    }
}