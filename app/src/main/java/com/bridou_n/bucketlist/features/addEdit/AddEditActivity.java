package com.bridou_n.bucketlist.features.addEdit;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
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
    private AddEditPresenter presenter;
    private ActionBar ab;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) ConstraintLayout container;
    @BindView(R.id.titleEt) EditText titleEt;
    @BindView(R.id.contentEt) EditText contentEt;
    @BindView(R.id.priority) AppCompatSpinner priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        // Setup the priorities spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(adapter);

        // Setup the toolbar
        toolbar.inflateMenu(R.menu.menu_add_edit_task);
        ab = getSupportActionBar();

        // Setup the presenter
        presenter = new AddEditPresenter(this, realm);
        presenter.displayTask(getIntent().getStringExtra(TASK_ID));
    }

    public void setupView(Task task) {
        if (ab != null) {
            ab.setTitle(task == null ? getString(R.string.add_task) : getString(R.string.edit_task));
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (task != null) {
            titleEt.setText(task.getTitle());
            contentEt.setText(task.getContent());
            priority.setSelection(task.getPriority());
        }
    }

    public void showError(String err) {
        Snackbar.make(container, err, Snackbar.LENGTH_LONG).show();
    }

    public void goBack() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_edit_task, menu);

        if (!presenter.hasTask()) {
            menu.removeItem(R.id.action_delete);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                presenter.saveTask(titleEt.getText().toString(), contentEt.getText().toString(), priority.getSelectedItemPosition());
                break;
            case R.id.action_delete:
                presenter.deleteTask();
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
