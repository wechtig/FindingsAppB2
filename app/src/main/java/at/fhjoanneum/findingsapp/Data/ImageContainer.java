package at.fhjoanneum.findingsapp.Data;

import android.graphics.Bitmap;

public class ImageContainer {
    private Bitmap image;
    private String description;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
