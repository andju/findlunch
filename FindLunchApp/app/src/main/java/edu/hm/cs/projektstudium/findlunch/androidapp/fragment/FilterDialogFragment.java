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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginCredentialsProvider;


/**
 * A {@link Fragment} that allows the
 * user to filter the restaurants and offers.
 * <p>
 * Activities that contain this fragment must implement the
 * {@link FilterDialogInteractionListener} interface
 * to handle interaction events.
 */
public class FilterDialogFragment extends DialogFragment {

    /**
     * The constant ARG_ITEMS_ENABLED.
     */
    private static final String ARG_ITEMS_ENABLED = "items-enabled";

    /**
     * The information
     * whether the item selection
     * is enabled or not.
     */
    private boolean itemsEnabled;

    /**
     * The listener whose implementation of {@link FilterDialogFragment.FilterDialogInteractionListener}
     * gets called on interaction.
     */
    private FilterDialogInteractionListener mListener;
    /**
     * The selected items.
     */
    private boolean[] mSelectedItems;

    /**
     * Instantiates a new Filter dialog fragment.
     */
    public FilterDialogFragment() {

    }

    /**
     * New instance of filter dialog fragment.
     *
     * @param itemsEnabled the items enabled
     * @return the filter dialog fragment
     */
    public static FilterDialogFragment newInstance(boolean itemsEnabled) {
        // create a new instance of the fragment
        FilterDialogFragment fragment = new FilterDialogFragment();
        // create a bundle for the arguments
        Bundle args = new Bundle();
        args.putBoolean(ARG_ITEMS_ENABLED, itemsEnabled);
        fragment.setArguments(args);
        return fragment;
    }

    // Override the Fragment.onAttach() method to instantiate the FilterDialogInteractionListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FilterDialogInteractionListener) {
            mListener = (FilterDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            itemsEnabled = getArguments().getBoolean(ARG_ITEMS_ENABLED);
        }

        mSelectedItems = mListener.getRestaurantContent().getFilter().getKitchenTypeSelectionMask();
        // variable for the dialog
        AlertDialog dialog;
        // create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // inflate the view
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.fragment_dialog_filter, nullParent);
        // find the text view for the maximum offer price
        final TextView maxOfferPriceView = (TextView) view.findViewById(R.id.maxOfferPrice);
        // find the spinner that allows the selection to show only favourites
        final Spinner onlyFavouritesSpinner = (Spinner) view.findViewById(R.id.spinnerOnlyFavourites);

        // Set the title
        builder.setTitle(getString(R.string.title_filter));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        // the index of the default selection
        // of the only favourites spinner
        int defaultIndex;
        // create the adapter for the only favourites spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.favourites_only_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        onlyFavouritesSpinner.setAdapter(adapter);

        int maxPrice = mListener.getRestaurantContent().getFilter().getMaxPrice();

        if (mListener.getRestaurantContent().getFilter().validMaxPrice(maxPrice)) {
            // select former maximum price
            maxOfferPriceView.setText(String.valueOf(maxPrice));
        }

        // set spinner to all restaurants or only favourites
        if (mListener.getRestaurantContent().getFilter().isOnlyFavourites()) {
            defaultIndex = adapter.getPosition(getResources().getString(R.string.favourites_only));
        } else {
            defaultIndex = adapter.getPosition(getResources().getString(R.string.all_restaurants));
        }
        if (defaultIndex != -1) {
            onlyFavouritesSpinner.setSelection(defaultIndex);
        }

        builder.setMultiChoiceItems(mListener.getRestaurantContent().getKitchenTypes(), mListener.getRestaurantContent().getFilter().getKitchenTypeSelectionMask(),
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                mSelectedItems[which] = isChecked;
                            }
                        })

                // Add a positive button that saves the user input
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // the maximum price user input
                        String maxPriceString = maxOfferPriceView.getText().toString().trim();
                        // the maximum price user input
                        int maxPrice = maxPriceString.equals("") ? 0 : Integer.parseInt(maxPriceString);
                        // the user input of favourites only
                        boolean onlyFavourites = onlyFavouritesSpinner.getSelectedItem().toString().trim().equals(getString(R.string.favourites_only));

                        // save the values in filter
                        mListener.getRestaurantContent().getFilter().setKitchenTypesSelected(mSelectedItems);
                        mListener.getRestaurantContent().getFilter().setMaxPrice(maxPrice);
                        mListener.getRestaurantContent().getFilter().setOnlyFavourites(onlyFavourites);

                        // Send the positive button event back to the host activity
                        mListener.onFilterDialogPositiveClick();
                    }
                })
                // Add a negative button that cancels the dialog
                .setNegativeButton(android.R.string.cancel, null);

        dialog =  builder.create();
        // only enable items and spinner when the dialog got called from the restaurant fragment.
        dialog.getListView().setEnabled(itemsEnabled);
        onlyFavouritesSpinner.setEnabled(itemsEnabled && mListener.getUserLoginCredentials().isLoggedIn());
        if(!mListener.getUserLoginCredentials().isLoggedIn()) {
            onlyFavouritesSpinner.setVisibility(View.GONE);
        }

        return dialog;
    }

    /**
     * The interface Filter dialog interaction listener.
     */
/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FilterDialogInteractionListener  extends RestaurantAndLocationProvider, UserLoginCredentialsProvider {
        /**
         * Method that is invoked
         * in {@link FilterDialogFragment}
         * when the positive button gets pressed
         *
         */
        void onFilterDialogPositiveClick();
    }

}