package com.example.notekeeper;

import androidx.annotation.ContentView;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static com.example.notekeeper.R.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class NextThroughNotesTest {
    @Rule
    public ActivityTestRule<MainActivity>mMainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void NextThroughNotes(){
        onView(withId(id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(id.nav_view)).perform(NavigationViewActions.navigateTo(id.nav_notes));

        onView(withId(id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        for(int index=0; index<notes.size(); index++) {

            NoteInfo note = notes.get(index);
            onView(withId(id.spinner_courses)).check(
                    matches(withSpinnerText(note.getCourse().getTitle())));
            onView(withId(id.text_note_title1)).check(matches(withText(note.getTitle())));
            onView(withId(id.text_note_text)).check(matches(withText(note.getText())));

            //onView(withId(id.action_next)).perform(click());
            if(index<notes.size()-1)
            onView(allOf(withId(id.action_next), isEnabled())).perform(click());
        }
        onView(withId(id.action_next)).check(matches(not(isEnabled())));
        pressBack();

    }

}