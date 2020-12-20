package com.alabama.sweethome.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.alabama.sweethome.CAPIData;

import java.util.ArrayList;
import java.util.List;

public class DBService extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "covid.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "coviddata";
    private static final int SCHEMA = 1;


    private static DBService sInstance = null;

    public static DBService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBService(context.getApplicationContext());
        }
        return sInstance;
    }

    public DBService(@Nullable Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE = "CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "reg TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "cas INTEGER NOT NULL, " +
                "dcas INTEGER NOT NULL);";
        db.execSQL(CREATE);
    }

    private ContentValues toContentValues(CAPIData data) {
        final ContentValues cv = new ContentValues(4);
        cv.put("reg", data.getRegion());
        cv.put("date", data.getDataDate());
        cv.put("cas", data.getNewCases());
        cv.put("dcas", data.getNewDeaths());
        return cv;
    }

    public void addData(List<CAPIData> data) {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(getWritableDatabase());
        data.forEach(this::addData);
    }

    public List<CAPIData> getAllData() {
        Cursor c = null;

        try {
            c = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
            if (c == null) return new ArrayList<>();

            final int size = c.getCount();
            final ArrayList<CAPIData> list = new ArrayList<>(size);
            if (c.moveToFirst()) {
                do {
                    final long id = c.getLong(c.getColumnIndex("_id"));
                    final String reg = c.getString(c.getColumnIndex("reg"));
                    final String date = c.getString(c.getColumnIndex("date"));
                    final int cas = c.getInt(c.getColumnIndex("cas"));
                    final int dcas = c.getInt(c.getColumnIndex("dcas"));

                    final CAPIData data = new CAPIData();
                    data.setRegion(reg);
                    data.setDataDate(date);
                    data.setNewCases(cas);
                    data.setNewDeaths(dcas);
                    list.add(data);
                } while (c.moveToNext());
            }
            return list;
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }
    }

    private long addData(CAPIData data) {
        return getWritableDatabase().insert(TABLE_NAME, null, toContentValues(data));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
