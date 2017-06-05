package com.glazik.michal.jatl.jatl.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.glazik.michal.jatl.jatl.BitmapAndStringConverters;
import com.glazik.michal.jatl.jatl.R;
import com.glazik.michal.jatl.jatl.SaveNoteOnDiskDelegate;
import com.glazik.michal.jatl.jatl.models.Note;

import java.text.SimpleDateFormat;

/**
 * Created by Michał on 2017-05-17.
 */

public class NoteDetailsActivity extends AppCompatActivity {


    public static final String NOTE_EXTRA_KEY = "note";

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_details);

        Button button = (Button) findViewById(R.id.button);
        final ScrollView sv = (ScrollView) findViewById(R.id.scroll);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sv.scrollTo(0, 0);
            }
        });

        Intent i = getIntent();
        note = (Note) i.getExtras().getSerializable(NOTE_EXTRA_KEY);

        showRecipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_to_disc: {
                saveRecipeToDisk(note);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void showRecipe() {
        TextView noteTitle = (TextView) findViewById(R.id.note_title);
        TextView noteBody = (TextView) findViewById(R.id.note_body);
        TextView noteDate = (TextView) findViewById(R.id.note_creation_date);
        ImageView noteImage = (ImageView) findViewById(R.id.photo);

        noteTitle.setText(note.getTitle());
        noteBody.setText(note.getBody());
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        String noteDateAsString = simpleDateFormat.format(note.getDate());

        noteDate.setText(noteDateAsString);
        Bitmap noteImageAsBitmap;
        if (note.getImage() != null)
            noteImageAsBitmap = BitmapAndStringConverters.StringToBitMap(note.getImage());
        else
            noteImageAsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        noteImage.setImageBitmap(noteImageAsBitmap);
    }

    private void saveRecipeToDisk(Note note) {
        SaveNoteOnDiskDelegate delegate = new SaveNoteOnDiskDelegate();
        delegate.saveRecipe(this, note);
    }
}

