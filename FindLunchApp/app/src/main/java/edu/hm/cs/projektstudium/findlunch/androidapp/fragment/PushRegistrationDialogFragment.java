package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginCredentialsProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.DateHelper;

/**
 * A {@link Fragment} that allows the registration of a push notification.
 * Activities that contain this fragment must implement the
 * {@link PushRegistrationDialogInteractionListener} interface
 * to handle interaction events.
 *
 * Created by Andreas Juckel on 10.07.2016.
 */
public class PushRegistrationDialogFragment extends DialogFragment {

    /**
     * The listener whose implementation of
     * {@link PushRegistrationDialogFragment.PushRegistrationDialogInteractionListener}
     * gets called on interaction.
     */
    PushRegistrationDialogInteractionListener mListener;

    /**
     * The selected weekdays.
     */
    boolean[] mSelectedWeekdays;

    /**
     * Empty constructor (required)
     */
    public PushRegistrationDialogFragment() {
    }

    /**
     * New instance of push registration dialog fragment.
     *
     * @return the push registration dialog fragment
     */
    public static PushRegistrationDialogFragment newInstance() {
        PushRegistrationDialogFragment fragment = new PushRegistrationDialogFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    // Override the Fragment.onAttach() method to instantiate the PushRegistrationDialogInteractionListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (PushRegistrationDialogInteractionListener) context;
        } catch (Exception e) {
            throw new RuntimeException(context.toString()
                    + " must implement PushRegistrationDialogInteractionListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Pre-select Monday to Friday (1-7)
        boolean[] mPreSelectedWeekdays = new boolean[7];
        for(int i = 0;i<=4;i++) {
            mPreSelectedWeekdays[i] = true;
        }

        mSelectedWeekdays = mPreSelectedWeekdays;

        // variable for the dialog
        AlertDialog dialog;
        // create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate the view
        final View view = inflater.inflate(R.layout.fragment_dialog_push_registration, null);
        // set the current location as editable name
        EditText locationName = (EditText) view.findViewById(R.id.push_title);
        locationName.setText(mListener.getUserContent().getHeader());

        // get the selected kitchenTypes
        StringBuilder kitchenTypes = new StringBuilder();
        if(mListener.getRestaurantContent().getFilter().getKitchenTypeNamesSelected() != null
                && mListener.getRestaurantContent().getFilter().getKitchenTypeNamesSelected().size() > 0) {
            for(String a : mListener.getRestaurantContent().getFilter().getKitchenTypeNamesSelected()) {
                kitchenTypes.append(a);
                kitchenTypes.append(", ");
            }
        } else {
            kitchenTypes.append(getString(R.string.text_all) + ", ");
        }
        TextView filterName = (TextView) view.findViewById(R.id.filter_name);
        filterName.setText(kitchenTypes.substring(0,kitchenTypes.length()-2));

        // Set the title
        builder.setTitle(getString(R.string.title_push));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        builder.setMultiChoiceItems(mListener.getDateHelper().getWeekDays(), mPreSelectedWeekdays,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            mSelectedWeekdays[which] = true;
                        } else {
                            mSelectedWeekdays[which] = false;
                        }
                    }
                })

                // Add a positive button that saves the title (user input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String pushLocation = ((EditText) view.findViewById(R.id.push_title)).getText().toString();
                        // remove line breaks from user input
                        pushLocation = pushLocation.replaceAll("\\n"," ");

                        mListener.onPushRegistrationDialogPositiveClick(mSelectedWeekdays, pushLocation);
                    }
                })
                // Add a negative button that cancels the dialog
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PushRegistrationDialogFragment.this.getDialog().cancel();
                    }
                });

        dialog =  builder.create();

        return dialog;
    }

    /**
    *  This interface must be implemented by activities that use the
    * {@link PushRegistrationDialogFragment}, to allow functions in the fragment
     * to communicate with the activity.
    */
    public interface PushRegistrationDialogInteractionListener extends RestaurantAndLocationProvider, UserLoginCredentialsProvider {
        /**
         * Handles the request for the registration of a push notification
         *
         * @param selectedWeekdays Array with the selected weekdays (array position represents number of weekday)
         * @param pushTitle Title of the push notification
         */
        void onPushRegistrationDialogPositiveClick(boolean[] selectedWeekdays, String pushTitle);

        /**
         * Returns a DateHelper instance.
         *
         * @return DateHelpler instance
         */
        DateHelper getDateHelper();
    }

}
