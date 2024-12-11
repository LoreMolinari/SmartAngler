package com.smartangler.smartangler.FishingLocation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FishingLocation {
    private String name;
    private final List<Vertex> vertices;

    public FishingLocation(String name) {
        if (name != null) {
            this.name = name;
        }
        vertices = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public boolean isPointInsideLocation(Vertex p) {
        int intersections = 0;
        int n = vertices.size();
        double x = p.getLongitude();
        double y = p.getLatitude();
        Log.d("Fishing Location", String.format("Checking point %s", p));

        for (int i = 0; i < n; i++) {
            Vertex vertex1 = vertices.get(i);
            Vertex vertex2 = vertices.get((i + 1) % n);
            Log.d("Fishing Location", String.format("Checking vertices %s and %s", vertex1, vertex2));

            // Check if the point is between the y-coordinates of the edge
            if ((vertex1.getLatitude() > y && vertex2.getLatitude() <= y) || (vertex2.getLatitude() > y && vertex1.getLatitude() <= y)) {
                // Calculate the x-coordinate of the intersection with the ray
                double slope = (vertex2.getLongitude() - vertex1.getLongitude()) / (vertex2.getLatitude() - vertex1.getLatitude());
                double interceptX = vertex1.getLongitude() + slope * (y - vertex1.getLatitude());

                // Check if the point is to the right of the intersection point
                if (x >= interceptX) {
                    intersections++;
                }
            }
        }

        // If there are an odd number of intersections, the point is inside the polygon
        return intersections % 2 != 0;
    }
}
