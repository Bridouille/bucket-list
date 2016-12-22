package com.bridou_n.bucketlist.features.list;

import android.util.Log;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.models.Task;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by bridou_n on 22/12/2016.
 */

public class ListPresenter {

    private static final String TAG = "LIST_PRESENTER";

    private ListActivity view;
    private Realm realm;
    private CompositeSubscription subs = new CompositeSubscription();
    private TasksRecyclerViewAdapter tasksAdapter;
    private TasksRecyclerViewAdapter currentAdapter;

    public ListPresenter(ListActivity view, Realm realm) {
        this.view = view;
        this.realm = realm;
    }

    public void dropView() {
        subs.clear();
        view = null;
    }

    public void getAllTasks() {
        // Initial tasks query
        RealmResults<Task> tasks = realm.where(Task.class).findAllSortedAsync(
                new String[]{"priority", "lastEdit"},
                new Sort[]{Sort.DESCENDING, Sort.ASCENDING});

        tasksAdapter = new TasksRecyclerViewAdapter(view, tasks, true, realm);
        currentAdapter = tasksAdapter;

        subs.add(tasks.asObservable()
                .filter(RealmResults::isLoaded)
                .doOnNext(results -> {
                    RealmResults<Task> done = results.where().equalTo("done", true).findAll(); // For overall done / to-do in the toolbar

                    view.updateSubtitle(done.size(), results.size() - done.size());
                })
                .filter(r -> currentAdapter == tasksAdapter)
                .subscribe(results -> {
                    if (results.size() == 0) {
                        view.showEmptyState(view.getString(R.string.you_dont_have_any_task_to_do_yet), R.drawable.ic_assignment_turned_in_black_48dp);
                    } else {
                        view.showTasks(currentAdapter);
                    }
                }));
    }

    public void onTaskSearched(String s) {

        if (s != null && s.length() > 0) { // Search task query
            RealmResults<Task> tasks = realm.where(Task.class)
                    .contains("title", s, Case.INSENSITIVE)
                    .or()
                    .contains("content", s, Case.INSENSITIVE)
                    .findAllSortedAsync(new String[]{"priority", "lastEdit"}, new Sort[]{Sort.DESCENDING, Sort.ASCENDING});

            currentAdapter = new TasksRecyclerViewAdapter(view, tasks, true, realm);

            subs.add(tasks.asObservable()
                    .filter(RealmResults::isLoaded)
                    .filter(r -> currentAdapter != tasksAdapter)
                    .subscribe(results -> {
                        if (results.size() == 0) {
                            view.showEmptyState(view.getString(R.string.no_task_matches_your_search), R.drawable.ic_assignment_late_black_48dp);
                        } else {
                            view.showTasks(currentAdapter);
                        }
                    }));
        } else {
            currentAdapter = tasksAdapter;
            view.showTasks(currentAdapter);
        }
    }
}
