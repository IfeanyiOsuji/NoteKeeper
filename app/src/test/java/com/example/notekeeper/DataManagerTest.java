package com.example.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {
    static DataManager sDataManager;

    static{
   // public static void classSetUp{

        sDataManager = DataManager.getInstance();
    }

    @Before
    public void setUp() {
        sDataManager = DataManager.getInstance();
        sDataManager.getNotes().clear();
        sDataManager.initializeExampleNotes();
    }

    @Test
    public void createNewNote() {
        sDataManager = DataManager.getInstance();
        final CourseInfo course = sDataManager.getCourse("android_sync");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body text of my test note";


        // craeting a new note at the particular index
        int noteIndex = sDataManager.createNewNote();
        NoteInfo newNote = sDataManager.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);


        // want to make sure that compareNote contains the same information in newNote
        // Unit test does not focus on implementation details but on functionality
        NoteInfo compareNote = sDataManager.getNotes().get(noteIndex);

        //to make sure we get the exact values we put in the newNote
        assertEquals(compareNote.getCourse(), course);
        assertEquals(compareNote.getTitle(), noteTitle);
        assertEquals(compareNote.getText(), noteText);
    }

    @Test
    public void findSimilarNotes() {
        sDataManager = DataManager.getInstance();
        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText1 = "This is the body text of my test note";
        final String noteText2 = "This is the body text of my  second test note";

        // craeting a new note at the particular index
        int noteIndex1 = sDataManager.createNewNote();
        NoteInfo newNote1 = sDataManager.getNotes().get(noteIndex1);
        newNote1.setCourse(course);
        newNote1.setTitle(noteTitle);
        newNote1.setText(noteText1);

        int noteIndex2 = sDataManager.createNewNote();
        NoteInfo newNote2 = sDataManager.getNotes().get(noteIndex2);
        newNote2.setCourse(course);
        newNote2.setTitle(noteTitle);
        newNote2.setText(noteText2);

        int foundNote1 = sDataManager.findNote(newNote1);
        assertEquals(noteIndex1, foundNote1);

        int foundNote2 = sDataManager.findNote(newNote2);
        assertEquals(noteIndex2, foundNote2);
    }
    @Test
    public void createNewNoteOneStepCreation(){
        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body text of my test note";

        int noteIndex = sDataManager.createNewNote(course, noteTitle, noteText);
        NoteInfo computNote = sDataManager.getNotes().get(noteIndex);
        assertEquals(course, computNote.getCourse());
        assertEquals(noteTitle, computNote.getTitle());
        assertEquals(noteText, computNote.getText());

    }

}