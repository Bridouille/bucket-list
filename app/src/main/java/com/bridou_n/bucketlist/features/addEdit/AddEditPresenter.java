package com.bridou_n.bucketlist.features.addEdit;

import android.support.design.widget.Snackbar;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.models.Task;

import java.util.Date;

import io.realm.Realm;

/**
 * Created by bridou_n on 22/12/2016.
 */

public class AddEditPresenter {
    private static final String TAG = "ADD_EDIT_PRESENTER";

    private AddEditActivity view;
    private Realm realm;
    private Task task;

    public AddEditPresenter(AddEditActivity view, Realm realm) {
        this.view = view;
        this.realm = realm;
    }

    public void displayTask(String taskId) {
        if (taskId != null) {
            task = realm.where(Task.class).equalTo("id", taskId).findFirst();
            view.setupView(task);
        }
    }

    public boolean hasTask() {
        return task != null;
    }

    public void saveTask(String title, String content, int prio) {
        if (title == null || title.length() == 0) {
            view.showError(view.getString(R.string.the_title_cannot_be_empty));
            return ;
        }

        if (task != null) {
            String id = task.getId();

            realm.executeTransactionAsync(tRealm -> {
                Task t = tRealm.where(Task.class).equalTo("id", id).findFirst();

                t.setTitle(title);
                t.setContent(content);
                t.setPriority(prio);
                t.setLastEdit(new Date().getTime());
            });
        } else {
            realm.executeTransactionAsync(tRealm -> {
                Task t = new Task(title, content, false, prio);

                tRealm.copyToRealm(t);
            });
        }
        view.goBack();
    }

    public void deleteTask() {
        if (task != null) {
            String id = task.getId();

            realm.executeTransactionAsync(tRealm -> {
                tRealm.where(Task.class).equalTo("id", id).findFirst().deleteFromRealm();
            });
            view.goBack();
        }
    }
}
