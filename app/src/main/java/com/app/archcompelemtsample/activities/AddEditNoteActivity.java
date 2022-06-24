package com.app.archcompelemtsample.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.app.archcompelemtsample.R;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.app.archcompelemtsample.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.app.archcompelemtsample.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.app.archcompelemtsample.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.app.archcompelemtsample.EXTRA_PRIORITY";

    private EditText edtTitle;
    private EditText edtDescription;
    private NumberPicker numPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //To initialize all the views
        initUI();


    }

    private void initUI(){
        edtTitle = findViewById(R.id.edt_note_title);
        edtDescription = findViewById(R.id.edt_note_description);
        numPriority = findViewById(R.id.note_priority_number);

        numPriority.setMinValue(1);
        numPriority.setMaxValue(10);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            getSupportActionBar().setTitle("Edit Note");
            edtTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            edtDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numPriority.setMinValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        }else {
            getSupportActionBar().setTitle("Add Note");
        }
    }

    private void saveNote(){
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        int priority = numPriority.getValue();

        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Add Note Title & Description!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1){
            data.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}