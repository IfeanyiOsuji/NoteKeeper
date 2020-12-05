package com.example.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.ORIGINAL_NOTE_TEXT";


    public String mOriginalNoteCourseId;
    public  String mOriginalNoteTitle;
    public String mOriginalNoteText;
    public boolean mIsNewlyCreated = true;

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }
    public void restoreState(Bundle inState){
        inState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        inState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        inState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }
}
