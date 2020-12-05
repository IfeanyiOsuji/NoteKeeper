package com.example.notekeeper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

//import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.Espresso.pressBack;
//import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    public  static DataManager sDataManager;

    @BeforeClass
    public static void classSetUp(){
       sDataManager = DataManager.getInstance();
    }
    @Rule // This is to make the jUnit to be aware of the test and also to clean up at the end of each test
        public ActivityTestRule<NoteListActivity> mNoteListActivityRule =
                new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote(){
        final CourseInfo course = sDataManager.getCourse("java_lang");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body of our test note";


//Expresso classes heip us to specify a matcher based on our target data because the data may not be on the visible
        // window of our listView.

        // we use the general purpose hemcrest matcher because we want to tie the matcher to our data not the view

        //The onData method returns back a reference to data interaction. The dadainteraction type provides
        // a number of methods that returns a match or narrowing a match that corresponds to our selection.
        //Although the datainteraction can do a number of things, we tend to use its perform method
        // which alows us to perform an action against the top level view of that entry within the adapter view
        // in other cases when the user interaction does not involve a view, we tend to use the expresso classe's
        // pushBack button. It does not return a reference to a view. it takes you back to the activity
        // we left before


        //ViewInteraction fabNewNote = onView(withId(R.id.fab));
        //fabNewNote.perform(click());
        //Getting to the view and performing action on it
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.spinner_courses)).perform(click());//the app will throw an exception if
        //this is missing because it has to access the spinner first before extracting the data
        // and also we are dealing with listView. If it is tesxView we are dealing with, the onData
        // method alone will work fine

        //onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(course))).perform(click());// rada than going to the spinner
        // to get the course, this method goes to the courseInfo class to get the required matched couese
        //onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(containsString(course.getTitle()))));//perfoms match check

        onView(withId(R.id.text_note_title1)).perform(typeText(noteTitle))
       .check(matches(withText(containsString(noteTitle))));
        onView(withId(R.id.text_note_text)).perform(typeText(noteText),
                closeSoftKeyboard());
        onView(withId(R.id.text_note_text)).check(matches(withText(containsString(noteText))));

        pressBack();
       //performing the unit test check to confirm the authenticity of the data
        int noteIndex = sDataManager.getNotes().size()-1;
        NoteInfo note = sDataManager.getNotes().get(noteIndex);
        assertEquals(course, note.getCourse());
        assertEquals(noteTitle, note.getTitle());
        assertEquals(noteText, note.getText());
    }
}