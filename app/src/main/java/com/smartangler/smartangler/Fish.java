package com.smartangler.smartangler;

import com.smartangler.smartangler.FishingLocation.FishingLocation;

import java.util.ArrayList;
import java.util.Calendar;
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
    private List<FishingLocation> fishingLocations;

    public Fish(String name) {
        this.name = name;
        this.description = new String();
        this.techniques = new ArrayList<>();
        this.baitsAndLures = new ArrayList<>();
        this.seasons = new ArrayList<>();
        this.timesOfDay = new ArrayList<>();
        this.fishingLocations = new ArrayList<>();
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

    public List<FishingLocation> getFishingLocations() {
        return fishingLocations;
    }
    public void addFishingLocation(FishingLocation fishingLocation) {
        fishingLocations.add(fishingLocation);
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
                ", fishingLocations=" + fishingLocations +
                '}';
    }

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER
    }

    public static Season getCurrentSeason() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);

        switch (month) {
            case Calendar.DECEMBER:
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
                return Season.WINTER;
            case Calendar.MARCH:
            case Calendar.APRIL:
            case Calendar.MAY:
                return Season.SPRING;
            case Calendar.JUNE:
            case Calendar.JULY:
            case Calendar.AUGUST:
                return Season.SUMMER;
            case Calendar.SEPTEMBER:
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
                return Season.AUTUMN;
            default:
                throw new IllegalStateException("Unexpected month: " + month);
        }
    }

    public enum TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }

    public static TimeOfDay getCurrentTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return TimeOfDay.MORNING;
        } else if (hour >= 12 && hour < 17) {
            return TimeOfDay.AFTERNOON;
        } else if (hour >= 17 && hour < 21) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }
    }
}
