package com.glazik.michal.jatl.jatl.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.glazik.michal.jatl.jatl.Analytics;
import com.glazik.michal.jatl.jatl.ColumnNumberPreferences;
import com.glazik.michal.jatl.jatl.R;
import com.glazik.michal.jatl.jatl.adapters.RecyclerNoteAdapter;
import com.glazik.michal.jatl.jatl.configs.OnConfigLoadedListener;
import com.glazik.michal.jatl.jatl.configs.RemoteConfig;
import com.glazik.michal.jatl.jatl.models.Note;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;




public class NoteListActivity extends AppCompatActivity implements OnConfigLoadedListener
{

    private ColumnNumberPreferences columnNumberPreferences;

    private RecyclerNoteAdapter adapter;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list);

        RemoteConfig.addOnConfigLoadedListener(this);
    }

    @Override
    public void onConfigLoaded() {
        bindView();
    }

    @Override
    public void onConfigLoadFail() {
        bindView();
    }

    private void bindView() {
        columnNumberPreferences = new ColumnNumberPreferences(this);
        createNoteAdapter();
        initializeGrid();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void createNoteAdapter() {
        adapter = new RecyclerNoteAdapter(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initializeGrid() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, columnNumberPreferences.getSelectedNumberOfColumns());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                layoutManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
            }
        });
    }

    private void setNumberOfGridColumns(int numberOfColumns) {
        columnNumberPreferences.saveSelectedNumberOfColumns(numberOfColumns);
        initializeGrid();

        Analytics.changeNumberOfColumnsOnListEvent(firebaseAnalytics, numberOfColumns);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.col_number: {
                showColNumberChooserDialog();
                return true;
            }
            case R.id.add_note: {
                startAddNoteActivity();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void startAddNoteActivity() {
        Intent i = new Intent(this, AddNoteActivity.class);
        startActivity(i);
    }



    private void showColNumberChooserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        List<CharSequence> options = new ArrayList<>();

        options.add("1");
        options.add("2");
        options.add("3");
        options.add("4");
        options.add("5");

        builder.setItems(options.toArray(new CharSequence[5]), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setNumberOfGridColumns(which + 1);
            }
        });

        builder.setTitle("Select number of columns");

        builder.create().show();
    }

}
