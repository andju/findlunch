package edu.hm.cs.projektstudium.findlunch.androidapp.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.RestaurantItemFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.RestaurantItemFragment.OnRestaurantListFragmentInteractionListener;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.ViewHelper;

/**
 * {@link RecyclerView.Adapter} that can display a {@link edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant} and makes a call to the
 * specified {@link OnRestaurantListFragmentInteractionListener}.
 */
public class RestaurantItemRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantItemRecyclerViewAdapter.ViewHolder> {

    /**
     * The restaurants to display.
     */
    private final RestaurantContent mValues;
    /**
     * The listener whose implementation of {@link OnRestaurantListFragmentInteractionListener}
     * gets called on interaction.
     */
    private final RestaurantItemFragment.OnRestaurantListFragmentInteractionListener mListener;

    /**
     * Instantiates a new Restaurant item recycler view adapter.
     *
     * @param items    the items
     * @param listener the listenerxt
     */
    public RestaurantItemRecyclerViewAdapter(RestaurantContent items, OnRestaurantListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mNameView.setText(mValues.getName(position));
        holder.mOfferTimesView.setText(mValues.getOpeningTimes(position));
        ViewHelper.makeVisibleOnUpdate(holder.mRestaurantType, mValues.getRestaurantTypes(position));
        ViewHelper.makeVisibleOnUpdate(holder.mKitchenType, mValues.getKitchenTypes(position));
        holder.mDistanceView.setText(mValues.getDistance(position));

        Drawable drawable;
        if (mValues.isFavorit(position)) {
            drawable = ContextCompat.getDrawable(mValues.getContext(), R.drawable.ic_star_black);
        } else {
            drawable = ContextCompat.getDrawable(mValues.getContext(), R.drawable.ic_star_border_black);
        }
        holder.mFavouriteView.setImageDrawable(drawable);
        if (!mListener.getUserLoginCredentials().isLoggedIn()) {
            holder.mFavouriteView.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onRestaurantListFragmentInteraction(holder);
            }
        });
        holder.mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that the favourite icon of an
                // item has been selected.
                mListener.onRestaurantListFragmentFavouriteInteraction(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The view.
         */
        public final View mView;
        /**
         * The name view.
         */
        public final TextView mNameView;
        /**
         * The offer times view.
         */
        public final TextView mOfferTimesView;
        /**
         * The restaurant type.
         */
        public final TextView mRestaurantType;
        /**
         * The kitchen type.
         */
        public final TextView mKitchenType;
        /**
         * The distance view.
         */
        public final TextView mDistanceView;
        /**
         * The favourite view
         */
        public final ImageView mFavouriteView;

        /**
         * Instantiates a new View holder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.restaurantName);
            mOfferTimesView = (TextView) view.findViewById(R.id.restaurantOfferTime);
            mRestaurantType = (TextView) view.findViewById(R.id.restaurantType);
            mKitchenType = (TextView) view.findViewById(R.id.kitchenType);
            mDistanceView = (TextView) view.findViewById(R.id.distance);
            mFavouriteView = (ImageView) view.findViewById(R.id.iconFavourite);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + " '" + mOfferTimesView.getText() +" '" + mRestaurantType.getText() +" '" + mKitchenType.getText() + " '" + mDistanceView.getText() + "'";
        }
    }
}
