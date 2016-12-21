package com.bridou_n.bucketlist.features.list;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.features.addEdit.AddEditActivity;
import com.bridou_n.bucketlist.models.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.empty) ConstraintLayout empty;
    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.add_note) FloatingActionButton addNote;

    private Realm realm;
    private TasksRecyclerViewAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        RealmResults<Task> tasks = realm.where(Task.class).findAllSortedAsync(
                new String[]{"priority", "dateAdded", "done"},
                new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});

        tasksAdapter = new TasksRecyclerViewAdapter(this, tasks, true, realm);

        tasks.addChangeListener(newResults -> {
            empty.setVisibility(newResults.size() > 0 ? View.GONE : View.VISIBLE);
            rv.setVisibility(newResults.size() > 0 ? View.VISIBLE : View.GONE);
        });

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setAdapter(tasksAdapter);
    }

    @OnClick(R.id.add_note)
    public void onAddNoteClicked() {
        startActivity(new Intent(this, AddEditActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}