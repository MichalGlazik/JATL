package com.glazik.michal.jatl.jatl.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.glazik.michal.jatl.jatl.BitmapAndStringConverters;
import com.glazik.michal.jatl.jatl.R;
import com.glazik.michal.jatl.jatl.activities.NoteDetailsActivity;
import com.glazik.michal.jatl.jatl.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;


public class RecyclerNoteAdapter extends FirebaseRecyclerAdapter<Note, RecyclerNoteAdapter.NoteViewHolder>
{
    private final Context context;
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public RecyclerNoteAdapter(@NonNull Context context) {

        super(Note.class, R.layout.note_row, NoteViewHolder.class, database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        this.context = context;
    }

    @Override
    protected void populateViewHolder(NoteViewHolder viewHolder, final Note model, int position) {
        String imageAsString = model.getImage();
        Bitmap imageAsBitmap;
        if (imageAsString == null)
        {
            imageAsBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
        }
        else
        {
            imageAsBitmap = BitmapAndStringConverters.StringToBitMap(imageAsString);
        }
        viewHolder.noteImage.setImageBitmap(imageAsBitmap);
        viewHolder.noteTitle.setText(model.getTitle());
        viewHolder.noteBody.setText(model.getBody());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItem(model);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view)
            {
                deleteNote(model);
                return true;
            }
        });


    }

    private void showItem(Note note) {
        Intent i = new Intent(context, NoteDetailsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.putExtra(NoteDetailsActivity.NOTE_EXTRA_KEY, (Serializable) note);
        context.startActivity(i);
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView noteImage;
        TextView noteTitle;
        TextView noteBody;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteImage = (ImageView) itemView.findViewById(R.id.note_image);
            noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            noteBody = (TextView) itemView.findViewById(R.id.note_body);
        }
    }
    private void deleteNote(Note note)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query noteToDelete = ref.child(userID)
                .orderByChild("id")
                .equalTo(note.getId());
        noteToDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snsh: dataSnapshot.getChildren()) {
                    snsh.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
