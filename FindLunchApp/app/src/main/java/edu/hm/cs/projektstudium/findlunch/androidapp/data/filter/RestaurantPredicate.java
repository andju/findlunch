package edu.hm.cs.projektstudium.findlunch.androidapp.data.filter;

import java.util.ArrayList;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantOfferFilter;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;


/**
 * The type RestaurantPredicate
 * that contains a predicate for
 * the filtering of restaurants.
 */
public class RestaurantPredicate implements Predicate<Restaurant> {

    /**
     * The Restaurant offer filter.
     */
    private final RestaurantOfferFilter restaurantOfferFilter;

    /**
     * Instantiates a new Restaurant predicate.
     *
     * @param restaurantOfferFilter the restaurant offer filter
     */
    public RestaurantPredicate(RestaurantOfferFilter restaurantOfferFilter) {
        this.restaurantOfferFilter = restaurantOfferFilter;
    }

    @Override
    public boolean apply(Restaurant restaurant) {
        // get the list with the kitchen types selected
        ArrayList<KitchenType> selectedKitchenTypes =
                restaurantOfferFilter.getKitchenTypesSelected();

        if(restaurantOfferFilter.isOnlyFavourites() && !restaurant.isFavorite()) {
            return false;
        }

        if(selectedKitchenTypes == null || selectedKitchenTypes.size() == 0) {
            return true;
        } else {
            for(KitchenType a: restaurant.getKitchenTypes()) {
                if(a != null && selectedKitchenTypes.contains(a)) {
                    return true;
                }
            }
        }
        return false;
    }
}
