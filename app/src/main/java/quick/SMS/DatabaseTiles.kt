package quick.SMS

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import android.widget.Toast

class DatabaseTiles(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (recipient_id LONG PRIMARY KEY,tileid INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
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

    fun closeDatabaseTiles() {
        /* This function is simply to close the database - just good practise, plus ensures
         * that all transactions are completed properly */
        val db = this.writableDatabase
        db.close()
    }

    fun deleteEntireDBTiles() {
        /* USE THIS FUNCTION WISELY, WITH GREAT POWER COMES GREAT RESPONSIBILITY */
        val db = this.writableDatabase
        db.delete(DatabaseTiles.TABLE_NAME, null, null)
    }

    fun getRecipient(tileid: Int): Long {
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
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
        val tiles: HashMap<Long, Int> = linkedMapOf()
        while (res.moveToNext()) {
            tiles.put(res.getLong(res.getColumnIndex("recipient_id")), res.getInt(res.getColumnIndex("tileid")))
        }
        return tiles

    }

    fun copyData(ctx: Context, fromTileId: Int, toTileId: Int):Boolean {
        @SuppressLint("ApplySharedPref")
        fun saveString(key: String, value: String) {
            /* Saves a String to Shared Preferences */
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.commit()
        }
        fun reset(){
            saveString("fromTileReceipient", "")
            saveString("fromTilePrefered", "")
            saveString("isCopyInProgress", "NO") /*Here we know we have possibly done something fatal to the databaes*/
            saveString("FROMTILE", "")
            saveString("FROMTILE", "")
        }
        val db = this.writableDatabase
        var fromTileReceipient = ""
        var fromTilePrefered = ""
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
        while (res.moveToNext()) {
            if (fromTileId.toString() == res.getString(res.getColumnIndex("tileid"))) {
                fromTileReceipient = res.getString(res.getColumnIndex("recipient_id"))
                fromTilePrefered = res.getString(res.getColumnIndex("preferednum"))
                saveString("fromTileReceipient", fromTileReceipient)
                saveString("fromTilePrefered", fromTilePrefered)
                saveString("isCopyInProgress", "YES") /*Here we know we have possibly done something fatal to the databaes*/
                saveString("FROMTILE", fromTileId.toString())
                saveString("FROMTILE", toTileId.toString())
                break
            }
        }
        if (getRecipient(toTileId) == -1L) {
            /*In this case, the tile we are moving the data to is free, hence no need to seek users permission*/
            val cv = ContentValues()
            cv.put("recipient_id", fromTileReceipient)
            cv.put("preferednum", fromTilePrefered)
            db.update(TABLE_NAME, cv, "tileid=$toTileId", null)
            reset()
        } else {
            /*Here we will overwrite the tile, best asking for users permission*/
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle("Overwrite tile $toTileId?")

            // Set up the buttons
            builder.setPositiveButton("Yes") {
                _, _ ->
                val cv = ContentValues()
                cv.put("recipient_id", fromTileReceipient)
                cv.put("preferednum", fromTilePrefered)
                db.update(TABLE_NAME, cv, "tileid=$toTileId", null)
                reset()
            }
            builder.setNegativeButton("Cancel") {
                dialog, _ -> dialog.cancel()
                reset()
            /* the use wants to cancel the transfer */
            }

            builder.show()
        }
     return true
    }

    fun getTile (recipient_id: Long): Int {
        /*Given a receipient_id it will find the relevent tile ID*/
        val db = this.writableDatabase
        val res = db.rawQuery("select * from " + DatabaseTiles.TABLE_NAME, null)
        while(res.moveToNext()){
            if(recipient_id.toString() == res.getString(res.getColumnIndex("recipient_id"))){
                return res.getInt(res.getColumnIndex("tileid"))
            }
        }
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