package com.nick_sorrentino.simpletodo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by nick_sorrentino on 7/20/16.
 */
public class EditItemDialogFragment extends DialogFragment implements View.OnClickListener {
    EditText etTitle;
    TodoItem todoItem;
    Spinner spPriority;
    DatePicker dpDueDate;
    Button btnSave;
    Button btnCancel;
    Date newDueDate = null;


    public EditItemDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public interface EditItemDialogListener {
        void onFinishEditItemDialog(TodoItem todoItem);
    }

    public static EditItemDialogFragment newInstance(TodoItem todo) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("todo", todo);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edititem_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        todoItem = (TodoItem)getArguments().getSerializable("todo");

        etTitle = (EditText)view.findViewById(R.id.etTitle);
        // set the string in the title
        etTitle.setText(todoItem.getTitle());

        // get priority dropdown
        spPriority = (Spinner)view.findViewById(R.id.spPriority);
        initializePrioritySpinner(spPriority);

        // date picker
        dpDueDate = (DatePicker)view.findViewById(R.id.dpDueDate);
        initializeDatePicker(dpDueDate);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    /* sets up drop down, sets initial value */
    private void initializePrioritySpinner(Spinner spPriority){
        List<String> list = new ArrayList<String>();
        list.add("Low");
        list.add("Medium");
        list.add("High");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
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

    @Override
    public void onClick(View v){
        Log.d("ToDo", "CLICK " + v.getId() + "==" + R.id.btnSave);
        switch (v.getId()) {
            case R.id.btnSave:
                onSave(v);
                break;
            case R.id.btnCancel:
                onCancel(v);
                break;
        }
    }


    /* click handler to save the view */
    private void onSave(View view) {
        EditItemDialogListener activity = (EditItemDialogListener) getActivity();

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


        activity.onFinishEditItemDialog(todoItem);
        this.dismiss();
    }

    /* click handler to cancel */
    public void onCancel(View view) {
        EditItemDialogListener activity = (EditItemDialogListener) getActivity();
        activity.onFinishEditItemDialog(todoItem);
        this.dismiss();
    }


}
