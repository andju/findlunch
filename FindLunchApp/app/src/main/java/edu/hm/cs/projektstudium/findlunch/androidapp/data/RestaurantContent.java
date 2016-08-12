package edu.hm.cs.projektstudium.findlunch.androidapp.data;

import android.content.Context;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.RestaurantType;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.TimeSchedule;

/**
 * The type RestaurantContent
 * provides the information
 * of the type Restaurant in a formatted form.
 */
public class RestaurantContent {

    /**
     * The list of restaurants.
     */
    private List<Restaurant> items;

    /**
     * The Context of the activity.
     */
    private Context context;

    /**
     * The restaurant content.
     */
    private static RestaurantContent instance;

    /**
     * The restaurant offer filter
     */
    private static RestaurantOfferFilter filterInstance;

    /**
     * Gets instance.
     *
     * @param context the context of the activity
     * @return the instance
     */
    public static RestaurantContent getInstance(Context context) {
        if(instance == null) {
            setInstance(new RestaurantContent(context));
        }
        return instance;
    }

    /**
     * Instantiates a new Restaurant content.
     *
     * @param context the context of the activity
     */
    private RestaurantContent(Context context) {
        this.setContext(context);
    }

    /**
     * Sets instance.
     *
     * @param instance the instance
     */
    private static void setInstance(RestaurantContent instance) {
        RestaurantContent.instance = instance;
    }

    /**
     * Gets filter instance.
     *
     * @return the filter instance
     */
    private static RestaurantOfferFilter getFilterInstance() {
        return filterInstance;
    }

    /**
     * Sets filter instance.
     *
     * @param filterInstance the filter instance
     */
    private static void setFilterInstance(RestaurantOfferFilter filterInstance) {
        RestaurantContent.filterInstance = filterInstance;
    }

    /**
     * Gets name of a restaurant.
     *
     * @param position the position
     * @return the name
     */
    public String getName(int position) {
        return getItems().get(position).getName();
    }

    /**
     * Gets opening times of a restaurant.
     *
     * @param position the position
     * @return the opening times
     */
    public String getOpeningTimes(int position) {
        // create a string builder for the result
        StringBuilder result = new StringBuilder();
        // get the restaurant
        Restaurant restaurant = getItems().get(position);

        if (restaurant != null) {
            // get the time schedules of the restaurant
            List<TimeSchedule> openingTimes = restaurant.getTimeSchedules();
            // the current day
            TimeSchedule currentDay;
            // Do not change locale! Calendar with locale germany is needed to get static day of week numbers.
            Calendar calendar = Calendar.getInstance(Locale.GERMANY);
            // get the number of the day of week
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // get the locale from the context
            Locale userLocale = getContext().getResources().getConfiguration().locale;
            // create a new date format
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", userLocale);
            // create new date format symbols
            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(userLocale);
            // get an array of localized weekdays from the date format symbols
            String weekdays[] = dateFormatSymbols.getWeekdays();
            // indicates if the offer times of the weekday were found
            boolean offerTimeOnWeekdayFound = false;

            result.append(getContext().getResources().getString(R.string.text_offer_time));
            result.append(" ");

            for(int i = 0; i < openingTimes.size() && !offerTimeOnWeekdayFound; i++) {
                if(openingTimes.get(i) != null){
                    currentDay = openingTimes.get(i);

                    if(currentDay.getDayOfWeek() != null
                            && currentDay.getDayOfWeek().getDayNumber() == dayOfWeek
                            && currentDay.getOfferStartTime() != null
                            && currentDay.getOfferEndTime() != null) {
                        offerTimeOnWeekdayFound = true;
                        result.append(dateFormat.format(currentDay.getOfferStartTime()));
                        result.append(" - ");
                        result.append(dateFormat.format(currentDay.getOfferEndTime()));
                        result.append(" ");
                        result.append(getContext().getResources().getString(R.string.text_hour));
                    }
                }
            }
            if (!offerTimeOnWeekdayFound) {
                result.append(weekdays[dayOfWeek]);
                result.append(" ");
                result.append(getContext().getResources().getString(R.string.text_no_offertime));
            }
        }
        return  result.toString();
    }

    /**
     * Gets restaurant types of a restaurant.
     *
     * @param position the position
     * @return the restaurant types
     */
    public String getRestaurantTypes(int position) {
        // create a string builder for the result
        StringBuilder result = new StringBuilder();
        // get the restaurants
        Restaurant restaurant = getItems().get(position);
        RestaurantType restaurantType;

        if (restaurant != null) {
            // get the restaurant type
            restaurantType = restaurant.getRestaurantType();

            if(restaurantType != null) {
                result.append(getContext().getResources().getString(R.string.text_restaurant_type));
                result.append(" ");

                result.append(restaurantType.getName());
            }
        }
        return result.toString();
    }

