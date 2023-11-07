package com.example.noteme_lab;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteManager extends SQLiteOpenHelper {

    //set all the database variables
    private static SQLiteManager sqLiteManager;
    private static final String DB_NAME = "NotesApp.DB";
    private static final String TABLE_NAME = "Notes";
    private static final String COUNTER = "Counter";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESC = "description";
    private static final String COLUMN_COLOUR = "colour";
    private static final String COLUMN_DELETED = "deleted";
    private static final String COLUMN_IMAGE = "image";
    private static final int DB_VERSION = 1;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public SQLiteManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context) {
        if (sqLiteManager == null)
            sqLiteManager = new SQLiteManager(context);

        return sqLiteManager;
    }


    //create the database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sqlString;
        sqlString = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(COLUMN_ID)
                .append(" INT, ")
                .append(COLUMN_TITLE)
                .append(" TEXT, ")
                .append(COLUMN_DESC)
                .append(" TEXT, ")
                .append(COLUMN_COLOUR)
                .append(" TEXT, ")
                .append(COLUMN_DELETED)
                .append(" TEXT, ")
                .append(COLUMN_IMAGE)
                .append(" BLOB)");

        db.execSQL(sqlString.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //add a note to the database using contentvalues
    public void addNote(NoteManager noteManager){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, noteManager.getId());
        cv.put(COLUMN_TITLE, noteManager.getTitle());
        cv.put(COLUMN_DESC, noteManager.getDesc());
        cv.put(COLUMN_COLOUR, noteManager.getColour());

        cv.put(COLUMN_DELETED, getStringFromDate(noteManager.getDeleted()));
        cv.put(COLUMN_IMAGE, getByteArrayFromImage(noteManager.getUser_image()));

        db.insert(TABLE_NAME, null, cv);

    }

    //this gets all the notes saved in the database and saves it to the note manager to be later
    //used for the arraylist
    public void getAllNotesforArray(){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME;

        //Cursor cursor = db.rawQuery(queryString, null);
        try(Cursor cursor = db.rawQuery(queryString, null)){
            if(cursor.getCount() != 0){
                while(cursor.moveToNext()){
                    int id = cursor.getInt(1);
                    String title = cursor.getString(2);
                    String desc = cursor.getString(3);
                    String colour = cursor.getString(4);

                    String str_deleted = cursor.getString(5);
                    Date deleted = getDateFromString(str_deleted);

                    byte[] byteArray_Image = cursor.getBlob(6);
                    Bitmap bit_image = getImageFromByteArray(byteArray_Image);

                    NoteManager note = new NoteManager(id, title, desc, colour, deleted, bit_image);
                    NoteManager.notesArrayList.add(note);
                }
            }
            cursor.close();
            db.close();
        }

    }

    //called when updating the note on user edit
    public void updateNoteInDatabase(NoteManager noteManager){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, noteManager.getId());
        cv.put(COLUMN_TITLE, noteManager.getTitle());
        cv.put(COLUMN_DESC, noteManager.getDesc());
        cv.put(COLUMN_COLOUR, noteManager.getColour());
        cv.put(COLUMN_DELETED, getStringFromDate(noteManager.getDeleted()));
        cv.put(COLUMN_IMAGE, getByteArrayFromImage(noteManager.getUser_image()));

        db.update(TABLE_NAME, cv, COLUMN_ID + " =?", new String[]{
                String.valueOf(noteManager.getId())
        });
    }


    //used to get the date as a string
    private String getStringFromDate(Date date) {
        if (date == null)
            return null;
        return dateFormat.format(date);
    }

    //reverse it back to a date
    private Date getDateFromString(String string) {
        try {
            return dateFormat.parse(string);
        } catch (NullPointerException | ParseException e) {
            return null;
        }
    }

    //recommended way to save image in SQLite database
    private byte[] getByteArrayFromImage(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            return outputStream.toByteArray();
        }
    }

    //this is used when getting the image back from the database
    private Bitmap getImageFromByteArray(byte[] imageByteArray) {
        if(imageByteArray == null) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        }
    }
}
