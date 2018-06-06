@file:Suppress("unused")

package quick.sms.quicksms.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class DatabaseTiles(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
     /*

       This is a short global explanation how the db functions have been made atomic - the reason atomicity is important is to
       maintain the integrity of the app - no database, the app will become a waste of space :-)
       try {
           db.beginTransaction() //Start the databaseTransaction
           //Does the database work whatever it may be
           db.setTransactionSuccessful() //Set the transaction as successful, hence when db.endTransaction() is called, the changes will be applied

       } catch(e :Exception){
           /*If we get into this block something has happened, finally will be cancelled, but as we didn't call
           db.setTransactionSuccessful() changes will not be made, hence atomic operation */
       } finally {
          db.endTransaction() // End the database transaction, make changes iff db.setTransactionSuccessful()

       }
        */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (recipient_id LONG,tileid INTEGER PRIMARY KEY, prefered_number TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getPreferedNum(tileid: Int): String {
        // Given the users ID, return the users prefered phone number
        //This is being serarched linearly which is slow, but given that the the list will be probably at most 15, it is good for the job
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                if (it.getInt(it.getColumnIndex("tileid")) == tileid) { //If the receipientID matches
                    return it.getString(it.getColumnIndex("prefered_number"))//Return the prefered number, else continue
                }
            }

        }
        return ""

    }

    fun insertData(recipient_id: Long, tileid: Int, prefered_number: String) {
        /*WIll insert the data regardless of whether that field already has something in it, this exception is handledd*/
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            contentValues.put(COL_1, recipient_id)
            contentValues.put(COL_2, tileid)
            contentValues.put(COL_3, prefered_number)
            val a = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE) //1 if already exists
            if (a == (-1).toLong()){
                //If an entry already exists we notice this, and we simply update the database entry instead
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
        /*This function will act as a sort of defragmentation

         E.g.

         1 2 3 4 5
         A B C D E

         If we delete tile 4, then this function will update the database to the following.

         It handles the deletion and shifting, this is to ensure atomicity

         Step one  - Delete
         1 2 3     4      5
         A B C {Deleted} {E}

         Step two - defrag by moving position 5 to position 4
         1 2 3 4 5
         A B C E {Empty}

         It's as simple as that!
         */
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, "tileid = ?", arrayOf(deletedTile.toString()))
            db.rawQuery("select * from $TABLE_NAME", null).use {
                while (it.moveToNext()) {
                    if (it.getInt(it.getColumnIndex("tileid")) > deletedTile) {
                        val contentValues = ContentValues()
                        contentValues.put(COL_1, it.getLong(it.getColumnIndex("recipient_id")))
                        contentValues.put(COL_2, it.getInt(it.getColumnIndex("tileid")) - 1)
                        contentValues.put(COL_3, it.getString(it.getColumnIndex("prefered_number")))
                        val a = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
                        if (a == (-1).toLong()) {
                            contentValues.put(COL_1, it.getLong(it.getColumnIndex("recipient_id")))
                            contentValues.put(COL_2, (it.getInt(it.getColumnIndex("tileid")) - 1))
                            contentValues.put(COL_3, it.getString(it.getColumnIndex("prefered_number")))
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
        //Returns all tile mappings to recipient_id's
        val db = this.writableDatabase
        db.rawQuery("select * from $TABLE_NAME", null).use {
            val tiles = linkedMapOf<Long, Int>()
            while (it.moveToNext()) {
                tiles[it.getLong(it.getColumnIndex("recipient_id"))] = it.getInt(it.getColumnIndex("tileid")) //Add to tiles linkedMap
            }
            return tiles //return map of <receiptId, tileID>
        }
    }

    fun deleteTile(tileid: Int) {
        //This function will delete a single tile
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, "tileid = ?", arrayOf(tileid.toString())) //Delete the tile with that specific tileID
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // do some error handling
        } finally {
            db.endTransaction()
        }
    }

    fun deleteEntireDB() {
        //This function will delete the entire database simply, this is used to reset the app
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