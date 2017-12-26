package quick.SMS
/**
 * Created by MAHBUBIFTEKHAR on 26/12/2017.
 */
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase


class DatabaseTiles(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (receipient_id LONG PRIMARY KEY,tileid INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertData(receipient_id:Long, tileid: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, receipient_id)
        contentValues.put(COL_2, tileid)
        db.insert(TABLE_NAME, null, contentValues)
    }

    fun getRecipient (tileid: Int): Long{
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
        while(res.moveToNext()){
            if(tileid.toString() == res.getString(res.getColumnIndex("tileid"))){
               return res.getLong(res.getColumnIndex("receipient_id"))
            }
        }
        return 0L
    }
    fun deleteTileAndRecipeintData(tileid: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "receipient_id = ?", arrayOf(tileid.toString()))
    }

    @SuppressWarnings("UNUSED")
    fun deleteEntireDB() {
        /*USE THIS FUNCTION WISELY, WITH GREAT POWER COMES GREAT RESPONSIBILITY*/
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }
    companion object {
        val DATABASE_NAME = "TilesMaping.db"
        val TABLE_NAME = "TilesMappingTable"
        val COL_1 = "receipient_id"
        val COL_2 = "tileid"
    }
}