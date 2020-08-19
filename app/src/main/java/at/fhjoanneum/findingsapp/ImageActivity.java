package at.fhjoanneum.findingsapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import at.fhjoanneum.findingsapp.BL.DownloadCallback;
import at.fhjoanneum.findingsapp.BL.HttpsGetTask;
import at.fhjoanneum.findingsapp.BL.RequestCallback;
import at.fhjoanneum.findingsapp.BL.ZoomableImageView;
import at.fhjoanneum.findingsapp.Data.Finding;
import at.fhjoanneum.findingsapp.Data.ImageContainer;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ImageActivity extends AppCompatActivity implements DownloadCallback, RequestCallback<String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private List<ImageContainer> findingsImages = new ArrayList<>();
    private int currentFinding = 0;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean showText;
    private int size;
    private Spinner spinner;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        String project = getIntent().getExtras().getString("selectedProject");

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

        String path = res.getString(R.string.findings_images_file_url)+"/"+project+"/"+startDate.format(formatter)+"/"+endDate.format(formatter);
        httpsGetTask.execute(path);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bnvMain);
        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem) -> {
            switchImage(menuItem.getItemId());
            return false;
        });
    }

    private void switchImage(int itemId) {
        switch(itemId) {
            case R.id.action_PREV:
                getPrevFindingImage();
                break;
            case R.id.action_NEXT:
                getNextFindingImage();
                break;
            case R.id.action_Images:
            case R.id.action_Text:
                String project = spinner.getSelectedItem().toString();
                Intent intent = new Intent(ImageActivity.this, MainActivity.class);
                intent.putExtra("selectedProject", project);
                startActivity(intent);
        }
    }

    private void getPrevFindingImage() {
        setCurrentImage(currentFinding-1);
    }

    private void getNextFindingImage() {
        setCurrentImage(currentFinding+1);
    }

    @Override
    public void onResult(boolean result) {

    }

    @Override
    public void onDownloadStart() {

    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestResult(List<String> findingsStr) {
        if(findingsStr == null || findingsStr.size() < 0) {
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(findingsStr.get(0));

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String image = jsonObject.getString("image");
                String description = jsonObject.getString("description");

                ImageContainer imageContainer = new ImageContainer();
                imageContainer.setDescription(description);
                Log.i("Image: " ,image+"");

                byte[] encodeByte = Base64.decode(image,Base64.DEFAULT);
                Bitmap imageB = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                imageContainer.setImage(imageB);
                findingsImages.add(imageContainer);
            }


            Log.i("Size: " , findingsImages.size()+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        size = findingsImages.size();
        showText = false;
        setCurrentImage(0);
    }

    private void setCurrentImage(int index) {
        if(index < 0){
            index = 0;
        }
        if(index >= findingsImages.size()){
            index = findingsImages.size()-1;
        }

        currentFinding = index;
        ImageContainer imageContainer = findingsImages.get(index);

        Bitmap comic = imageContainer.getImage();
        ZoomableImageView iv = findViewById(R.id.ImageView);
        iv.setImageBitmap(comic);

        TextView altTextView = findViewById(R.id.FindingImageDescription);
        altTextView.setText(imageContainer.getDescription());
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
                getRandomImage();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void getRandomImage() {
        Random r = new Random();
        int result = r.nextInt(size) + 1;
        setCurrentImage(result);
    }
}
