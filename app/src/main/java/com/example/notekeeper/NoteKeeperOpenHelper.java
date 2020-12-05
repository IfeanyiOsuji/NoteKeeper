package com.example.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="NoteKeeper1.db";
    public static final int DATABASE_VERSION = 4;
    public NoteKeeperOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
            db.execSQL(NoteKeeperDatabaseContract.UpdateNoteInfoEntry.SQL_CREATE_TABLE);

            db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteKeeperDatabaseContract.UpdateNoteInfoEntry.SQL_CREATE_INDEX1);


            DatabaseDataWorker worker= new DatabaseDataWorker(db);
            worker.insertCourses();
            worker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



           // db.execSQL(NoteKeeperDatabaseContract.UpdateNoteInfoEntry.DROP_SQL_TABLE);
           // onCreate(db);
           // DatabaseDataWorker worker= new DatabaseDataWorker(db);

           // worker.insertSampleNotes();
        if(oldVersion <4 ){
            db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteKeeperDatabaseContract.UpdateNoteInfoEntry.SQL_CREATE_INDEX1);
        }

    }
}
