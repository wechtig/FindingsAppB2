package at.fhjoanneum.findingsapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import at.fhjoanneum.findingsapp.BL.DownloadCallback;
import at.fhjoanneum.findingsapp.BL.RequestCallback;

public class ProjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private DatePickerDialog picker;
    private DatePickerDialog pickerTo;
    private EditText eText;
    private EditText eTextTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        List<String> projects = getIntent().getExtras().getStringArrayList("Projects");
        Collections.reverse(projects);
        spinner = findViewById(R.id.project_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProjectActivity.this,
                android.R.layout.simple_spinner_item, projects);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(this);

        eText=(EditText) findViewById(R.id.editText1);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = monthOfYear + 1;
                                String monthOfYearStr = "";
                                if(monthOfYear < 10) {
                                    monthOfYearStr = "0" + monthOfYear;
                                } else {
                                    monthOfYearStr = monthOfYear + "";
                                }

                                String dayOfMonthStr = "";
                                if(dayOfMonth < 10) {
                                    dayOfMonthStr = "0" + dayOfMonth;
                                } else {
                                    dayOfMonthStr = dayOfMonth + "";
                                }
                                eText.setText(dayOfMonthStr + "-" + (monthOfYearStr) + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        eTextTo=(EditText) findViewById(R.id.editText2);
        eTextTo.setInputType(InputType.TYPE_NULL);
        eTextTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                pickerTo = new DatePickerDialog(ProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = monthOfYear + 1;
                                String monthOfYearStr = "";
                                if(monthOfYear < 10) {
                                    monthOfYearStr = "0" + monthOfYear;
                                } else {
                                    monthOfYearStr = monthOfYear + "";
                                }

                                String dayOfMonthStr = "";
                                if(dayOfMonth < 10) {
                                    dayOfMonthStr = "0" + dayOfMonth;
                                } else {
                                    dayOfMonthStr = dayOfMonth + "";
                                }

                                eTextTo.setText(dayOfMonthStr + "-" + (monthOfYearStr) + "-" + year);
                            }
                        }, year, month, day);
                pickerTo.show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String dateFrom = eText.getText().toString();
        String dateTo = eTextTo.getText().toString();

        String project = spinner.getSelectedItem().toString();
        Intent intent = new Intent(ProjectActivity.this, MainActivity.class);
        intent.putExtra("selectedProject", project);
        intent.putExtra("dateFrom", dateFrom);
        intent.putExtra("dateTo", dateTo);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
