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
import android.view.View;
import android.view.ViewGroup;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.activity.MainActivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.adapter.RestaurantItemRecyclerViewAdapter;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginCredentialsProvider;

/**
 * A {@link Fragment} representing a list of Restaurants.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnRestaurantListFragmentInteractionListener}
 * interface.
 */
public class RestaurantItemFragment extends Fragment {

    /**
     * The constant ARG_COLUMN_COUNT.
     */
    private static final String ARG_COLUMN_COUNT = "column-count";

    /**
     * The number of columns.
     */
    private int mColumnCount = 1;
    /**
     * The listener whose implementation of {@link RestaurantItemFragment.OnRestaurantListFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnRestaurantListFragmentInteractionListener mListener;

    /**
     * BroadcastReceiver that receives intents when
     * the REST-API for the restaurants was called successfully.
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshRecycleView();
        }
    };

    /**
     * Refresh the recycle view with the restaurant information.
     */
    private void refreshRecycleView() {
        if (getView() != null) {
            // variable for the recycle view
            RecyclerView view = (RecyclerView) getView().findViewById(R.id.list);

            if (view != null) {
                view.getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View view = inflater.inflate(R.layout.fragment_restaurant_item_list, container, false);

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
                recyclerView.setAdapter(new RestaurantItemRecyclerViewAdapter(mListener.getRestaurantContent(), mListener));
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRestaurantListFragmentInteractionListener) {
            mListener = (OnRestaurantListFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRestaurantListFragmentInteractionListener extends RestaurantAndLocationProvider, UserLoginCredentialsProvider {
        /**
         * Method that is invoked
         * in {@link RestaurantItemRecyclerViewAdapter}
         * when an item gets selected.
         *
         * @param holder the holder
         */
        void onRestaurantListFragmentInteraction(RecyclerView.ViewHolder holder);
        /**
         * Method that is invoked
         * in {@link RestaurantItemRecyclerViewAdapter}
         * when the favourite icon an item gets selected.
         *
         * @param holder the holder
         */
        void onRestaurantListFragmentFavouriteInteraction(RecyclerView.ViewHolder holder);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register a BroadcastReceiver that receives intents
        // when the REST-API for the restaurants was called successfully.
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(MainActivity.INTENT_REST_RESTAURANTS));
    }

    @Override
    public void onPause() {
        // unregister a BroadcastReceiver that receives intents
        // when the REST-API for the restaurants was called successfully.
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
