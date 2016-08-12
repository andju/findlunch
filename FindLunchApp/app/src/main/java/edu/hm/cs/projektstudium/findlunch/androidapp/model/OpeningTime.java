package edu.hm.cs.projektstudium.findlunch.androidapp.model;

import java.util.Date;

/**
 * The type Opening time.
 */
@SuppressWarnings("unused")
public class OpeningTime {
    /**
     * The Id.
     */
    private int id;
    /**
     * The Closing time.
     */
    private Date closingTime;
    /**
     * The Opening time.
     */
    private Date openingTime;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets closing time.
     *
     * @return the closing time
     */
    public Date getClosingTime() {
        return closingTime;
    }

    /**
     * Sets closing time.
     *
     * @param closingTime the closing time
     */
    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    /**
     * Gets opening time.
     *
     * @return the opening time
     */
    public Date getOpeningTime() {
        return openingTime;
    }

    /**
     * Sets opening time.
     *
     * @param openingTime the opening time
     */
    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }
}
