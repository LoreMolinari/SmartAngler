package com.smartangler.smartangler.ui.fishing;

import android.graphics.Bitmap;

public class FishEntry {
    private final Bitmap image;
    private final String name;
    private final String description;
    private final String date;

    public FishEntry(Bitmap image, String name, String description, String date) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}



