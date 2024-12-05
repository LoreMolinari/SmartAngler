package com.smartangler.smartangler.FishingLocation;

import java.util.List;

public class FishingLocation {
    private String name;
    private List<Vertex> vertices;
    public FishingLocation(String name) {
        this.name = name;
    }

    public boolean isPointInsideLocation(Vertex p) {
        int intersections = 0;
        int n = vertices.size();
        double x = p.getLongitude();
        double y = p.getLatitude();

        for (int i = 0; i < n; i++) {
            Vertex vertex1 = vertices.get(i);
            Vertex vertex2 = vertices.get((i + 1) % n);

            // Check if the point is between the y-coordinates of the edge
            if ((vertex1.getLatitude() > y && vertex2.getLatitude() <= y) || (vertex2.getLatitude() > y && vertex1.getLatitude()<= y)) {
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
