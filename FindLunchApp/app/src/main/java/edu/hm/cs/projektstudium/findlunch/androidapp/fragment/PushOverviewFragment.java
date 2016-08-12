package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.adapter.PushItemRecyclerViewAdapter;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;

/**
 * A {@link Fragment} that displays the active push notifications of
 * the current user.
 * Activities that contain this fragment must implement the
 * {@link OnPushOverviewFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Created by Andres Juckel on 18.07.2016.
 */
public class PushOverviewFragment extends Fragment {

    /**
     * The listener whose implementation of {@link PushOverviewFragment.OnPushOverviewFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnPushOverviewFragmentInteractionListener mListener;

    /**
     * Empty constructor (required)
     */
    public PushOverviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // disable the menu from other fragments
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View view = inflater.inflate(R.layout.fragment_push_overview_item_list, container, false);

        mListener.onPushOverviewFragmentGetsActive();
        mListener.setPushOverviewHeaderTopic(mListener.getActivePushNotifications().size() > 0);

        // Set the adapter
        try {
            // get the  context of the view
            Context context = view.getContext();
            // find the recycle view by id
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            recyclerView.setAdapter(new PushItemRecyclerViewAdapter(mListener.getActivePushNotifications(), mListener));
        } catch (Exception e) {
            Log.e("PushOverviewFragment", e.getMessage());
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPushOverviewFragmentInteractionListener) context;
        } catch (Exception e) {
            throw new RuntimeException(context.toString()
                    + " must implement OnPushOverviewFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.onPushOverviewFragmentGetsInactive();
    }

    /**
     * This interface must be implemented by activities that use the
     * {@link PushOverviewFragment}, to allow functions in the fragment
     * to communicate with the activity.
     */
    public interface OnPushOverviewFragmentInteractionListener {
        /**
         * Returns the list of active push notification of the user
         */
        List<PushNotification> getActivePushNotifications();
        /**
         * Handles the request for the deletion of a push notification
         *
         * @param holder the holder from which the request is initiated
         */
        void onPushOverviewFragmentDeleteInteraction(RecyclerView.ViewHolder holder);

        /**
         * Includes actions to execute when the {@link PushOverviewFragment} is activated.
         * Loads the header for the Fragment.
         */
        void onPushOverviewFragmentGetsActive();

        /**
         * Includes actions to execute when the {@link PushOverviewFragment} is deactivated.
         * Removes the header of the Fragment.
         */
        void onPushOverviewFragmentGetsInactive();

        /**
         * Sets the correct title of the Fragment, depending whether the User has active push
         * notification to display.
         *
         * @param pushNotificationAvailable Does the user have active push notifications to display?
         */
        void setPushOverviewHeaderTopic(boolean pushNotificationAvailable);
    }

}
