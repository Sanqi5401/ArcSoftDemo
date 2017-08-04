package com.arcsoft.library.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.arcsoft.library.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/8/2.
 */

public class FaceDataBaseHelp   extends SQLiteOpenHelper{

    private static FaceDataBaseHelp instance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "face";
    private String createSQL;
    private Context context;
    public static final String TABLE_FACE = "Faces";

    // Deprecate global instance?
    public static synchronized FaceDataBaseHelp getHelper(Context context) {
        if (instance == null) {
            instance = new FaceDataBaseHelp(context);
        }
        return instance;
    }

    public FaceDataBaseHelp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        Resources res = context.getResources();
        InputStream inputStream = res.openRawResource(R.raw.face);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder createSQLBuilder = new StringBuilder();
        try {
            while (( line = reader.readLine()) != null) {
                createSQLBuilder.append(line);
                createSQLBuilder.append('\n');
            }
        } catch (IOException e) {
            Log.e("SettingsOpenHelper", "error creating database");
        }

        createSQL = createSQLBuilder.toString();
    }

    public FaceDataBaseHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public FaceDataBaseHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] statements = createSQL.split(";");
        for (String statement : statements ) {
            if (statement.trim().length() > 0)
                db.execSQL(statement.trim() + ";");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
