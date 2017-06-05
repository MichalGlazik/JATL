package com.glazik.michal.jatl.jatl.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.glazik.michal.jatl.jatl.Analytics;
import com.glazik.michal.jatl.jatl.BitmapAndStringConverters;
import com.glazik.michal.jatl.jatl.MyNotificationPublisher;
import com.glazik.michal.jatl.jatl.R;
import com.glazik.michal.jatl.jatl.models.Note;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Michał on 2017-05-17.
 */

public class AddNoteActivity extends AppCompatActivity
{
    private static int notificationNumber = 0;
    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAnalytics firebaseAnalytics;
    private Calendar cal;
    private Calendar notyficationTime;
    private boolean setNotyfication = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
        setupViews();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        cal = Calendar.getInstance();
        notyficationTime = Calendar.getInstance();
        notificationNumber++;
    }

    private void setupViews() {
        final EditText noteTitleEditText = (EditText) findViewById(R.id.note_title_edit_text);
        final EditText noteBodyEditText = (EditText) findViewById(R.id.note_body_edit_text);
        final ImageView noteImageView = (ImageView) findViewById(R.id.note_new_photo);
        Button saveButton = (Button) findViewById(R.id.save_note_button);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String noteTitle = noteTitleEditText.getText().toString();
                String noteBody = noteBodyEditText.getText().toString();
                Note newNote = new Note();
                if (noteImageView.getDrawable() != null)
                {
                    Drawable noteImage = noteImageView.getDrawable();
                    BitmapDrawable noteImageAsBitmapDrawable = ((BitmapDrawable) noteImage);
                    Bitmap noteImageAsBitmap = noteImageAsBitmapDrawable.getBitmap();
                    String noteImageAsString = BitmapAndStringConverters.BitMapToString(noteImageAsBitmap);
                    newNote.setImage(noteImageAsString);
                }
                newNote.setTitle(noteTitle);
                newNote.setBody(noteBody);

                saveNote(newNote);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView)findViewById(R.id.note_new_photo);
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_note, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_photo: {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
                return true;
            }
            case R.id.add_alarm:
            {
                setAlarm();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void setAlarm()
    {
        TimePickerDialog timePicker = new TimePickerDialog(this, tpickerListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        timePicker.show();
        DatePickerDialog datePicker = new DatePickerDialog(this, dpickerListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();

    }

    private DatePickerDialog.OnDateSetListener dpickerListener =
            new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day)
                {
                    notyficationTime.set(Calendar.YEAR,year);
                    notyficationTime.set(Calendar.MONTH, month);
                    notyficationTime.set(Calendar.DAY_OF_MONTH, day);
                }
            };

    private TimePickerDialog.OnTimeSetListener tpickerListener =
            new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute)
                {
                    notyficationTime.set(Calendar.HOUR_OF_DAY, hour);
                    notyficationTime.set(Calendar.MINUTE, minute);
                    setNotyfication = true;
                }
            };



    public void scheduleNotification(Context context, long delay, int notificationId, String title, String text) {//delay is after how much time(in millis) from current time you want to schedule the notification
        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.launcher_icon);

        Intent intent = new Intent(context, NoteListActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
    private void saveNote(Note note) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference noteRef = database.getReference(user.getUid());
        Long delay = notyficationTime.getTimeInMillis() - cal.getTimeInMillis();
        if (setNotyfication)
        {
            scheduleNotification(AddNoteActivity.this, delay, notificationNumber, note.getTitle(), note.getBody());
            setNotyfication = false;
        }
        noteRef.push().setValue(note);
        Analytics.createNewNoteEvent(firebaseAnalytics, note);
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        finish();

    }


}
