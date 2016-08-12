package edu.hm.cs.projektstudium.findlunch.androidapp.model;

/**
 * The type Day of week.
 */
public class DayOfWeek {
    /**
     * The Id.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int id;
    /**
     * The Day number.
     */
    private int dayNumber;
    /**
     * The Name.
     */
    private String name;

    /**
     * Sets id.
     *
     * @param id the id
     */
    @SuppressWarnings("unused")
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets day number.
     *
     * @return the day number
     */
    public int getDayNumber() {
        return dayNumber;
    }

    /**
     * Sets day number.
     *
     * @param dayNumber the day number
     */
    @SuppressWarnings("unused")
    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}
