package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;


/**
 * A {@link Fragment} that allows the
 * user to log out.
 * <p>
 * Activities that contain this fragment must implement the
 * {@link UserLogoutDialogInteractionListener} interface
 * to handle interaction events.
 */
public class UserLogoutDialogFragment extends DialogFragment {

    /**
     * The listener whose implementation of {@link UserLogoutDialogInteractionListener}
     * gets called on interaction.
     */
    private UserLogoutDialogInteractionListener mListener;

    /**
     * Instantiates a new Filter dialog fragment.
     */
    public UserLogoutDialogFragment() {

    }

    // Override the Fragment.onAttach() method to instantiate the UserLogoutDialogInteractionListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserLogoutDialogInteractionListener) {
            mListener = (UserLogoutDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserLogoutDialogInteractionListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.text_logout)
                .setMessage(R.string.text_ask_logout)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // perform logout
                        mListener.onUserLogoutDialogPositiveClick();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * The interface User logout dialog interaction listener.
     */
/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface UserLogoutDialogInteractionListener extends RestaurantAndLocationProvider {
        /**
         * Method that is invoked
         * in {@link UserLogoutDialogFragment}
         * when the positive button gets pressed
         *
         */
        void onUserLogoutDialogPositiveClick();
    }

}