package com.bridou_n.bucketlist.features.addEdit;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.models.Task;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddEditActivity extends AppCompatActivity {

    public static final String TASK_ID = "taskID";

    private Realm realm;
    private Task task;

    @BindView(R.id.container) ConstraintLayout container;
    @BindView(R.id.titleEt) EditText titleEt;
    @BindView(R.id.contentEt) EditText contentEt;
    @BindView(R.id.priority) AppCompatSpinner priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        // Setup the priorities spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(adapter);

        // Getting the given task, if any
        String id = getIntent().getStringExtra(TASK_ID);
        if (id != null) {
            task = realm.where(Task.class).equalTo("id", id).findFirst();
            titleEt.setText(task.getTitle());
            contentEt.setText(task.getContent());
            priority.setSelection(task.getPriority());
        }

        // Set the actionbar title
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
                String title = titleEt.getText().toString();
                String content = contentEt.getText().toString();
                int prio = priority.getSelectedItemPosition();

                if (title.length() == 0) {
                    Snackbar.make(container, getString(R.string.the_title_cannot_be_empty), Snackbar.LENGTH_LONG).show();
                    return false;
                }

                if (task != null) {
                    realm.executeTransaction(tRealm -> {
                        task.setTitle(title);
                        task.setContent(content);
                        task.setPriority(prio);
                        task.setLastEdit(new Date().getTime());
                    });
                } else {
                    task = new Task(title, content, false, prio);
                    realm.executeTransaction(tRealm -> {
                        tRealm.copyToRealm(task);
                    });
                }
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
