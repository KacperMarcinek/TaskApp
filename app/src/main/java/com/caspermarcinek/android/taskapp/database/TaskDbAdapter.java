package com.caspermarcinek.android.taskapp.database;

/**
 * Created by witch on 10/13/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class TaskDbAdapter {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_TODO_TABLE = "todo";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_DESCRIPTION = "description";
    public static final String DESCRIPTION_OPTIONS = "TEXT NOT NULL";
    public static final int DESCRIPTION_COLUMN = 1;
    public static final String KEY_COMPLETED = "completed";
    public static final String COMPLETED_OPTIONS = "INTEGER DEFAULT 0";
    public static final int COMPLETED_COLUMN = 2;

    private static final String DB_CREATE_TODO_TABLE =
            "CREATE TABLE " + DB_TODO_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_DESCRIPTION + " " + DESCRIPTION_OPTIONS + ", " +
                    KEY_COMPLETED + " " + COMPLETED_OPTIONS +
                    ");";
    private static final String DROP_TODO_TABLE =
            "DROP TABLE IF EXISTS " + DB_TODO_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TODO_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TODO_TABLE);

            onCreate(db);
        }
    }

    public TaskDbAdapter(Context context) {
        this.context = context;
    }

    public TaskDbAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTask(String description) {
        ContentValues newTodoValues = new ContentValues();
        newTodoValues.put(KEY_DESCRIPTION, description);
        return db.insert(DB_TODO_TABLE, null, newTodoValues);
    }

    public boolean updateTask(long id, String description, boolean completed) {
        String where = KEY_ID + "=" + id;
        int completedTask = completed ? 1 : 0;
        ContentValues updateTodoValues = new ContentValues();
        updateTodoValues.put(KEY_DESCRIPTION, description);
        updateTodoValues.put(KEY_COMPLETED, completedTask);
        return db.update(DB_TODO_TABLE, updateTodoValues, where, null) > 0;
    }

    public boolean deleteTask(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_TODO_TABLE, where, null) > 0;
    }

    public Cursor getAllTasks() {
        String[] columns = {KEY_ID, KEY_DESCRIPTION, KEY_COMPLETED};
        return db.query(DB_TODO_TABLE, columns, null, null, null, null, null);
    }
}

