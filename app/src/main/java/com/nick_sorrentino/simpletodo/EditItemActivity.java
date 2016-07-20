package com.nick_sorrentino.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditItemActivity extends AppCompatActivity {
    EditText etTitle;
    TodoItem todoItem;
    Spinner spPriority;
    DatePicker dpDueDate;
    Date newDueDate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // get the TodoItem from the parent activity passed through the intent
        // http://guides.codepath.com/android/Using-Intents-to-Create-Flows
        todoItem = (TodoItem) getIntent().getSerializableExtra("todo");

        etTitle = (EditText)findViewById(R.id.etTitle);
        // set the string in the title
        etTitle.setText(todoItem.getTitle());

        // get priority dropdown
        spPriority = (Spinner)findViewById(R.id.spPriority);
        initializePrioritySpinner(spPriority);

        // date picker
        dpDueDate = (DatePicker)findViewById(R.id.dpDueDate);
        initializeDatePicker(dpDueDate);
    }

    /* sets up drop down, sets initial value */
    private void initializePrioritySpinner(Spinner spPriority){
        List<String> list = new ArrayList<String>();
        list.add("Low");
        list.add("Medium");
        list.add("High");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(dataAdapter);

        int listSize = list.size();

        if ( todoItem.getPriority() != null ) {
            // search the list to see if the priority matches
            for (int i = 0; i < listSize; i++) {
                if (todoItem.getPriority().equals(list.get(i))) {
                    spPriority.setSelection(i);
                }
            }
        }
    }

    /* sets up the date picker, attaches listeners and sets inital date */
    private void initializeDatePicker(DatePicker dp){
        Date dueDate = todoItem.getDueDate();
        Calendar cal = Calendar.getInstance();


        if ( dueDate == null ) {
            cal.setTime(new Date());
        }else{
            cal.setTime(dueDate);
        }

        // initialize the date picker with the date and attach a listener for change
        dp.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDueDate = getDate(year, monthOfYear, dayOfMonth);
            }
        });
    }

    /* given a year, month, and day return back a date object */
    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /* click handler to save the view */
    public void onSave(View view) {
        // get the values from the UI
        String updatedText = etTitle.getText().toString();
        String priority = spPriority.getSelectedItem().toString();

        // only update the date model if its been updated
        if ( newDueDate != null ) {
            todoItem.setDueDate(newDueDate);
        }

        //update the model
        todoItem.setTitle(updatedText);
        todoItem.setPriority(priority);


        Intent returnData = new Intent();
        returnData.putExtra("todo", todoItem);

        setResult(RESULT_OK, returnData);
        finish();
    }

    /* click handler to cancel */
    public void onCancel(View view) {
        Intent returnData = new Intent();
        returnData.putExtra("todo", todoItem);

        setResult(RESULT_OK, returnData);
        finish();
    }
}
