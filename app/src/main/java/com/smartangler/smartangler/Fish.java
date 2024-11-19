package com.smartangler.smartangler;

import java.util.ArrayList;
import java.util.List;

/**
 * To be populated when fetching from the fish DB.
 * End user only reads from instances of this class.
 */
public class Fish {
    private String name;
    private String description;
    private List<String> techniques;
    private List<String> baitsAndLures;
    private List<Season> seasons;
    private List<TimeOfDay> timesOfDay;

    public Fish(String name) {
        this.name = name;
        this.description = new String();
        this.techniques = new ArrayList<>();
        this.baitsAndLures = new ArrayList<>();
        this.seasons = new ArrayList<>();
        this.timesOfDay = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<String> getTechniques() {
        return techniques;
    }
    public void addTechnique(String technique) {
        techniques.add(technique);
    }
    public List<String> getBaitsAndLures() {
        return baitsAndLures;
    }
    public void addBaitOrLure(String baitOrLure) {
        baitsAndLures.add(baitOrLure);
    }
    public List<Season> getSeasons() {
        return seasons;
    }
    public void addSeason(Season season) {
        seasons.add(season);
    }
    public List<TimeOfDay> getTimesOfDay() {
        return timesOfDay;
    }
    public void addTimeOfDay(TimeOfDay timeOfDay) {
        timesOfDay.add(timeOfDay);
    }

    @Override
    public String toString() {
        return "Fish{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", techniques=" + techniques +
                ", baitsAndLures=" + baitsAndLures +
                ", seasons=" + seasons +
                ", timesOfDay=" + timesOfDay +
                '}';
    }

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER
    }

    public enum TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }
}
