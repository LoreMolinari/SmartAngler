package com.smartangler.smartangler.FishingLocation;

import androidx.annotation.NonNull;

public class Vertex {
    private double latitude;
    private double longitude;

    public Vertex(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%f %f", this.latitude, this.longitude);
    }
}
