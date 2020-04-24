package com.caspermarcinek.android.taskapp;

/**
 * Created by witch on 09/28/2017.
 */

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import com.caspermarcinek.android.taskapp.adapter.TasksAdapter;
import com.caspermarcinek.android.taskapp.database.TaskDbAdapter;
import com.caspermarcinek.android.taskapp.model.Task;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Button add_button;
    private Button clear_button;
    private Button save_button;
    private Button cancel_button;
    private EditText new_task_edit_text;
    private ListView todo_list_view;
    private LinearLayout control_buttons_linear_layout;
    private LinearLayout new_task_buttons_linear_layout;

    private TaskDbAdapter todoDbAdapter;
    private Cursor todoCursor;
    private List<Task> tasks;
    private TasksAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_button = (Button) findViewById(R.id.add_button);
        clear_button = (Button) findViewById(R.id.clear_button);
        save_button = (Button) findViewById(R.id.save_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        new_task_edit_text = (EditText) findViewById(R.id.new_task_edit_text);
        todo_list_view = (ListView) findViewById(R.id.todo_list_view);
        control_buttons_linear_layout = (LinearLayout) findViewById(R.id.control_buttons_linear_layout);
        new_task_buttons_linear_layout = (LinearLayout) findViewById(R.id.new_task_button_linear_layout);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_top);
        TextView toolBarTitle = (TextView) findViewById(R.id.toolbar_title);

        startListView();
        startButtonsOnClickListeners();
    }

    private void startListView() {
        fillListViewData();
        startListViewOnItemClick();
    }

    private void fillListViewData() {
        todoDbAdapter = new TaskDbAdapter(getApplicationContext());
        todoDbAdapter.open();
        getAllTasks();
        listAdapter = new TasksAdapter(this, tasks);
        todo_list_view.setAdapter(listAdapter);
    }

    private void getAllTasks() {
        tasks = new ArrayList<>();
        todoCursor = getAllEntriesFromDb();
        updateTaskList();
    }

    private Cursor getAllEntriesFromDb() {
        todoCursor = todoDbAdapter.getAllTasks();
        if(todoCursor != null) {
            startManagingCursor(todoCursor);
            todoCursor.moveToFirst();
        }
        return todoCursor;
    }

    private void updateTaskList() {
        if(todoCursor != null && todoCursor.moveToFirst()) {
            do {
                long id = todoCursor.getLong(TaskDbAdapter.ID_COLUMN);
                String description = todoCursor.getString(TaskDbAdapter.DESCRIPTION_COLUMN);
                boolean completed = todoCursor.getInt(TaskDbAdapter.COMPLETED_COLUMN) > 0;
                tasks.add(new Task(id, description, completed));
            } while(todoCursor.moveToNext());
        }
    }

    @Override
    protected void onDestroy() {
        if(todoDbAdapter != null)
            todoDbAdapter.close();
        super.onDestroy();
    }

    private void startListViewOnItemClick() {
        todo_list_view.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Task task = tasks.get(position);
                if(task.isCompleted()){
                    todoDbAdapter.updateTask(task.getId(), task.getDescription(), false);
                } else {
                    todoDbAdapter.updateTask(task.getId(), task.getDescription(), true);
                }
                updateListViewData();
            }
        });
    }

    private void updateListViewData() {
        todoCursor.requery();
        tasks.clear();
        updateTaskList();
        listAdapter.notifyDataSetChanged();
    }

    private void startButtonsOnClickListeners() {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_button:
                        addNewTask();
                        break;
                    case R.id.save_button:
                        saveNewTask();
                        break;
                    case R.id.cancel_button:
                        cancelNewTask();
                        break;
                    case R.id.clear_button:
                        clearCompletedTasks();
                        break;
                    default:
                        break;
                }
            }
        };

        add_button.setOnClickListener(onClickListener);
        clear_button.setOnClickListener(onClickListener);
        save_button.setOnClickListener(onClickListener);
        cancel_button.setOnClickListener(onClickListener);
    }

    private void showOnlyNewTaskPanel() {
        setVisibilityOf(control_buttons_linear_layout, false);
        setVisibilityOf(new_task_buttons_linear_layout, true);
        setVisibilityOf(new_task_edit_text, true);
    }

    private void showOnlyControlPanel() {
        setVisibilityOf(control_buttons_linear_layout, true);
        setVisibilityOf(new_task_buttons_linear_layout, false);
        setVisibilityOf(new_task_edit_text, false);
    }

    private void setVisibilityOf(View v, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        v.setVisibility(visibility);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new_task_edit_text.getWindowToken(), 0);
    }

    private void addNewTask(){

        Vibrator btnAddVibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnAddVibrate.vibrate(25);

        showOnlyNewTaskPanel();
    }

    private void saveNewTask(){

        Vibrator btnSaveVibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnSaveVibrate.vibrate(25);


        String taskDescription = new_task_edit_text.getText().toString();
        if(taskDescription.equals("")){
            new_task_edit_text.setError("Your task description cannot be empty string.");
        } else {
            todoDbAdapter.insertTask(taskDescription);
            new_task_edit_text.setText("");
            hideKeyboard();
            showOnlyControlPanel();
        }
        updateListViewData();
    }

    private void cancelNewTask() {

        Vibrator btnCancelVibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnCancelVibrate.vibrate(25);

        new_task_edit_text.setText("");
        showOnlyControlPanel();
    }

    private void clearCompletedTasks(){

        Vibrator btnClearVibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnClearVibrate.vibrate(25);

        if(todoCursor != null && todoCursor.moveToFirst()) {
            do {
                if(todoCursor.getInt(TaskDbAdapter.COMPLETED_COLUMN) == 1) {
                    long id = todoCursor.getLong(TaskDbAdapter.ID_COLUMN);
                    todoDbAdapter.deleteTask(id);
                }
            } while (todoCursor.moveToNext());
        }
        updateListViewData();
    }
}