package com.app.archcompelemtsample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.archcompelemtsample.activities.AddEditNoteActivity;
import com.app.archcompelemtsample.adapters.NoteAdapter;
import com.app.archcompelemtsample.models.Note;
import com.app.archcompelemtsample.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;

    private NoteViewModel noteViewModel;
    RecyclerView recyclerVwNotes;
    NoteAdapter noteAdapter = new NoteAdapter();
    FloatingActionButton fltActionBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fltActionBtn = findViewById(R.id.flt_add_note_btn);

        fltActionBtn.setOnClickListener(v -> {
            Intent iAdd = new Intent(MainActivity.this, AddEditNoteActivity.class);
            startActivityForResult(iAdd, ADD_NOTE_REQUEST);
        });

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        //noteViewModel.insert(new Note("Title X", "Description X", 4));

        setRecyclerView(); //To set recycler view and it's adapter and arraylist

        Log.d("Notes", noteViewModel.getAllNotes().toString());
        //Toast.makeText(this, "" + noteViewModel.getAllNotes(), Toast.LENGTH_SHORT).show();
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //Toast.makeText(MainActivity.this, "onChanged: " + notes.get(3).getTitle() , Toast.LENGTH_SHORT).show();
                Log.d("List", "" + notes.size());
                noteAdapter.submitList(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = noteAdapter.getNoteAt(viewHolder.getAdapterPosition());
                noteViewModel.delete(note);
            }
        }).attachToRecyclerView(recyclerVwNotes);

        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    private void setRecyclerView(){
        recyclerVwNotes = findViewById(R.id.recyler_vw_notes);
        recyclerVwNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerVwNotes.setHasFixedSize(true);

        recyclerVwNotes.setAdapter(noteAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);

            Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
        }else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK){
             int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
             if(id == -1){
                 Toast.makeText(MainActivity.this, "Can't be edited", Toast.LENGTH_SHORT).show();
                 return;
             }

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(MainActivity.this, "Note Updated", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "Note Not Saved!!!", Toast.LENGTH_SHORT).show();
        }
    }

    //For creating option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}