package com.bridou_n.bucketlist.features.list;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.features.addEdit.AddEditActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ListActivity extends AppCompatActivity {

    private static final String PREF_TUTO_KEY = "prefTuto";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.empty) ConstraintLayout empty;
    @BindView(R.id.empty_content) TextView emptyContent;
    @BindView(R.id.empty_image) ImageView emptyImage;
    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.add_note) FloatingActionButton addNote;

    private ActionBar ab;
    private Realm realm;
    private ListPresenter presenter;
    private Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_task_list);

        realm = Realm.getDefaultInstance();

        ab = getSupportActionBar();

        // Setup the list
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Setup the presenter
        presenter = new ListPresenter(this, realm);
        presenter.getAllTasks();

        // Show the tutorial
        if (!getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_TUTO_KEY, false)) {
            showTutorial();
        }
    }

    public void showTutorial() {
        new TapTargetSequence(this)
                .targets(TapTarget.forToolbarMenuItem(toolbar, R.id.action_search_task, getString(R.string.search_tasks), getString(R.string.click_here_to_search_your_tasks))
                                .dimColor(R.color.primaryText)
                                .drawShadow(true)
                                .cancelable(false),
                        TapTarget.forView(addNote, getString(R.string.add_task), getString(R.string.this_is_where_you_can_add_new_things_to_do))
                            .dimColor(R.color.primaryText)
                            .drawShadow(true)
                            .tintTarget(false)

                ).start();
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(PREF_TUTO_KEY, true).apply();
    }

    @OnClick(R.id.add_note)
    public void onAddNoteClicked() {
        startActivity(new Intent(this, AddEditActivity.class));
    }

    public void updateSubtitle(int done, int todo) {
        if (ab != null) {
            ab.setSubtitle(String.format(Locale.getDefault(), getString(R.string.x_done_x_todo), done, todo));
        }
    }

    public void showEmptyState(String msg, int iconRes) {
        rv.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        emptyContent.setText(msg);
        emptyImage.setImageResource(iconRes);
    }

    public void showTasks(TasksRecyclerViewAdapter adapter) {
        rv.setAdapter(adapter);
        empty.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_list, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_task).getActionView();
        searchView.setQueryHint(getString(R.string.search_tasks));

        sub = RxSearchView.queryTextChanges(searchView) // Watch for the user's typing
                .skip(1)
                .debounce(200, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    presenter.onTaskSearched(s);
                });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        presenter.dropView();
        realm.close();
    }
}
