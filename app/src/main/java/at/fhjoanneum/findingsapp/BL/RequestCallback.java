package at.fhjoanneum.findingsapp.BL;

import java.util.List;

public interface RequestCallback<T> {
    void onRequestStart();
    void onRequestResult(List<T> projects);
}
