package com.example.noteme_lab;

import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class NewNoteActivity extends AppCompatActivity {

    //initialize all variabels
    private NoteManager noteSelected;
    private LinearLayout note_layout;
    private EditText et_title, et_desc, et_colour;
    private ImageView note_image;
    private File img_photo_file = null;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        //on create, set all the values for edittexts, buttons, etc
        et_title = findViewById(R.id.et_Title);
        et_desc = findViewById(R.id.et_description);
        et_colour = findViewById(R.id.et_colour);

        Button btn_orange = findViewById(R.id.btn_Orange);
        Button btn_blue = findViewById(R.id.btn_Blue);
        Button btn_green = findViewById(R.id.btn_Green);
        Button btn_Image = findViewById(R.id.btn_Image);
        Button btn_Photo = findViewById(R.id.btn_Camera);

        note_layout = findViewById(R.id.layout_Note);
        Context context = getApplicationContext();

        note_image = findViewById(R.id.img_note);

        //set the back ground colour for the note
        btn_orange.setOnClickListener(v -> {
            et_colour.setText("#ECAB3A");
            note_layout.setBackgroundResource(R.color.note_orange);
        });

        btn_blue.setOnClickListener(v -> {
            et_colour.setText("#37CEEC");
            note_layout.setBackgroundResource(R.color.note_blue);
        });

        btn_green.setOnClickListener(v -> {
            et_colour.setText("#78D0A1");
            note_layout.setBackgroundResource(R.color.note_green);
        });


        //set onclick listener for when user chooses to add an image
        btn_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        //set onclick listener for when user chooses the camera option
        btn_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromCamera();
            }
        });

        //call method to edit the note
        editNote();

    }

    //method called when "choose an image" is chosen, this method will set the type to image and call the launcher
    public void imageChooser(){
        Intent image = new Intent();
        image.setType("image/*");
        image.setAction(Intent.ACTION_GET_CONTENT);
        
        launchImageChooser.launch(image);
    }

    //this method calls the activity result launcher to launch the intent
    //upon doing so it will handle the launching of the image file for user to choose an image
    ActivityResultLauncher<Intent> launchImageChooser = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent dataIntent = result.getData();
                    if(dataIntent != null && dataIntent.getData() != null){
                        Uri selectedImgUri = dataIntent.getData();
                        Bitmap selectedImgBitmap = null;
                        try{
                            selectedImgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri);
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        //sets the imageview to the selected image
                        note_image.setImageBitmap(selectedImgBitmap);
                    }
                }
            });


    //this method is invoked on clicking an item from the list view
    private void editNote(){
        Intent prevIntent = getIntent();
        int passedNoteId = prevIntent.getIntExtra(NoteManager.NOTE_EDIT_EXTRA, -1);
        noteSelected = com.example.noteme_lab.NoteManager.getNoteForID(passedNoteId);

        if(noteSelected != null){
            et_title.setText(noteSelected.getTitle());
            et_desc.setText(noteSelected.getDesc());
            et_colour.setText(noteSelected.getColour());
            note_layout.setBackgroundColor(Color.parseColor(String.valueOf(et_colour.getText())));
            note_image.setImageBitmap(noteSelected.getUser_image());
        }
    }

    //this method is invoked on clicking the save note button
    public void saveNotePressed(View view){
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = String.valueOf(et_title.getText());
        String desc = String.valueOf(et_desc.getText());
        String col = String.valueOf(et_colour.getText());
        Bitmap img;

        if(note_image.getDrawable() == null){
            img = null;
        }
        else{
            Drawable draw = note_image.getDrawable();
            img = ((BitmapDrawable) draw).getBitmap();
        }

        //if the title is empty make a toast pop up for the user, invoking them to add a title
        if(title.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a title!", Toast.LENGTH_SHORT).show();
        }
        //otherwise add the new note to the database and the note manager
        else{
            if(noteSelected == null){
                int id = NoteManager.notesArrayList.size();
                NoteManager newNote = new NoteManager(id, title, desc, col, img);
                NoteManager.notesArrayList.add(newNote);
                sqLiteManager.addNote(newNote);
            }
            //update the note if already made
            else{
                noteSelected.setTitle(title);
                noteSelected.setDesc(desc);
                noteSelected.setColour(col);
                sqLiteManager.updateNoteInDatabase(noteSelected);
            }
            //let user know the note saved successfully
            Toast.makeText(getApplicationContext(), "Note saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    //this method is invoked on clicking the back button
    public void backPressed(View view){
        getOnBackPressedDispatcher().onBackPressed();
    }

    //method invoked when delete button clicked
    public void deleteNotePressed(View view){
        noteSelected.setDeleted(new Date());
        SQLiteManager sq = SQLiteManager.instanceOfDatabase(this);
        sq.updateNoteInDatabase(noteSelected);
        Toast.makeText(getApplicationContext(), "Note has been deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    //method is called when user chooses to use camera to add a picture to the note
    private void chooseFromCamera(){
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            img_photo_file = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.noteme_lab.fileprovider", img_photo_file);
            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePicIntent, NewNoteActivity.REQUEST_IMAGE_CAPTURE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //creates the image file for the picture taken by camera
    private File createImageFile() throws IOException {
        long timeStamp = Calendar.getInstance().getTimeInMillis();
        String imgFileName = "JPEG_" + timeStamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imgFileName,".jpg", storageDirectory);
    }
    


}