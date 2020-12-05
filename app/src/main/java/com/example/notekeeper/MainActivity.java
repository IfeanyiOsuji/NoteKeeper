package com.example.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.example.notekeeper.NoteActivity.LOADER_NOTE;
import static com.example.notekeeper.NoteKeeperDatabaseContract.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

   // private AppBarConfiguration mAppBarConfiguration;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNoteLayoutmanager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCourseLayoutManager;
    private NoteKeeperOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteKeeperOpenHelper(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
              drawer.setDrawerListener(toggle);
                toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
       // mAppBarConfiguration = new AppBarConfiguration.Builder(
           //     R.id.nav_notes, R.id.nav_courses)
           //     .setDrawerLayout(drawer)
           //     .build();
       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
       // NavigationUI.setupWithNavController(navigationView, navController);

        initialDisplayContent();
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        //  mAdapterniotes.notifyDataSetChanged();
       // mNoteRecyclerAdapter.notifyDataSetChanged();
        //loadNotes();
        getSupportLoaderManager().restartLoader(LOADER_NOTE, null, this);
        updateNavHeader();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String noteOrderBy = UpdateNoteInfoEntry.COLUMN_COURSE_ID +","
                + UpdateNoteInfoEntry.COLUMN_NOTE_TITLE;
        String[] noteColumns =
                {UpdateNoteInfoEntry.COLUMN_NOTE_TITLE,
                        UpdateNoteInfoEntry.COLUMN_COURSE_ID,
                        UpdateNoteInfoEntry._ID};

        final Cursor noteCursor1 = db.query(UpdateNoteInfoEntry.TABLE_NAME1, noteColumns, null, null,
                null, null, noteOrderBy);
        mNoteRecyclerAdapter.changeCursor(noteCursor1);

       // noteCursor1.close();
    }

    private void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        TextView textEmailAddress = (TextView)headerView.findViewById(R.id.text_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address","");

        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);


    }

    //Displaying the list of courses on the List view using adapter
    private void initialDisplayContent() {
        DataManager.loadFromDataBase(mDbOpenHelper);
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_items);
        mNoteLayoutmanager = new LinearLayoutManager(this);
        mCourseLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));


       // List<NoteInfo> note = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

        List<CourseInfo>courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);
        displayNotes();

    }

   

    private void displayNotes() {
        mRecyclerItems.setLayoutManager(mNoteLayoutmanager);
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);

      // SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        selectNavigationMenuItem(R.id.nav_notes);
    }

    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        mRecyclerItems.setLayoutManager(mCourseLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_courses);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   // @Override
   // public boolean onSupportNavigateUp() {
     //   NavController navController = Navigation.findNavController(this, R.id.nav_view);
      //  return NavigationUI.navigateUp(navController, mAppBarConfiguration)
      //          || super.onSupportNavigateUp();
  //  }
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_notes){
            displayNotes();
        }
        else
            if(id==R.id.nav_courses){
                displayCourses();
            }
            else
            if(id==R.id.nav_share){
                handleShare();
            }
            else
            if(id==R.id.nav_send){
                handleSelection(R.string.nav_send_message);
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Sahre to - "+
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social",""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();

    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTE) {
            loader = new CursorLoader(this) {
                @Override
                public Cursor loadInBackground() {
                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                    final String[] noteColumns = {
                            UpdateNoteInfoEntry.getQName(UpdateNoteInfoEntry._ID),
                            UpdateNoteInfoEntry.COLUMN_NOTE_TITLE,
                    CourseInfoEntry.COLUMN_COURSE_TITLE};

                    final String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE +
                            "," + UpdateNoteInfoEntry.COLUMN_NOTE_TITLE;

                    //note_info join course_info on note_info_course_id = course_info_course_id
                    String tableWithJoin =
           UpdateNoteInfoEntry.TABLE_NAME1 + " JOIN "+CourseInfoEntry.TABLE_NAME+" ON "+
           CourseInfoEntry.getQName(UpdateNoteInfoEntry.COLUMN_COURSE_ID)+" = "+
           UpdateNoteInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

                    return db.query(tableWithJoin, noteColumns,
                            null, null, null, null, noteOrderBy);
                }
            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if(loader.getId() == LOADER_NOTE)  {
            mNoteRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if(loader.getId() == LOADER_NOTE)  {
            mNoteRecyclerAdapter.changeCursor(null);
        }

    }
}