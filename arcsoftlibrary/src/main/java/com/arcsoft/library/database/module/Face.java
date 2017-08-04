package com.arcsoft.library.database.module;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.arcsoft.library.database.FaceDataBaseHelp;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class Face {

    private SQLiteDatabase database;
    private FaceDataBaseHelp dbHelper;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String FEATURE = "feature";
    public static final String PATH = "path";

    private Context context;

    private Integer id;
    private String name, path, mFeature;


    public Face(Context context) {
        this.context = context;

        dbHelper = new FaceDataBaseHelp(context);
        open();

        Cursor cursor = database.query(FaceDataBaseHelp.TABLE_FACE, null, null, null, null, null, null);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            List<String> columnNamesList = Arrays.asList(columnNames);
            setId(cursor.getInt(columnNamesList.indexOf(ID)), false);
            setName(cursor.getString(columnNamesList.indexOf(NAME)), false);
            setFeature(cursor.getString(columnNamesList.indexOf(FEATURE)), false);
            setPath(cursor.getString(columnNamesList.indexOf(PATH)), false);
        }
        cursor.close();
        close();
    }

    public Face(Context context, int offset) {
        //Log.i("Camera", "constructor device");
        this.context = context.getApplicationContext();

        dbHelper = new FaceDataBaseHelp(this.context);
        open();

        Cursor cursor = database.query(FaceDataBaseHelp.TABLE_FACE, null, null, null, null, null, null);
        if (cursor.getCount() != 0 && cursor.moveToPosition(offset)) {
            {
                String[] columnNames = cursor.getColumnNames();
                List<String> columnNamesList = Arrays.asList(columnNames);
                setId(cursor.getInt(columnNamesList.indexOf(ID)), false);
                setName(cursor.getString(columnNamesList.indexOf(NAME)), false);
                setFeature(cursor.getString(columnNamesList.indexOf(FEATURE)), false);
                setPath(cursor.getString(columnNamesList.indexOf(PATH)), false);
            }
            cursor.close();
            close();
        }
    }


    public Face(Context context, ContentValues values) {
        this.context = context.getApplicationContext();

        dbHelper = new FaceDataBaseHelp(this.context);
        open();

        long result = -1;
        if (values != null) {
            result = database.insertOrThrow(FaceDataBaseHelp.TABLE_FACE, null, values);
            //Log.i("INSERTING", Long.toString(result));
        }

        if (result != -1) {
            Cursor cursor = database.query(FaceDataBaseHelp.TABLE_FACE, null, Face.ID + "=?", new String[]{Long.toString(result)}, null, null, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                String[] columnNames = cursor.getColumnNames();
                List<String> columnNamesList = Arrays.asList(columnNames);
                setId(cursor.getInt(columnNamesList.indexOf(ID)), false);
                setName(cursor.getString(columnNamesList.indexOf(NAME)), false);
                setFeature(cursor.getString(columnNamesList.indexOf(FEATURE)), false);
                setPath(cursor.getString(columnNamesList.indexOf(PATH)), false);
            }
            cursor.close();
        }
        close();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        setId(id, true);
    }

    public void setId(Integer id, Boolean updateDB) {
        this.id = id;

        if (updateDB) {
            ContentValues values = new ContentValues();
            values.put(Face.ID, id);
            open();
            database.update(FaceDataBaseHelp.TABLE_FACE, values, Face.ID + "=?", new String[]{Long.toString(getId())});
            close();
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        setName(name, true);
    }

    public void setName(String name, Boolean updateDB) {
        this.name = name;
        if (updateDB) {
            ContentValues values = new ContentValues();
            values.put(Face.NAME, name);
            open();
            database.update(FaceDataBaseHelp.TABLE_FACE, values, Face.ID + "=?", new String[]{Long.toString(getId())});
            close();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        setPath(path, true);
    }

    public void setPath(String path, Boolean updateDB) {
        this.path = path;

        if (updateDB) {
            ContentValues values = new ContentValues();
            values.put(Face.PATH, path);
            open();
            database.update(FaceDataBaseHelp.TABLE_FACE, values, Face.ID + "=?", new String[]{Long.toString(getId())});
            close();
        }
    }

    public byte[] getFeature() {
        return Base64.decode(mFeature.getBytes(), Base64.DEFAULT);
    }

    public void setFeature(byte[] feature) {
        setFeature(feature, true);
    }

    public void setFeature(byte[] feature, Boolean updateDB) {
        this.mFeature = Base64.encodeToString(feature, Base64.DEFAULT);

        if (updateDB) {
            ContentValues values = new ContentValues();
            values.put(Face.FEATURE, feature);
            open();
            database.update(FaceDataBaseHelp.TABLE_FACE, values, Face.ID + "=?", new String[]{Long.toString(getId())});
            close();
        }
    }

    public void setFeature(String feature, Boolean updateDB) {
        this.mFeature = feature;

        if (updateDB) {
            ContentValues values = new ContentValues();
            values.put(Face.FEATURE, feature);
            open();
            database.update(FaceDataBaseHelp.TABLE_FACE, values, Face.ID + "=?", new String[]{Long.toString(getId())});
            close();
        }
    }

    public void delete(){
        open();
        database.delete(FaceDataBaseHelp.TABLE_FACE, Face.ID + "=?", new String[]{Long.toString(getId())});
        close();
    }

}
