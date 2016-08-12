package edu.hm.cs.projektstudium.findlunch.androidapp.data;


/**
 * An activity implementing this interface
 * provides information about the user and the restaurants.
 */
public interface RestaurantAndLocationProvider {
    /**
     * Gets restaurant content.
     *
     * @return the restaurant content
     */
    RestaurantContent getRestaurantContent();

    /**
     * Gets user content.
     *
     * @return the user content.
     */
    UserContent getUserContent();
}
