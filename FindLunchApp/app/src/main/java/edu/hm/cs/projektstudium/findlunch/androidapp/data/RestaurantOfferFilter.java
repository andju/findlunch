package edu.hm.cs.projektstudium.findlunch.androidapp.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.data.filter.Filter;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.filter.OfferPredicate;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.filter.Predicate;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.filter.RestaurantPredicate;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;


/**
 * The type RestaurantOfferFilter
 * which represents the filter information
 * that were provided by the user.
 */
public class RestaurantOfferFilter {

    /**
     * The Restaurant content.
     */
    private final RestaurantContent restaurantContent;
    /**
     * The Kitchen types
     * that were selected.
     */
    private ArrayList<KitchenType> kitchenTypesSelected;
    /**
     * The id numbers of the Kitchen types
     * from a received push notification.
     */
    private int[] kitchenTypesPush;
    /**
     * The Max price.
     */
    private int maxPrice;
    /**
     * The information whether
     * to show only favourites.
     */
    private boolean onlyFavourites;
    /**
     * The Restaurant predicate.
     */
    private final Predicate<Restaurant> restaurantPredicate;
    /**
     * The Offer predicate.
     */
    private final Predicate<Offer> offerPredicate;

    /**
     * Instantiates a new Restaurant offer filter.
     *
     * @param restaurantContent the restaurant content
     */
    public RestaurantOfferFilter(RestaurantContent restaurantContent) {
        this.restaurantContent = restaurantContent;
        this.restaurantPredicate = new RestaurantPredicate(this);
        this.offerPredicate = new OfferPredicate(this);
    }

    /**
     * Returns <code>true</code> if
     * <code>maxPrice</code> is a valid
     * maximum price.
     *
     * @param maxPrice the max price
     * @return <code>true</code> if <code>maxPrice</code> is a valid maximum price.
     */
    public boolean validMaxPrice(int maxPrice) {
        return maxPrice > 0;
    }

    /**
     * Gets max price.
     *
     * @return the max price
     */
    public int getMaxPrice() {
        return maxPrice;
    }

    /**
     * Gets max price as big decimal.
     *
     * @return the max price as big decimal
     */
    public BigDecimal getMaxPriceBigDecimal() {
        return new BigDecimal(maxPrice);
    }

    /**
     * Sets max price.
     *
     * @param maxPrice the max price
     */
    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    /**
     * Gets the information whether
     * to show only favourites.
     *
     * @return the boolean
     */
    public boolean isOnlyFavourites() {
        return onlyFavourites;
    }

    /**
     * Sets the information whether
     * to show only favourites.
     *
     * @param onlyFavourites the only favourites
     */
    public void setOnlyFavourites(boolean onlyFavourites) {
        this.onlyFavourites = onlyFavourites;
    }

    /**
     * Sets kitchen types push.
     *
     * @param kitchenTypesPush the kitchen types push
     */
    public void setKitchenTypesPush(int[] kitchenTypesPush) {
        this.kitchenTypesPush = kitchenTypesPush;
    }

    /**
     * Gets kitchen types selected as string.
     *
     * @return the kitchen types selected as string.
     */
    public ArrayList<String> getKitchenTypeNamesSelected() {
        checkPushedKitchenTypes();
        ArrayList<String> result = new ArrayList<>();
        if (kitchenTypesSelected != null) {
            for(KitchenType a: kitchenTypesSelected) {
                result.add(a.getName());
            }
        }
        return result;
    }

    /**
     * Gets kitchen types selected.
     *
     * @return the kitchen types selected
     */
    public ArrayList<KitchenType> getKitchenTypesSelected() {
        checkPushedKitchenTypes();
        return kitchenTypesSelected;
    }

    /**
     * Sets kitchen types selected.
     *
     * @param selected the selected
     */
    public void setKitchenTypesSelected(boolean[] selected) {
        // get the kitchen types
        ArrayList<KitchenType> kitchenTypes = getKitchenTypes();
        // create a list for the result
        ArrayList<KitchenType> result = new ArrayList<>();

        if(selected != null &&
                kitchenTypes != null &&
                selected.length <= kitchenTypes.size()) {

            for(int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    result.add(kitchenTypes.get(i));
                }
            }
        }

        kitchenTypesSelected = result;
    }


    /**
     * Check pushed kitchen types.
     * Add pushed kitchen types - if available -
     * to the list of kitchen types selected.
     */
    private void checkPushedKitchenTypes() {
        // get the kitchen types
        ArrayList<KitchenType> kitchenTypes = getKitchenTypes();
        // create a list for the result
        ArrayList<KitchenType> result = new ArrayList<>();

        if(kitchenTypesPush != null) {

            for(int id: kitchenTypesPush) {
                for(KitchenType kitchenType: kitchenTypes) {
                    if(kitchenType.getId() == id) {
                        result.add(kitchenType);
                    }
                }
            }
            kitchenTypesSelected = result;
            // set push kitchen types null
            // as the were already added
            kitchenTypesPush = null;
        }
    }


    /**
     * Gets kitchen types.
     *
     * @return the kitchen types
     */
    private ArrayList<KitchenType> getKitchenTypes() {
        ArrayList<KitchenType> result = new ArrayList<>();

        for(Restaurant a: restaurantContent.getItemsUnfiltered()){
            for(KitchenType b: a.getKitchenTypes()) {
                if(!result.contains(b)) {
                    result.add(b);
                }
            }
        }
        Collections.sort(result);

        return result;
    }

    /**
     * Gets the kitchen type selection mask
     * in form of an array of booleans
     * that contains a <code>true</code> for each kitchen type
     * that was selected by the user before.
     *
     * @return the array of booleans
     * that contains a <code>true</code> for each kitchen type
     * that was selected by the user before.
     */
    public boolean[] getKitchenTypeSelectionMask() {
        // get the kitchen types
        ArrayList<KitchenType> kitchenTypes = getKitchenTypes();
        // variable representing the length of the kitchen types
        int length = 0;
        // array representing the selection mask of the kitchen types
        boolean[] selectionMask;

        if (kitchenTypes != null) {
            length  = kitchenTypes.size();
            selectionMask  = new boolean[length];

            if (kitchenTypesSelected != null) {
                for(int i = 0; i < kitchenTypes.size(); i++) {
                    selectionMask[i] = kitchenTypesSelected.contains(kitchenTypes.get(i));
                }
            }
        } else {
            selectionMask  = new boolean[length];
        }
        return selectionMask;
    }

    /**
     * Returns a filtered list of restaurants.
     *
     * @param items the list of restaurants to filter
     * @return the filtered list of restaurants
     */
    public List<Restaurant> filterItems(List<Restaurant> items) {
        return Filter.filter(items, restaurantPredicate);
    }

    /**
     * Returns a filtered list of offers.
     *
     * @param offers the list of offers to filter
     * @return the filtered list of offers
     */
    public List<Offer> filterOffers(List<Offer> offers) {
        return Filter.filter(offers, offerPredicate);
    }

}
