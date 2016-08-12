package edu.hm.cs.projektstudium.findlunch.androidapp.model;

import java.math.BigDecimal;

/**
 * The type Offer.
 */
public class Offer {
    /**
     * The Id.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int id;
    /**
     * The Description.
     */
    private String description;
    /**
     * The Preparation time.
     */
    private int preparationTime;
    /**
     * The Price.
     */
    private BigDecimal price;
    /**
     * The Title.
     */
    private String title;
    /**
     * The Default photo.
     */
    private OfferPhoto defaultPhoto;

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
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    @SuppressWarnings("unused")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets preparation time.
     *
     * @return the preparation time
     */
    public int getPreparationTime() {
        return preparationTime;
    }

    /**
     * Sets preparation time.
     *
     * @param preparationTime the preparation time
     */
    @SuppressWarnings("unused")
    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    @SuppressWarnings("unused")
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets default photo.
     *
     * @return the default photo
     */
    public OfferPhoto getDefaultPhoto() {
        return defaultPhoto;
    }

    /**
     * Sets default photo.
     *
     * @param defaultPhoto the default photo
     */
    @SuppressWarnings("unused")
    public void setDefaultPhoto(OfferPhoto defaultPhoto) {
        this.defaultPhoto = defaultPhoto;
    }
}
