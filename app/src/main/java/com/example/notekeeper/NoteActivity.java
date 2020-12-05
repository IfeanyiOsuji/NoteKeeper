package com.example.notekeeper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.notekeeper.NoteKeeperDatabaseContract.UpdateNoteInfoEntry;

import java.util.List;

import static com.example.notekeeper.NoteKeeperDatabaseContract.*;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTE = 0;
    public static final int LOADER_COURSES = 1;
    private final String TAG = getClass().getSimpleName();
        public static final String NOTE_ID = "com.example.notekeeper.NOTE_POSITION";
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");
    //private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnercourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNoteId;
    private boolean mIsCancelling;
    private NoteActivityViewModel mviewmodel;
    private NoteKeeperOpenHelper mDbHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private SimpleCursorAdapter mAdaptercourses;
    private boolean mCourseQueryFinished;
    private boolean mNoteQueryFinished;

    //private int POSITION_NOT_SET;

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbHelper = new NoteKeeperOpenHelper(this);

        /*ViewModelProvider viewmodelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
            mviewmodel = viewmodelProvider.get(NoteActivityViewModel.class);
            if(mviewmodel.mIsNewlyCreated && savedInstanceState != null )
                mviewmodel.restoreState(savedInstanceState);
        mviewmodel.mIsNewlyCreated = false;*/

// Populating our spinner with list of Courses
        mSpinnercourses = findViewById(R.id.spinner_courses);
       // List<CourseInfo> courses = DataManager.getInstance().getCourses();

        //This adapter will be used in associating the list with the spinner
        mAdaptercourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
