package com.nick_sorrentino.simpletodo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nick_sorrentino on 7/18/16.
 */
public class TodoItemDatabase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "Todo.db";
    public static final String TODO_TABLE_NAME = "todo_items";
    public static final String COLUMN_TASK_ID = "_id";
    public static final String COLUMN_TASK = "title";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DUE_DATE = "due_date";

    public TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the tables
        String createTable = "CREATE TABLE " + TODO_TABLE_NAME + " ( " +
                COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK + " TEXT NOT NULL," +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_DUE_DATE + " DATE);";

        db.execSQL(createTable);
    }

    // This method is called when database is upgraded like
    // modifying the table structure,
    // adding constraints to database, etc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // SQL for upgrading the tables
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_NAME);
        onCreate(db);
    }
}