    /**
     * Gets kitchen types of a restaurant.
     *
     * @param position the position
     * @return the kitchen types
     */
    public String getKitchenTypes(int position) {
        // create a string builder for the result
        StringBuilder result = new StringBuilder();
        // get the restaurant
        Restaurant restaurant = getItems().get(position);
        List<KitchenType> kitchenTypes;

        if (restaurant != null) {
            // get the kitchen types
            kitchenTypes = restaurant.getKitchenTypes();

            if(kitchenTypes != null && kitchenTypes.size() > 0) {
                result.append(getContext().getResources().getString(R.string.text_kitchen_type));
                result.append(" ");

                for(int i = 0; i < kitchenTypes.size(); i++) {
                    if(kitchenTypes.get(i) != null) {
                        if(i > 0) {
                            result.append(", ");
                        }
                        result.append(kitchenTypes.get(i).getName());
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * Gets distance of a restaurant.
     *
     * @param position the position
     * @return the distance
     */
    public String getDistance(int position) {
        // create a string builder for the result
        StringBuilder result = new StringBuilder();
        result.append(getContext().getResources().getString(R.string.text_distance));
        result.append(" ");
        if(getItems().get(position) != null) {
            result.append(getItems().get(position).getDistance());
            result.append(" ");
            result.append(getContext().getResources().getString(R.string.text_meters));
        }
        return result.toString();
    }

    /**
     * Gets the information if the
     * restaurant is a Favorite.
     * @param position the position
     * @return <code>true</code> if the restaurant
     * is a Favorite.
     */
    public boolean isFavorit(int position) {
        return (getItems().get(position) != null &&
                getItems().get(position).isFavorite());
    }

    /**
     * Gets email of a restaurant.
     *
     * @param position the position
     * @return the email
     */
    public String getEmail(int position) {
        if (getItems().size() >= position &&
                getItems().get(position) != null) {
            return getItems().get(position).getEmail();
        }
        return null;
    }

    /**
     * Gets phone of a restaurant.
     *
     * @param position the position
     * @return the phone
     */
    public String getPhone(int position) {
        if (getItems().size() >= position &&
                getItems().get(position) != null) {
            return getItems().get(position).getPhone();
        }
        return null;
    }

    /**
     * Gets an array of kitchen types.
     *
     * @return the array of kitchen types
     */
    public String[] getKitchenTypes() {
        // create a list for the result
        ArrayList<String> resultList = new ArrayList<>();
        // the result
        String[] result;

        for(Restaurant a: getItemsUnfiltered()) {
            for(KitchenType b: a.getKitchenTypes()) {
                if(!resultList.contains(b.getName())) {
                    resultList.add(b.getName());
                }
            }
        }
        Collections.sort(resultList);
        result = new String[resultList.size()];
        result = resultList.toArray(result);

        return result;
    }

    /**
     * Size of the list of restaurants.
     *
     * @return the int
     */
    public int size() {
        return getItems().size();
    }

    /**
     * Gets longitude of a restaurant.
     *
     * @param position the position
     * @return the longitude
     */
    public double getLongitude(int position) {
        return getItems().get(position).getLocationLongitude();
    }

    /**
     * Gets latitude of a restaurant.
     *
     * @param position the position
     * @return the latitude
     */
    public double getLatitude(int position) {
        return getItems().get(position).getLocationLatitude();
    }

    /**
     * Gets a list of restaurants.
     *
     * @return the items
     */
    public List<Restaurant> getItems() {
        return getFilter().filterItems(items);
    }

    /**
     * Gets a unfiltered list of restaurants.
     *
     * @return the items unfiltered
     */
    public List<Restaurant> getItemsUnfiltered() {
        return items;
    }

    /**
     * Sets a list of restaurants.
     *
     * @param items the items
     */
    public void setItems(List<Restaurant> items) {
        this.items = items;
    }

    /**
     * Gets offer content.
     *
     * @param restaurantId the id of the restaurant
     * in the list of restaurants.
     * @return the offer content
     */
    public OfferContent getOfferContent(int restaurantId) {
        return new OfferContent(this, restaurantId, getContext());
    }

    /**
     * Gets filter.
     *
     * @return the filter
     */
    public RestaurantOfferFilter getFilter() {
        if(getFilterInstance() == null) {
            setFilterInstance(new RestaurantOfferFilter(this));
        }
        return getFilterInstance();
    }

    /**
     * Gets the position of the restaurant
     * in the unfiltered list of restaurants.
     * The method returns -1 if the unfiltered
     * list of restaurants doesn't contain a
     * restaurant with the given id.
     *
     * @param restaurantId the restaurant id
     * @return the position of the restaurant
     * in the unfiltered list of restaurants.
     */
    public int getRestaurantUnfilteredPosition(int restaurantId) {
        for(int i = 0; i < getItemsUnfiltered().size(); i++) {
            if(getItemsUnfiltered().get(i).getId() == restaurantId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets position of the restaurant
     * in the list of restaurants.
     *
     * @param restaurantId the restaurant id
     * @return the position of the restaurant
     * in the list of restaurants.
     * The method returns -1 if the
     * list of restaurants doesn't contain a
     * restaurant with the given id.
     */
    public int getRestaurantPosition(int restaurantId) {
        for(int i = 0; i < getItems().size(); i++) {
            if(getItems().get(i).getId() == restaurantId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the Context of the activity.
     *
     * @return the Context of the activity
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets the Context of the activity.
     *
     * @param context the Context of the activity
     */
    private void setContext(Context context) {
        this.context = context;
    }
}
