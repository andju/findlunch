package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.activity.MainActivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.adapter.OfferItemRecyclerViewAdapter;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;

/**
 * A fragment representing a list of Offers.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnOfferListFragmentInteractionListener}
 * interface.
 */
public class OfferItemFragment extends Fragment {

    /**
     * The constant ARG_COLUMN_COUNT.
     */
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     * The constant ARG_RESTAURANT_ID.
     */
    private static final String ARG_RESTAURANT_ID = "restaurant-id";

    /**
     * The number of columns.
     */
    private int mColumnCount = 1;
    /**
     * The id of the restaurant.
     */
    private int mRestaurantId;
    /**
     * The listener whose implementation of {@link OfferItemFragment.OnOfferListFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnOfferListFragmentInteractionListener mListener;

    /**
     * BroadcastReceiver that receives intents when
     * the REST-API for the restaurants was called successfully.
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshRecycleView();
            setOfferHeader();
        }
    };

    /**
     * Refresh the recycle view with the restaurant information.
     */
    private void refreshRecycleView() {
        // variable for the recycle view
        RecyclerView view;
        if (getView() != null) {
            // find the recycle view by id
            view = (RecyclerView) getView().findViewById(R.id.offerList);

            if (view != null) {
                view.getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * Sets offer header.
     */
    private void setOfferHeader() {
        mListener.setOfferHeaderTopic(mListener.getRestaurantContent().getRestaurantPosition(mRestaurantId));
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferItemFragment() {
    }

    /**
     * New instance offer item fragment.
     *
     * @param restaurantId the id of the restaurant
     * @return the offer item fragment
     */
    public static OfferItemFragment newInstance(int restaurantId) {
        // create a new instance of the fragment
        OfferItemFragment fragment = new OfferItemFragment();
        // create a bundle for the arguments
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        args.putInt(ARG_RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mRestaurantId = getArguments().getInt(ARG_RESTAURANT_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View view = inflater.inflate(R.layout.fragment_offer_item_list, container, false);

        mListener.onOfferFragmentGetsActive();
        setOfferHeader();

        // Set the adapter
        if (view instanceof RecyclerView) {
            // get the  context of the view
            Context context = view.getContext();
            // find the recycle view by id
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            if (mListener.getRestaurantContent() != null) {
                recyclerView.setAdapter(new OfferItemRecyclerViewAdapter(
                        mListener.getRestaurantContent().getOfferContent(mRestaurantId), mListener));
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.onOfferFragmentGetsInactive();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.offer_fragment, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOfferListFragmentInteractionListener) {
            mListener = (OnOfferListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRestaurantListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh_offers:
                // refresh the offers
                mListener.onOfferFragmentRefreshOffers(mRestaurantId);
                return true;
            case R.id.action_filter_offers:
                // filter the offers
                mListener.onOfferFragmentFilterOffers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnOfferListFragmentInteractionListener extends RestaurantAndLocationProvider {
        /**
         * Method that is invoked
         * in {@link OfferItemRecyclerViewAdapter}
         * when an item gets selected.
         *
         */
        void onOfferListFragmentInteraction();

        /**
         * Method that is invoked
         * in the method onCreateView of {@link OfferItemFragment},
         * when OfferItemFragment becomes active.
         */
        void onOfferFragmentGetsActive();

        /**
         * Method that is invoked
         * in the method onDestroy of {@link OfferItemFragment},
         * when OfferItemFragment gets inactive.
         */
        void onOfferFragmentGetsInactive();

        /**
         * Method that is invoked
         * when the user selects the refresh offers
         * item from the menu.
         * @param restaurantId the id of the restaurant to refresh the offers for
         */
        void onOfferFragmentRefreshOffers(int restaurantId);

        /**
         * Method that is invoked
         * when the user selects the filter offers
         * item from the menu.
         */
        void onOfferFragmentFilterOffers();

        /**
         * Method that is invoked
         * in the method onCreateView of {@link OfferItemFragment},
         * to set the restaurant information in the header.
         *
         * @param restaurantListPosition the restaurant list position
         */
        void setOfferHeaderTopic(int restaurantListPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register a BroadcastReceiver that receives intents
        // when the REST-API for the offers was called successfully.
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(MainActivity.INTENT_REST_OFFERS));
    }

    @Override
    public void onPause() {
        // unregister a BroadcastReceiver that receives intents
        // when the REST-API for the offers was called successfully.
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
