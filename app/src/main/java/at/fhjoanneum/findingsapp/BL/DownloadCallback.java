package at.fhjoanneum.findingsapp.BL;

public interface DownloadCallback {
    void onResult(boolean result);
    void onDownloadStart();
}
