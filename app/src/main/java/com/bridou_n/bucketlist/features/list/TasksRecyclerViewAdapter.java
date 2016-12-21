package com.bridou_n.bucketlist.features.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridou_n.bucketlist.R;
import com.bridou_n.bucketlist.features.addEdit.AddEditActivity;
import com.bridou_n.bucketlist.models.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import static com.bridou_n.bucketlist.features.addEdit.AddEditActivity.TASK_ID;

/**
 * Created by bridou_n on 21/12/2016.
 */

public class TasksRecyclerViewAdapter extends RealmRecyclerViewAdapter<Task, TasksRecyclerViewAdapter.TaskHolder> {

    private static final String TAG = "TASKS_RV_ADAPTER";

    private Context ctx;
    private Realm realm;

    public TasksRecyclerViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Task> data, boolean autoUpdate, Realm realm) {
        super(context, data, autoUpdate);
        ctx = context;
        this.realm = realm;
    }

    public class TaskHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.content) TextView content;
        @BindView(R.id.state) AppCompatCheckBox state;

        private Task task;

        TaskHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindTo(Task task) {
            this.task = task;
            title.setText(task.getTitle());
            content.setText(task.getContent());
            state.setChecked(task.isDone());
            if (task.isDone()) {
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                content.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                title.setPaintFlags(title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                content.setPaintFlags(title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        @OnClick(R.id.content)
        public void onTaskclicked(View v) {
            Intent intent = new Intent(ctx, AddEditActivity.class);

            intent.putExtra(TASK_ID, task.getId());
            ctx.startActivity(intent);
        }

        @OnClick(R.id.state)
        public void onCheckedChanged(View v) {
            realm.executeTransaction(tRealm -> {
                task.setDone(!task.isDone());
            });
        }
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        holder.bindTo(getItem(position));
    }
}