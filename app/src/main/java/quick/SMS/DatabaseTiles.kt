package quick.SMS

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


class DatabaseTiles(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (recipient_id LONG PRIMARY KEY,tileid INTEGER, prefered_number INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(recipient_id: Long, tileid: Int, prefered_number: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, recipient_id)
        contentValues.put(COL_2, tileid)
        contentValues.put(COL_3, prefered_number)
        db.insert(TABLE_NAME, null, contentValues)
    }

    private fun getRecipient(tileid: Int): Long {
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
        res.close() //closing the cursor
        while (res.moveToNext()) {
            if (tileid.toString() == res.getString(res.getColumnIndex("tileid"))) {
                return res.getLong(res.getColumnIndex("recipient_id"))
            }
        }
        return -1L
    }

    fun getAllTiles(): HashMap<Long, Int> {
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
        res.close()
        val tiles: HashMap<Long, Int> = linkedMapOf()
        while (res.moveToNext()) {
            tiles[res.getLong(res.getColumnIndex("recipient_id"))] = res.getInt(res.getColumnIndex("tileid"))
        }
        return tiles
    }

    fun copyData(fromTileId: Int, toTileId: Int) {


    }

    fun getTile(recipient_id: Long): Int {
        /*Given a receipient_id it will find the relevent tile ID*/
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
        res.close()
        while (res.moveToNext()) {
            if (recipient_id.toString() == res.getString(res.getColumnIndex("recipient_id"))) {
                return res.getInt(res.getColumnIndex("tileid"))
            }
        }
        db.close()
        return -1
    }

    fun deleteTile(tileid: Int) {
        /*This will delete the tile, the tile will be re-added if the user adds it again*/
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "tileid = ?", arrayOf(tileid.toString()))
    }

    fun deleteEntireDB() {
        /* USE THIS FUNCTION WISELY, WITH GREAT POWER COMES GREAT RESPONSIBILITY */
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }

    companion object {
        val DATABASE_NAME = "TilesMaping.db"
        val TABLE_NAME = "TilesMappingTable"
        val COL_1 = "recipient_id"
        val COL_2 = "tileid"
        val COL_3 = "preferednum"
    }
}