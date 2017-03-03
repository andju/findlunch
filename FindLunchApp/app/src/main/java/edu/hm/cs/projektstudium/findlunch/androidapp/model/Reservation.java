package edu.hm.cs.projektstudium.findlunch.androidapp.model;

/**
 * The type Reservation.
 */
public class Reservation {

    /**
     * The Id.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int id;

    /**
     * The amount.
     */
    private int amount;

    /**
     * The offer.
     */
    private Offer offer;

    /**
     * The donation.
     */
    private float donation;

    /**
     * The total price.
     */
    private float totalPrice;

    /**
     * The confirm status.
     */
    private boolean confirmed;

    /**
     * The used points.
     */
    private boolean usedPoints;

    /**
     * Default Constructor for reservation.
     */
    public Reservation(){}

    /**
     * Gets the amount.
     * @return The amount
     */
    @SuppressWarnings("unused")
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     * @param amount The amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets the offer.
     * @return The offer
     */
    public Offer getOffer() {
        return offer;
    }

    /**
     * Sets the offer.
     * @param offer The offer
     */
    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    /**
     * Gets the donation.
     * @return The donation
     */
    @SuppressWarnings("unused")
    public float getDonation() {
        return donation;
    }

    /**
     * Sets the donation.
     * @param donation The donation
     */
    public void setDonation(float donation) {
        this.donation = donation;
    }

    /**
     * Gets the total price.
     * @return The total price
     */
    @SuppressWarnings("unused")
    public float getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price.
     * @param totalPrice The total price
     */
    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Checks, if a reservation confirmed.
     * @return true, if it is confirmed
     */
    @SuppressWarnings("unused")
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Sets the confirm status.
     * @param confirmed true, if it is confirmed
     */
    @SuppressWarnings("unused")
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Checks, if points are used for this reservation.
     * @return True, if points are used
     */
    @SuppressWarnings("unused")
    public boolean isUsedPoints() {
        return usedPoints;
    }

    /**
     * Sets the used points.
     * @param usedPoints The used points
     */
    public void setUsedPoints(boolean usedPoints) {
        this.usedPoints = usedPoints;
    }
}
