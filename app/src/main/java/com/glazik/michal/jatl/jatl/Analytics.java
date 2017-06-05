package com.glazik.michal.jatl.jatl;

import android.os.Bundle;

import com.glazik.michal.jatl.jatl.models.Note;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Michał on 2017-05-17.
 */

public class Analytics
{
    private static final String CREATE_NEW_NOTE_EVENT_NAME = "createNewNote";
    private static final String NUMBER_OF_COLUMNS_EVENT_NAME = "numberOfColumns";

    public static void createNewNoteEvent(FirebaseAnalytics firebaseAnalytics, Note note) {
        Bundle bundle = new Bundle();
        bundle.putString("noteTitle", note.getTitle());
        firebaseAnalytics.logEvent(CREATE_NEW_NOTE_EVENT_NAME, bundle);
    }

    public static void changeNumberOfColumnsOnListEvent(FirebaseAnalytics firebaseAnalytics, int numberOfColumns) {
        Bundle bundle = new Bundle();
        bundle.putInt("numberOfColumns", numberOfColumns);
        firebaseAnalytics.logEvent(NUMBER_OF_COLUMNS_EVENT_NAME, bundle);
    }
}
