package com.nick_sorrentino.simpletodo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemDialogListener {
    ArrayList<TodoItem> items;
    ArrayAdapter<TodoItem> itemsAdapter;
    ListView lvItems;
    private TodoItemDatabase todoItemDatabase;
    private static final String TAG = "ToDo";
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the items from the view
        lvItems = (ListView)findViewById(R.id.lvItems);

        // initialize the items array
        items = new ArrayList<TodoItem>();

        // set up the sqllite database to store tasks
        todoItemDatabase = new TodoItemDatabase(this);

        // read the items from the database into items
        readItems();

        // set up the adapter using the custom
        itemsAdapter = new TodoItemAdapter(this, items);
        itemsAdapter.sort(new SortByDueDate());
        lvItems.setAdapter(itemsAdapter);

        // setup long click on list to remove items
        setupListViewListener();
    }

    /* Sort method for the todo items */
    public class SortByDueDate implements Comparator {
        public int compare(Object o1, Object o2) {
            TodoItem t1 = (TodoItem) o1;
            TodoItem t2 = (TodoItem) o2;
            Date d1 = t1.getDueDate();
            Date d2 = t2.getDueDate();
            int time1;
            int time2;
            int result = 0;
            int p1 = getPriorityValue(t1.getPriority());
            int p2 = getPriorityValue(t2.getPriority());

            if (d1 == null) {
                result = (d2 == null) ? 0 : 1;
            } else if (d2 == null) {
                result = 1;
            } else {
                result = d1.compareTo(d2);
                Log.d(TAG, t1.getTitle()+ "," + t2.getTitle() + "===" + result);
            }

            if ( result == 0 ){
                Log.d("ToDo", p1 +"============"+ p2);

                result = p1 - p2;
            }

            return result;
        }
    }

    private int getPriorityValue(String priority) {
        if ( priority.equals("Low") ) {
            return 3;
        } else if ( priority.equals("Medium") ) {
            return 2;
        } else if ( priority.equals("High") ) {
            return 1;
        }

        return 0;
    }

    /* Callback when other activities resolve and return back to this MainActivity */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            TodoItem todoItem;

            todoItem = null;

            // get the todoItem from the editItemActivity
            try {
                todoItem = (TodoItem) data.getExtras().getSerializable("todo");
            } catch ( Exception e ) {
                Toast.makeText(this, "There has been an error updating your items", Toast.LENGTH_SHORT).show();
            }

            if ( todoItem != null ) {
                // find the index in the items array
                int arrayIndex = getTodoItemIndexById(todoItem.getId());

                if (arrayIndex > -1) {
                    // update the value in the database
                    updateItemInDB(todoItem);
                    // update the items list
                    items.set(arrayIndex, todoItem);

                    // update the adapter for the UI
                    itemsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "There has been an error updating your items", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void onFinishEditItemDialog(TodoItem todoItem) {
        if ( todoItem != null ) {
            // find the index in the items array
            int arrayIndex = getTodoItemIndexById(todoItem.getId());

            if (arrayIndex > -1) {
                // update the value in the database
                updateItemInDB(todoItem);
                // update the items list
                items.set(arrayIndex, todoItem);

                //resort the adapter since the due date could change
                itemsAdapter.sort(new SortByDueDate());

                // update the adapter for the UI
                itemsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "There has been an error updating your items", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Loads the edit view activity */
    private void launchEditView(TodoItem todo) {
        FragmentManager fm = getSupportFragmentManager();
        EditItemDialogFragment editItemDialogFragment = EditItemDialogFragment.newInstance(todo);
        editItemDialogFragment.show(fm, "edititem_dialog");
    }

    /* Binds the click listener to edit items and the long click listener to remove items */
    private void setupListViewListener() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                launchEditView(items.get(pos));
            }
        });

        // long click to remove items
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long id) {
                TodoItem todoItem = (TodoItem) items.get(pos);

                // remove the todoItem from the items array
                items.remove(pos);

                //update the ui
                itemsAdapter.notifyDataSetChanged();

                //remove the item from the database
                removeItemFromDB(todoItem.getId());
                return true;
            }
        });
    }

    /* Reads the TodoItems from the Database and adds them to the items array */
    private void readItems() {
        SQLiteDatabase db = todoItemDatabase.getReadableDatabase();
        String[] columns = {
                TodoItemDatabase.COLUMN_TASK_ID,
                TodoItemDatabase.COLUMN_TASK,
                TodoItemDatabase.COLUMN_PRIORITY,
                TodoItemDatabase.COLUMN_DUE_DATE
        };

        Cursor cursor = db.query(TodoItemDatabase.TODO_TABLE_NAME,columns, null, null, null, null, null);

        items = new ArrayList<TodoItem>();

        while(cursor.moveToNext()) {
            int titleIdx = cursor.getColumnIndex(TodoItemDatabase.COLUMN_TASK);
            int idIdx = cursor.getColumnIndex(TodoItemDatabase.COLUMN_TASK_ID);
            int priorityIdx = cursor.getColumnIndex(TodoItemDatabase.COLUMN_PRIORITY);
            int dueDateIdx = cursor.getColumnIndex(TodoItemDatabase.COLUMN_DUE_DATE);
            Date dueDate = null;

            String dueDateString = cursor.getString(dueDateIdx);
            Log.d(TAG, "Date==" + dueDateString);
            if ( dueDateString != null ) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    dueDate = dateFormat.parse(dueDateString);
                } catch (ParseException e) {
                    Log.e(TAG, "Parsing date failed");
                }
            }


            // create a TodoItem Class item
            TodoItem todo = new TodoItem(cursor.getInt(idIdx), cursor.getString(titleIdx), cursor.getString(priorityIdx), dueDate);

            // add the Class Item to the list
            items.add(todo);
        }

        cursor.close();
        db.close();

    }

    /* Writes a new todoItem into the database and returns the row id */
    private int writeItemToDB(String todoTitle, String priority) {
        SQLiteDatabase db = todoItemDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoItemDatabase.COLUMN_TASK, todoTitle);
        values.put(TodoItemDatabase.COLUMN_PRIORITY, priority);

        // insert the item into the DB
        long rowId = db.insertWithOnConflict(TodoItemDatabase.TODO_TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        db.close();

        return (int) rowId;
    }

    /* updates the values of the row in the database for a given todoItem */
    private void updateItemInDB(TodoItem todoItem) {
        SQLiteDatabase db = todoItemDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoItemDatabase.COLUMN_TASK, todoItem.getTitle());
        values.put(TodoItemDatabase.COLUMN_PRIORITY, todoItem.getPriority());

        if ( todoItem.getDueDate() != null ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
            values.put(TodoItemDatabase.COLUMN_DUE_DATE, sdf.format(todoItem.getDueDate()));
        }
        String[] args = new String[]{ Integer.toString(todoItem.getId()) };

        db.update(TodoItemDatabase.TODO_TABLE_NAME, values, TodoItemDatabase.COLUMN_TASK_ID + " = ?", args );
        db.close();
    }

    /* removes an item from the database */
    private void removeItemFromDB(int id) {
        SQLiteDatabase db = todoItemDatabase.getWritableDatabase();
        String[] args = new String[]{ Integer.toString(id) };

        db.delete(TodoItemDatabase.TODO_TABLE_NAME, TodoItemDatabase.COLUMN_TASK_ID + " = ?", args);
        db.close();
    }

    /* gets the array index of a given todoItem searching using its id */
    private int getTodoItemIndexById(int id) {
        final int size = items.size();

        for ( int i = 0; i < size; i++ ){
            TodoItem todoItem = items.get(i);

            if ( todoItem.getId() == id ){
                return i;
            }
        }

        return -1;
    }

    /* Click handler when adding new TodoItems in the UI */
    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        String priority = "Medium";

        int id = writeItemToDB(itemText, priority);

        TodoItem todo = new TodoItem(id, itemText, priority, null);

        itemsAdapter.add(todo);
        etNewItem.setText("");

    }
}
