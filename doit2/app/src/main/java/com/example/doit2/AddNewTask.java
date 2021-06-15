package com.example.doit2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.doit2.Model.ToDoModel;
import com.example.doit2.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskDate;
    private Button newTaskTime;
    private ImageButton newTaskSaveButton;

    private DatabaseHandler db;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = requireView().findViewById(R.id.newTaskText);
        newTaskDate = requireView().findViewById(R.id.date_btn);
        newTaskTime = requireView().findViewById(R.id.time_btn);
        newTaskSaveButton = requireView().findViewById(R.id.newTaskButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            String date = bundle.getString("date");
            newTaskText.setText(task);
            newTaskDate.setText(date.toCharArray(), 0, 7);
            newTaskTime.setText(date.toCharArray(), 7, 5);
            assert task != null;
            if(task.length()>0)
                newTaskSaveButton.setColorFilter(R.color.design_default_color_secondary);
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setColorFilter(R.color.design_default_color_secondary);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setColorFilter(R.color.material_on_primary_disabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        newTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        newTaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButton();
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String date = newTaskDate.getText().toString() +" " + newTaskTime.getText().toString();
                if(finalIsUpdate){
                    db.updateTask(bundle.getInt("id"), text, date);
                }
                else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setDate(date);
                    task.setStatus(0);
                    db.insertTask(task);
                }
                dismiss();
            }
        });

    }

    private void handleTimeButton() {
        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar calendar1 = Calendar.getInstance();

                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);

                CharSequence timeCharSequence = DateFormat.format("hh:mm", calendar1);
                newTaskTime.setText(timeCharSequence);

            }
        }, HOUR,MINUTE,true);

        timePickerDialog.show();
    }

    private void handleDateButton(){
        Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateString = year + " " + month + " " + dayOfMonth;
                newTaskDate.setText(dateString);

                Calendar calendar1 = Calendar.getInstance();

                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, dayOfMonth);

                CharSequence dateCharSequence = DateFormat.format("dd MMM", calendar1);
                newTaskDate.setText(dateCharSequence);
            }
        }, YEAR,MONTH,DATE);

        datePickerDialog.show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }

}
