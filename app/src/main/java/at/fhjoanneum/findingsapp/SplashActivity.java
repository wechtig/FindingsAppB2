package at.fhjoanneum.findingsapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.fhjoanneum.findingsapp.BL.HttpsGetTask;
import at.fhjoanneum.findingsapp.BL.RequestCallback;

public class SplashActivity extends AppCompatActivity implements RequestCallback<String> {

    private long requestStarted = 0;
    //minimal time the splashscreen is shown in milliseconds
    private final long MIN_TIME = 2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HttpsGetTask httpsGetTask = new HttpsGetTask(this);
        Resources res = getResources();
        httpsGetTask.execute(res.getString(R.string.projects_url));
    }

    @Override
    public void onRequestStart() {
        requestStarted = System.currentTimeMillis();
    }

    @Override
    public void onRequestResult(List<String> res) {
        switchToMainActivity(new ArrayList<>(res));
    }
    private void switchToMainActivity(ArrayList<String> res){
        String projectsString = res.get(0);
        projectsString = projectsString.replace("\"", "");
        projectsString = projectsString.replace("[", "");
        projectsString = projectsString.replace("]", "");
        String[] projectsArray = projectsString.split(",");
        Intent intent = new Intent(SplashActivity.this, ProjectActivity.class);
        ArrayList<String> projects = new ArrayList<>(Arrays.asList(projectsArray));
        intent.putStringArrayListExtra("Projects", projects);
        startActivity(intent);
    }
}

