package com.glazik.michal.jatl.jatl;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.glazik.michal.jatl.jatl.models.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Michał on 2017-05-17.
 */


public class SaveNoteOnDiskDelegate {

    private static final String FILENAME = "note.txt";

    public void saveRecipe(Context context, Note note) {
        if (!isExternalStorageWritable()) {
            Toast.makeText(context, "Could not save file - external storage is not writable", Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(context.getExternalFilesDir(null), FILENAME);

        file.getParentFile().mkdirs();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buildRecipeText(context, note).getBytes());
            fos.close();
            Toast.makeText(context, "File saved at " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error during saving file", Toast.LENGTH_LONG).show();
        }
    }

    private String buildRecipeText(Context context, Note note) {
        return context.getString(R.string.note_title_label) + "\n" + note.getTitle() + "\n\n"
                + context.getString(R.string.note_body_label) + "\n" + note.getBody() + "\n\n";
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
