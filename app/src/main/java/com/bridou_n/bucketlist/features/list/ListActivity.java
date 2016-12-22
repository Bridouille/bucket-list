package com.bridou_n.bucketlist.features.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.features.addEdit.AddEditActivity;
import com.bridou_n.bucketlist.models.Task;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.empty) ConstraintLayout empty;
    @BindView(R.id.empty_content) TextView emptyContent;
    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.add_note) FloatingActionButton addNote;

    private ActionBar ab;
    private Realm realm;
    private TasksRecyclerViewAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_task_list);

        realm = Realm.getDefaultInstance();

        ab = getSupportActionBar();

        RealmResults<Task> tasks = realm.where(Task.class).findAllSortedAsync(
                new String[]{"priority", "lastEdit"},
                new Sort[]{Sort.DESCENDING, Sort.ASCENDING});

        tasksAdapter = new TasksRecyclerViewAdapter(this, tasks, true, realm);

        tasks.addChangeListener(newResults -> {
            if (newResults.size() == 0) {
                showEmptyState(getString(R.string.you_dont_have_any_task_to_do_yet));
            } else {
                showContent(tasksAdapter);
            }

            RealmResults<Task> done = newResults.where().equalTo("done", true).findAll();

            if (ab != null) {
                ab.setSubtitle(String.format(Locale.getDefault(), getString(R.string.x_done_x_todo), done.size(), newResults.size() - done.size()));
            }
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

    public void showEmptyState(String msg) {
        rv.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        emptyContent.setText(msg);
    }

    public void showContent(TasksRecyclerViewAdapter adapter) {
        empty.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_list, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_task).getActionView();
        searchView.setQueryHint(getString(R.string.search_tasks));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 1) {

                    RealmResults<Task> tasks = realm.where(Task.class)
                            .contains("title", newText, Case.INSENSITIVE)
                            .or()
                            .contains("content", newText, Case.INSENSITIVE)
                            .findAllSortedAsync(new String[]{"priority", "lastEdit"}, new Sort[]{Sort.DESCENDING, Sort.ASCENDING});

                    tasks.addChangeListener(newResults -> {
                        if (newResults.size() == 0) {
                            showEmptyState(getString(R.string.no_task_matches_your_search));
                        } else {
                            showContent(new TasksRecyclerViewAdapter(ListActivity.this, tasks, true, realm));
                        }
                    });
                } else {
                    showContent(tasksAdapter);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
