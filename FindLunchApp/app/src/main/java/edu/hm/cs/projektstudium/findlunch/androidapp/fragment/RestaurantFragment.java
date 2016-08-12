package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;

/**
 * A {@link Fragment} that contains two tabs
 * with a list of restaurants and a map.
 * Activities that contain this fragment must implement the
 * {@link OnResultFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RestaurantFragment extends Fragment {

    /**
     * The constant TAB_NAME_LIST.
     */
    private static final String TAB_NAME_LIST = "list";
    /**
     * The constant TAB_NAME_MAP.
     */
    private static final String TAB_NAME_MAP = "map";

    /**
     * The fragment tab host.
     */
    private FragmentTabHost mTabHost;

    /**
     * The listener whose implementation of {@link RestaurantFragment.OnResultFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnResultFragmentInteractionListener mListener;

    /**
     * Instantiates a new Restaurant fragment.
     */
    public RestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Notify the activity that the restaurant fragment gets active
        mListener.onRestaurantFragmentGetsActive();

        // Create a fragment tab host that holds the tabs
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.flResultContent);

        // Create the tabs
        mTabHost.addTab(mTabHost.newTabSpec(TAB_NAME_LIST).setIndicator(getString(R.string.tabList)),
                RestaurantItemFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_NAME_MAP).setIndicator(getString(R.string.tabMap)),
                RestaurantMapFragment.class, null);

        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Notify the activity that the restaurant fragment gets inactive
        mListener.onRestaurantFragmentGetsInactive();
        mTabHost = null;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.restaurant_fragment, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResultFragmentInteractionListener) {
            mListener = (OnResultFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainFragmentInteractionListener");
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
            case R.id.action_push:
                mListener.onRestaurantFragmentPushNotificationRegistration();
                return true;
            case R.id.action_refresh:
                // refresh the restaurants
                mListener.onRestaurantFragmentRefreshRestaurants();
                return true;
            case R.id.action_filter_restaurants:
                // filter the restaurants
                mListener.onRestaurantFragmentFilterRestaurants();
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
    public interface OnResultFragmentInteractionListener {

        /**
         * Method that is invoked
         * in the method onCreateView of {@link RestaurantFragment},
         * when {@link RestaurantFragment} becomes active.
         */
        void onRestaurantFragmentGetsActive();

        /**
         * Method that is invoked
         * in the method onDestroyView of {@link RestaurantFragment},
         * when {@link RestaurantFragment} gets inactive.
         */
        void onRestaurantFragmentGetsInactive();

        /**
         * Method that is invoked
         * when the user selects the refresh restaurants
         * item from the menu.
         */
        void onRestaurantFragmentRefreshRestaurants();

        /**
         * Method that is invoked
         * when the user selects the filter
         * item from the menu.
         */
        void onRestaurantFragmentFilterRestaurants();

        /**
         * Method that is invoked
         * when the user selects the push notification
         * item from the menu.
         */
        void onRestaurantFragmentPushNotificationRegistration();
    }
}
