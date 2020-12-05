package com.example.notekeeper;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public  final class NoteKeeperDatabaseContract{
    private NoteKeeperDatabaseContract mNoteKeeperDatabaseContract;
    public static final class CourseInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "course_Info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";



        // create index course_info_index1 on course_info(course_title)

        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX "+
                INDEX1 + " ON " +TABLE_NAME+ "("+COLUMN_COURSE_TITLE+")";

        public static final String getQName(String columnName){

            return TABLE_NAME+"."+columnName;
        }

        //CREATE TABLE course_info, course_id, course_title->
        public static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME +" ("
                + _ID+" INTEGER PRIMARY KEY, "
                +COLUMN_COURSE_ID +" TEXT UNIQUE NOT NULL, "
                +COLUMN_COURSE_TITLE +" TEXT NOT NULL)";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    }

    public static final class UpdateNoteInfoEntry implements BaseColumns{
        public static final String TABLE_NAME1 = "note_info1";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";


        // create index note_info_index1 on course_info(note_title)
        public static final String INDEX1 = TABLE_NAME1 + "_index1";
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX "+
                INDEX1 + " ON " + TABLE_NAME1+"("+COLUMN_NOTE_TITLE+")";

        public static final String getQName(String columnName){
            return TABLE_NAME1+"."+columnName;
        }

        //Create table note_info, note_title, note_text, course_id;

            public static final String SQL_CREATE_TABLE ="CREATE TABLE " + TABLE_NAME1 +
                    "(" + _ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_NOTE_TITLE + " TEXT NOT NULL, "
                    + COLUMN_NOTE_TEXT + " TEXT, " +
                    COLUMN_COURSE_ID + " TEXT NOT NULL)";

        public static final String DROP_SQL_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME1;



    }
}
