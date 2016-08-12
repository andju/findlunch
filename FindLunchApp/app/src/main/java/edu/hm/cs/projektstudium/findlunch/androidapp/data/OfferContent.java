package edu.hm.cs.projektstudium.findlunch.androidapp.data;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.Formatter;
import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.ImageHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;


/**
 * The type OfferContent
 * provides the information
 * of the type Offer in a formatted form.
 */
public class OfferContent {

    /**
     * The id of the restaurant
     * in the list of restaurants.
     */
    private final int restaurantId;
    /**
     * The Restaurant content
     * that contains the list of restaurants.
     */
    private final RestaurantContent restaurantContent;
    /**
     * The Context of the activity.
     */
    private final Context context;

    /**
     * Instantiates a new Offer content.
     *
     * @param restaurantContent the restaurant content
     * @param restaurantId      the restaurant id
     * @param context           the context
     */
    public OfferContent(RestaurantContent restaurantContent, int restaurantId, Context context) {
        this.restaurantId = restaurantId;
        this.restaurantContent = restaurantContent;
        this.context = context;
    }

    /**
     * Gets the list of offers.
     *
     * @return the offers
     */
    private List<Offer> getOffers() {
        return restaurantContent.getFilter()
                .filterOffers(restaurantContent.getItemsUnfiltered()
                        .get(restaurantContent.getRestaurantUnfilteredPosition(restaurantId)).getOffers());
    }

    /**
     * Size of the list of offers.
     *
     * @return the int
     */
    public int size() {
        List<Offer> offers = getOffers();
        return offers == null ? 0 : offers.size();
    }

    /**
     * Get title string of an offer.
     *
     * @param position the position of the offer within the list
     * @return the string
     */
    public String getTitle(int position){
        List<Offer> offers = getOffers();
        Offer offer;

        if (offers != null) {
            offer = offers.get(position);

            if (offer != null) {
                return offer.getTitle();
            }
        }
        return null;
    }

    /**
     * Gets description of an offer.
     *
     * @param position the position of the offer within the list
     * @return the description
     */
    public String getDescription(int position) {
        List<Offer> offers = getOffers();
        Offer offer;

        if (offers != null) {
            offer = offers.get(position);

            if (offer != null) {
                return offers.get(position).getDescription();
            }
        }
        return null;
    }

    /**
     * Gets price of an offer.
     *
     * @param position the position of the offer within the list
     * @return the price
     */
    public String getPrice(int position) {
        List<Offer> offers = getOffers();
        Offer offer;
        StringBuilder result = new StringBuilder();
        Formatter formatter = new Formatter(result);

        if (offers != null) {
            offer = offers.get(position);

            if (offer != null) {
                formatter.format("%10.2f", offer.getPrice());
                result.append(" ");
                result.append(context.getResources().getString(R.string.text_currency));
            }
        }

        return result.toString();
    }

    /**
     * Gets preparation time of an offer.
     *
     * @param position the position of the offer within the list
     * @return the preparation time
     */
    public String getPreparationTime(int position) {
        List<Offer> offers = getOffers();
        Offer offer;
        StringBuilder result = new StringBuilder();

        if (offers != null) {
            offer = offers.get(position);

            if (offer != null) {
                result.append(String.valueOf(offer.getPreparationTime()));
                result.append(" ");
                result.append(context.getResources().getString(R.string.text_abbreviation_minutes));
            }
        }

        return result.toString();
    }

    /**
     * Gets default photo of an offer.
     *
     * @param position the position of the offer within the list
     * @return the default photo
     */
    public Bitmap getDefaultPhoto(int position) {
        List<Offer> offers = getOffers();
        Offer offer;

        if (offers != null) {
            offer = offers.get(position);

            if (offer != null &&
                    offer.getDefaultPhoto() != null &&
                    offer.getDefaultPhoto().getThumbnail() != null) {
                return ImageHelper.decodeBase64(offer.getDefaultPhoto().getThumbnail());
            }
        }
        return null;
    }
}
