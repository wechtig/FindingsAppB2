package at.fhjoanneum.findingsapp;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import at.fhjoanneum.findingsapp.BL.DownloadCallback;
import at.fhjoanneum.findingsapp.BL.HttpsGetTask;
import at.fhjoanneum.findingsapp.BL.RequestCallback;
import at.fhjoanneum.findingsapp.Data.Finding;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements DownloadCallback, RequestCallback<String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private List<Finding> findings = new ArrayList<>();
    private int currentFinding = 0;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean showText;
    private int size;
    private String project;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        project = getIntent().getExtras().getString("selectedProject");

        HttpsGetTask httpsGetTask = new HttpsGetTask(this);
        Resources res = getResources();
        int days = Integer.parseInt(res.getString(R.string.search_period_days));
        LocalDate endDate = null;
        LocalDate startDate = null;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(sensorManager).registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(days);
        }

        String path = res.getString(R.string.findings_file_url)+"/"+project+"/"+startDate.format(formatter)+"/"+endDate.format(formatter);
        httpsGetTask.execute(path);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bnvMain);
        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem) -> {
            switchFinding(menuItem.getItemId());
            return false;
        });
    }

    private void switchFinding(int itemId) {
        switch(itemId) {
            case R.id.action_PREV:
                getPrevFinding();
                break;
            case R.id.action_NEXT:
                getNextFinding();
                break;
            case R.id.action_Images:
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("selectedProject", project);
                startActivity(intent);
        }
    }

    private void getPrevFinding(){
        setCurrentFinding(currentFinding-1);
    }

    private void setCurrentFinding(int index) {
        if(index < 1){
            index = 1;
        }
        if(index > findings.size()){
            index = findings.size();
        }

        currentFinding = index;
        Finding currentFinding = findings.get(index);

        TextView altTextView = findViewById(R.id.FindingMessage);
        altTextView.setText(currentFinding.getMessage());

        TextView titleView = findViewById(R.id.FindingDescription);
        titleView.setText("In file " + currentFinding.getFile() + "(Line "+currentFinding.getLine()+"), saved on " + currentFinding.getDate());

    }

    private void getNextFinding(){
        setCurrentFinding(currentFinding+1);
    }

    private void getRandomComic() {
        Random r = new Random();
        int result = r.nextInt(size) + 1;
        setCurrentFinding(result);
    }

    @Override
    public void onResult(boolean result) {

    }

    @Override
    public void onDownloadStart() {

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = currentAcceleration - lastAcceleration;
            acceleration = acceleration * 0.9f + delta;
            if (acceleration > 12) {
                getRandomComic();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestResult(List<String> findingsStr) {
        try {
            JSONArray jsonArray = new JSONArray(findingsStr.get(0));

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String project = jsonObject.getString("project");
                String file = jsonObject.getString("file");
                String source = jsonObject.getString("source");
                String message = jsonObject.getString("message");
                String severity = jsonObject.getString("severity");
                String line = jsonObject.getString("line");
                String dateStr = jsonObject.getString("date");
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                Finding finding = new Finding();
                finding.setDate(date);
                if(file.contains(".java")) {
                    int idx = file.lastIndexOf("/");
                    String filename = idx >= 0 ? file.substring(idx + 1) : file;
                    finding.setFile(filename);
                } else {
                    finding.setFile(file);
                }

                finding.setLine(line);
                finding.setMessage(message);
                finding.setProject(project);
                finding.setSeverity(severity);
                finding.setId(id);
                finding.setSource(source);
                findings.add(finding);
            }

            Log.i("Size: " , findings.size()+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Size: " , findings.size()+"");
        size = findings.size();
        showText = true;
        setCurrentFinding(0);
    }
}
