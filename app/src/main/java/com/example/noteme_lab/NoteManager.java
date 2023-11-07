package com.example.noteme_lab;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class NoteManager {
    public static ArrayList<NoteManager> notesArrayList = new ArrayList<>();
    public static String NOTE_EDIT_EXTRA = "noteEdit";

    private int id;
    private String title;
    private String desc;
    private String colour;
    private Date deleted;
    private Bitmap user_image;

    public NoteManager(int id, String title, String description, String colour, Date deleted, Bitmap user_image) {
        this.id = id;
        this.title = title;
        this.desc = description;
        this.colour = colour;
        this.deleted = deleted;
        this.user_image = user_image;
    }

    public NoteManager(int id, String title, String description, String colour, Bitmap user_image) {
        this.id = id;
        this.title = title;
        this.desc = description;
        this.colour = colour;
        this.user_image = user_image;
    }

    public NoteManager(int id, String title, String description, String colour, Date deleted) {
        this.id = id;
        this.title = title;
        this.desc = description;
        this.colour = colour;
        this.deleted = deleted;
    }

    public NoteManager(int id, String title, String description, String colour) {
        this.id = id;
        this.title = title;
        this.desc = description;
        this.colour = colour;
        deleted = null;
    }

    //get the id for note
    public static NoteManager getNoteForID(int passedNoteID) {
        for (NoteManager note : notesArrayList) {
            if (note.getId() == passedNoteID)
                return note;
        }

        return null;
    }

    //get the notes that are not deleted
    public static ArrayList<NoteManager> nonDeletedNotes() {
        ArrayList<NoteManager> nonDeleted = new ArrayList<>();
        for (NoteManager note : notesArrayList) {
            if (note.getDeleted() == null)
                nonDeleted.add(note);
        }

        return nonDeleted;
    }

    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Bitmap getUser_image() {
        return user_image;
    }

    public void setUser_image(Bitmap user_image) {
        this.user_image = user_image;
    }

}