new int[]{android.R.id.text1}, 0);
                mAdaptercourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnercourses.setAdapter(mAdaptercourses);
                
               // loadCourseData();
        getSupportLoaderManager().initLoader(LOADER_COURSES, null, this);

                readDisplayStateValue();
                if(savedInstanceState == null){
                    saveOriginalNoteValues();
                }else
                {
                    restoreOriginalNoteValue(savedInstanceState);
                }

        mTextNoteTitle = (EditText) findViewById(R.id.text_note_title1);
        mTextNoteText = (EditText) findViewById(R.id.text_note_text);
        if(!mIsNewNote)
        //displayNotes();
          //  loadNoteData();

        getSupportLoaderManager().initLoader(LOADER_NOTE, null, this);

        Log.d(TAG, "onCreate");


    }

    private void loadCourseData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String [] courseColumns = {
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };
       Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns, null, null, null,
                null, CourseInfoEntry.COLUMN_COURSE_TITLE);
        mAdaptercourses.changeCursor(cursor);
    }

    private void restoreOriginalNoteValue(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }


    private void loadNoteData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String courseId = "android_intents";
        String titleStart = "dynamic";

        String selection = UpdateNoteInfoEntry._ID +" = ?";
        String [] selectionArgs = {Integer.toString(mNoteId)};

        String []noteColumns = {
                UpdateNoteInfoEntry.COLUMN_COURSE_ID,
                UpdateNoteInfoEntry.COLUMN_NOTE_TITLE,
                UpdateNoteInfoEntry.COLUMN_NOTE_TEXT

        };
        mNoteCursor = db.query(UpdateNoteInfoEntry.TABLE_NAME1, noteColumns, selection, selectionArgs, null,
                null, null);
        mCourseIdPos = mNoteCursor.getColumnIndex(UpdateNoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(UpdateNoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(UpdateNoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteCursor.moveToNext();
        displayNotes();


    }

    // Saves the original values of the Note after leaving the NoteListActivity intent.
    private void saveOriginalNoteValues() {
            if(mIsNewNote)
                return;
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
       mOriginalNoteText = mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            Log.i(TAG, "Cancelling note at position "+ mNoteId);
            if(mIsNewNote) {
               // DataManager.getInstance().removeNote(mNoteId);
                deleteNoteFromDatabase();
            }
            else{
                storePreviousNoteValues();
            }
        }
            else
        saveNote();
            Log.d(TAG, "onPause");
    }

    private void deleteNoteFromDatabase() {
       final String selection = UpdateNoteInfoEntry._ID +" = ?";
       final String []selectionArgs = {Integer.toString(mNoteId)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.delete(UpdateNoteInfoEntry.TABLE_NAME1, selection, selectionArgs);
                return null;
            }
        };
        task.execute();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*if(outState !=null){
            mviewmodel.saveState(outState);
        }*/
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    private void storePreviousNoteValues() {
        /*CourseInfo courses = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(courses);
        mNote.setTitle(mviewmodel.mOriginalNoteTitle);
        mNote.setText(mviewmodel.mOriginalNoteText);*/
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    private void saveNote() {
       // mNote.setCourse((CourseInfo) mSpinnercourses.getSelectedItem());
        String courseId = selectCourseId();
        String noteTitle = mTextNoteTitle.getText().toString();
        String noteText = mTextNoteText.getText().toString();
        saveNoteToDatabase(courseId, noteTitle, noteText);
    }

    private String selectCourseId() {
        int selectedPosition = mSpinnercourses.getSelectedItemPosition();
        Cursor cursor = mAdaptercourses.getCursor();
        cursor.moveToPosition(selectedPosition);
        int cursorIdpos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        String cursorId = cursor.getString(cursorIdpos);
        return cursorId;



    }

    private void saveNoteToDatabase(String courseId, String noteTitle, String noteText){
        String selection = UpdateNoteInfoEntry._ID+ " = ?";
        String []selectionArgs = {Integer.toString(mNoteId)};

        ContentValues values = new ContentValues();
        values.put(UpdateNoteInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(UpdateNoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle);
        values.put(UpdateNoteInfoEntry.COLUMN_NOTE_TEXT, noteText);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.update(UpdateNoteInfoEntry.TABLE_NAME1, values, selection, selectionArgs);
    }


    /*private void displayNotes(Spinner spinnercourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo>courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnercourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }*/
    private void displayNotes() {
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);
        /*List<CourseInfo>courses = DataManager.getInstance().getCourses();
        CourseInfo course= DataManager.getInstance().getCourse(courseId);*/
        int courseIndex = getIndexOfCourseId(courseId);
        mSpinnercourses.setSelection(courseIndex);
        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = mAdaptercourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while(more){
            String cursorCourseId = cursor.getString(courseIdPos);
            if(courseId.equals(cursorCourseId))
                break;

                courseRowIndex++;
                more = cursor.moveToNext();

        }
        return courseRowIndex;

    }

    private void readDisplayStateValue() {
        Intent intent = getIntent();
        //mNote = intent.getParcelableExtra(NOTE_POSITION);
       // POSITION_NOT_SET = -1;
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
         mIsNewNote = mNoteId == ID_NOT_SET;
         if(mIsNewNote)
             createNewNote();

            Log.i(TAG, "mNotePosition "+ mNoteId);
           //  mNote = DataManager.getInstance().getNotes().get(mNoteId);

    }

    private void createNewNote() {
        //DataManager dm = DataManager.getInstance();
        //mNoteId = dm.createNewNote();
       // mNote = dm.getNotes().get(mNotePosition);
        ContentValues values = new ContentValues();
        values.put(UpdateNoteInfoEntry.COLUMN_COURSE_ID, "");
        values.put(UpdateNoteInfoEntry.COLUMN_NOTE_TITLE, "");
        values.put(UpdateNoteInfoEntry.COLUMN_NOTE_TEXT, "");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

       mNoteId =  (int)db.insert(UpdateNoteInfoEntry.TABLE_NAME1, null, values);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }else
            if(id==R.id.action_cancel){
                mIsCancelling = true;
                finish();
            }
            else if(id==R.id.action_next){
                moveNext();
            }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size()-1;
        item.setEnabled(mNoteId < lastNoteIndex);
        return super.onPrepareOptionsMenu( menu);
    }

    private void moveNext() {

        saveNote();
       ++mNoteId;
       mNote = DataManager.getInstance().getNotes().get(mNoteId);
       saveOriginalNoteValues();
       displayNotes();
       invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo courses = (CourseInfo) mSpinnercourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the PluralSight courses\""+ courses.getTitle()
                + "\"\n" +mTextNoteText.getText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(intent.EXTRA_SUBJECT, subject);
        intent.putExtra(intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTE)
            loader =createLoaderNote();
        else
            if(id ==LOADER_COURSES)
                loader = createLoaderCourses();
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        mCourseQueryFinished = false;
        return  new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String courseId = "android_intents";
                String titleStart = "dynamic";

                String [] courseColumns = {
                        CourseInfoEntry.COLUMN_COURSE_TITLE,
                        CourseInfoEntry.COLUMN_COURSE_ID,
                        CourseInfoEntry._ID

                };
                return db.query(CourseInfoEntry.TABLE_NAME, courseColumns,null,null, null,
                        null, CourseInfoEntry.COLUMN_COURSE_TITLE);
            }
        };
    }

    private CursorLoader createLoaderNote() {
        mNoteQueryFinished = false;
        return  new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String courseId = "android_intents";
                String titleStart = "dynamic";

                String selection = UpdateNoteInfoEntry._ID +" = ?";
                String [] selectionArgs = {Integer.toString(mNoteId)};

                String []noteColumns = {
                        UpdateNoteInfoEntry.COLUMN_COURSE_ID,
                        UpdateNoteInfoEntry.COLUMN_NOTE_TITLE,
                        UpdateNoteInfoEntry.COLUMN_NOTE_TEXT

                };
                return db.query(UpdateNoteInfoEntry.TABLE_NAME1, noteColumns, selection, selectionArgs, null,
                        null, null);
            }
        };
    }

    @Override

    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTE)
        loadFinishedNote(data);
        else
            if(loader.getId()==LOADER_COURSES)
                mAdaptercourses.changeCursor(data);
            mCourseQueryFinished = true;
            displayNotesWhenQueryIsFinished();
    }


    private void loadFinishedNote(Cursor data) {
        mNoteCursor = data;
        mCourseIdPos = mNoteCursor.getColumnIndex(UpdateNoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(UpdateNoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(UpdateNoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteCursor.moveToFirst();
        mNoteQueryFinished = true;
        displayNotesWhenQueryIsFinished();

    }

    private void displayNotesWhenQueryIsFinished() {
        if(mNoteQueryFinished && mCourseQueryFinished)
            displayNotes();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId()==LOADER_NOTE){
            if(mNoteCursor != null)
                mNoteCursor.close();
        }else
            if(loader.getId() ==LOADER_COURSES)
                mAdaptercourses.changeCursor(null);

    }




}