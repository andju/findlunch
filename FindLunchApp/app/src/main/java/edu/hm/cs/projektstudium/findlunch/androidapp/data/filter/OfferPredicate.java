package edu.hm.cs.projektstudium.findlunch.androidapp.data.filter;

import java.math.BigDecimal;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantOfferFilter;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;


/**
 * The type OfferPredicate
 * that contains a predicate for
 * the filtering of offers.
 */
public class OfferPredicate implements Predicate<Offer> {

    /**
     * The Restaurant offer filter.
     */
    private final RestaurantOfferFilter restaurantOfferFilter;

    /**
     * Instantiates a new Offer predicate.
     *
     * @param restaurantOfferFilter the restaurant offer filter
     */
    public OfferPredicate(RestaurantOfferFilter restaurantOfferFilter) {
        this.restaurantOfferFilter = restaurantOfferFilter;
    }

    @Override
    public boolean apply(Offer type) {
        // true if there was no maximum price set
        if(restaurantOfferFilter.getMaxPriceBigDecimal().compareTo(new BigDecimal(0)) == 0) {
            return true;
        } else if (type.getPrice().compareTo(restaurantOfferFilter.getMaxPriceBigDecimal()) <= 0) {
            return true;
        }
        return false;
    }
}
