package wirkungsfunktional.de.emiProjectWS17.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by mk on 29.11.17.
 *
 *
 * Based on: https://www.tutorialspoint.com/android/android_sqlite_database.htm
 */

public class DataBaseContainer extends SQLiteOpenHelper {


    private static final String DATA_BASE_NAME = "Test.db";
    private static final String CONTACTS_TABLE_NAME = "initdata";
    private static final String CONTACTS_COLUMN_NAME = "name";

    public DataBaseContainer(Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table initdata " +
         "(id integer primary key, name text,comment text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS initdata");
        onCreate(sqLiteDatabase);
    }


    public boolean insertEntry(OrbitDataBundle data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", data.getName());
        contentValues.put("comment", data.getComment());
        db.insert("initdata", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from initdata where id="+id+"", null );
        return res;
    }
    
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateData(Integer id, OrbitDataBundle data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", data.getName());
        contentValues.put("comment", data.getComment());
        db.update("initdata", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteData(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("initdata",
                    "id = ? ", new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllData() {
      ArrayList<String> array_list = new ArrayList<String>();

      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from initdata", null );
      res.moveToFirst();

      while(res.isAfterLast() == false){
         array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
         res.moveToNext();
      }
      return array_list;
   }



}
