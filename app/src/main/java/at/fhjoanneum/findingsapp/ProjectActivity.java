package at.fhjoanneum.findingsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.fhjoanneum.findingsapp.BL.DownloadCallback;
import at.fhjoanneum.findingsapp.BL.RequestCallback;

public class ProjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;

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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String project = spinner.getSelectedItem().toString();
        Intent intent = new Intent(ProjectActivity.this, MainActivity.class);
        intent.putExtra("selectedProject", project);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
