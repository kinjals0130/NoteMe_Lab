package com.example.noteme_lab;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

//essentially, this class is used to set the note view when being shown on the main page through the listview
public class MyNoteAdapter extends ArrayAdapter<NoteManager> implements Filterable {

    //initialize variables
    private final List<NoteManager> noteList;
    private final List<NoteManager> filteredNoteList;


    public MyNoteAdapter(@NonNull Context context, List<NoteManager> notesList) {
        super(context, 0, notesList);
        this.noteList = notesList;
        this.filteredNoteList = new ArrayList<>(notesList);
    }

    //this will set the note view when shown on the main page in the list view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            //get the layout for the note
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_cell, parent, false);

        TextView title = convertView.findViewById(R.id.tv_cellTitle);
        TextView desc = convertView.findViewById(R.id.tv_cellDescription);
        TextView color = convertView.findViewById(R.id.tv_cellColour);

        NoteManager note = getItem(position);

        if (note != null) {
            title.setText(note.getTitle());
            desc.setText(note.getDesc());
            color.setText(note.getColour());
            convertView.setBackgroundColor(Color.parseColor(note.getColour()));
        }
        return convertView;
    }

    //searching filter for search bar
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterText = constraint.toString().toLowerCase().trim();
                List<NoteManager> filteredNotes = new ArrayList<>();

                if(filterText.isEmpty()){
                    filteredNotes.addAll(noteList);
                }
                else{
                    for(NoteManager note: noteList){
                        if(note.getTitle().toLowerCase().contains(filterText) ||
                        note.getDesc().toLowerCase().contains(filterText)){
                            filteredNotes.add(note);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values= filteredNotes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNoteList.clear();
                filteredNoteList.addAll((List<NoteManager>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return filteredNoteList.size();
    }

    @Override
    public NoteManager getItem(int position) {
        return filteredNoteList.get(position);
    }
}

