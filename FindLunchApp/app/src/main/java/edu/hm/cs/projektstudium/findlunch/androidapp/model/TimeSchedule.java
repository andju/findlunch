package edu.hm.cs.projektstudium.findlunch.androidapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * The type Time schedule.
 */
public class TimeSchedule {
    /**
     * The Id.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private int id;
    /**
     * The Offer end time.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de", timezone = "Europe/Berlin")
    private Date offerEndTime;
    /**
     * The Offer start time.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de", timezone = "Europe/Berlin")
    private Date offerStartTime;
    /**
     * The Opening times.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private List<OpeningTime> openingTimes;
    /**
     * The Day of week.
     */
    private DayOfWeek dayOfWeek;

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
     * Gets offer end time.
     *
     * @return the offer end time
     */
    public Date getOfferEndTime() {
        return offerEndTime;
    }

    /**
     * Sets offer end time.
     *
     * @param offerEndTime the offer end time
     */
    @SuppressWarnings("unused")
    public void setOfferEndTime(Date offerEndTime) {
        this.offerEndTime = offerEndTime;
    }

    /**
     * Gets offer start time.
     *
     * @return the offer start time
     */
    public Date getOfferStartTime() {
        return offerStartTime;
    }

    /**
     * Sets offer start time.
     *
     * @param offerStartTime the offer start time
     */
    @SuppressWarnings("unused")
    public void setOfferStartTime(Date offerStartTime) {
        this.offerStartTime = offerStartTime;
    }

    /**
     * Sets opening times.
     *
     * @param openingTimes the opening times
     */
    @SuppressWarnings("unused")
    public void setOpeningTimes(List<OpeningTime> openingTimes) {
        this.openingTimes = openingTimes;
    }

    /**
     * Gets day of week.
     *
     * @return the day of week
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets day of week.
     *
     * @param dayOfWeek the day of week
     */
    @SuppressWarnings("unused")
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
