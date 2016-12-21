package com.bridou_n.bucketlist.features.addEdit;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.models.Task;

import butterknife.ButterKnife;
import io.realm.Realm;

public class AddEditActivity extends AppCompatActivity {

    public static final String TASK_ID = "taskID";

    private Realm realm;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        String id = getIntent().getStringExtra(TASK_ID);
        if (id != null) {
            task = realm.where(Task.class).equalTo("id", id).findFirst();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(task == null ? getString(R.string.add_task) : getString(R.string.edit_task));
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                // TODO: 21/12/2016 save or update the note to realm here
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
