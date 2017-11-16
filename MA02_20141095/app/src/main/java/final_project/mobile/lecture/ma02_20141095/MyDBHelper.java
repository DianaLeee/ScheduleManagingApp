package final_project.mobile.lecture.ma02_20141095;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeeDaYeon on 2017-03-22.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public final static String DB_NAME = "my_db_ma02_20141095";
    public final static String TABLE_NAME = "my_table_ma02_20141095";


    public MyDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " ( _id integer primary key autoincrement, title text, content text, time integer," +
                                                              " sName text, sNumber text, sId integer, latitude integer, longitude integer, place_name text, place_address text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Appoint> getDataFromDB() {
        List<Appoint> items = new ArrayList<Appoint>();

        String query = "select * from " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            long time = cursor.getLong(3);
            String sName = cursor.getString(4);
            String sNumber = cursor.getString(5);
            long sId = cursor.getLong(6);
            double latitude = cursor.getDouble(7);
            double longitude = cursor.getDouble(8);
            String place_name = cursor.getString(9);
            String place_address = cursor.getString(10);
            items.add(new Appoint(_id, title, content, time, sName, sNumber,sId, latitude, longitude, place_name, place_address));
        }

        cursor.close();

        return items;

    }
}
