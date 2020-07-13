package at.fhjoanneum.findingsapp.BL;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class HttpsGetTask extends AsyncTask<String, Void, String> {
    private RequestCallback callback;

    public HttpsGetTask(RequestCallback callback) {
        this.callback = callback;
    }

    //executed on UI thread
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onRequestStart();
        }
    }

    //executed on background thread
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];

        HttpsClient httpsClient = new HttpsClient();
        String result = null;
        try {
            result = httpsClient.get(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //executed on UI thread
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (callback != null) {
            callback.onRequestResult(Arrays.asList(result));
        }
    }
}
