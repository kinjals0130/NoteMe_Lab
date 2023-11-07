package com.example.noteme_lab;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    //Initialize variables to be used
    private ListView lv_notesList;
    private MyNoteAdapter notesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set variables
        SearchView sv_notes = findViewById(R.id.sv_searchNotes);
        lv_notesList = findViewById(R.id.lv_notesList);

        //this method calls the stored note info from the database
        loadDBMemory();

        //this method sets the adapter to the listview to be shown to the user
        setNotesAdapter();

        //this method sets the on item click listener for the list view
        setListenerforListView();

        //this sets the query listener for the search bar
        sv_notes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                notesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void loadDBMemory(){
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        //call all the notes to be set into the listview
        sqLiteManager.getAllNotesforArray();
    }

    //set the listview with the notes from the adapter
    private void setNotesAdapter(){
        notesAdapter = new MyNoteAdapter(getApplicationContext(), NoteManager.nonDeletedNotes());
        lv_notesList.setAdapter(notesAdapter);
    }

    //method is invoked when the new note button is pressed
    public void addNewNote(View view){
        Intent newNote = new Intent(this, NewNoteActivity.class);
        startActivity(newNote);
    }

    //this method will allow users to edit the note on clicking the item in the listview
    private void setListenerforListView(){
        lv_notesList.setOnItemClickListener((parent, view, position, id) -> {
            NoteManager noteSelected = (NoteManager) lv_notesList.getItemAtPosition(position);
            Intent editIntent = new Intent(getApplicationContext(), NewNoteActivity.class);
            editIntent.putExtra(NoteManager.NOTE_EDIT_EXTRA, noteSelected.getId());
            startActivity(editIntent);
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        setNotesAdapter();
    }
}