package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLocationContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.MainFragmentContentValidator;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.ValidationError;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.DebouncedOnClickListener;

/**
 * A {@link Fragment} that allows the
 * user to provide the location information
 * and start the search for restaurants.
 * Activities that contain this fragment must implement the
 * {@link OnMainFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {

    /**
     * The listener whose implementation of {@link MainFragment.OnMainFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnMainFragmentInteractionListener mListener;

    /**
     * Instantiates a new Main fragment.
     */
    public MainFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Create the spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_distance_range);
        // create the adapter for the only favourites spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.distance_range_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // the index of the default selection
        // of the distance spinner
        int defaultIndex = adapter.getPosition(getResources().getString(R.string.distance_range_default));
        if (defaultIndex != -1) {
            spinner.setSelection(defaultIndex);
        }

        // find the button find offers by id
        Button buttonFindOffers = (Button) view.findViewById(R.id.buttonFindOffers);
        buttonFindOffers.setOnClickListener(new DebouncedOnClickListener(1500) {
            @Override
            public void onDebouncedClick(View v) {
                onSearchButtonPressed();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Method that gets invoked
     * by OnClickListener on button presses
     *
     */
    private void onSearchButtonPressed() {
        // Get the view with all elements
        View view = getView();
        if (view != null) {
            // Get the views for access to the information entered
            TextView streetTextView = (TextView) view.findViewById(R.id.edit_street);
            TextView streetNumberTextView = (TextView) view.findViewById(R.id.edit_street_numer);
            TextView zipTextView = (TextView) view.findViewById(R.id.edit_message_zip);
            Spinner distanceSpinner = (Spinner) view.findViewById(R.id.spinner_distance_range);

            // get the user input
           String street = streetTextView.getText().toString().trim();
           String streetNumber = streetNumberTextView.getText().toString().trim();
           String zip = zipTextView.getText().toString().trim();
           String distance = distanceSpinner.getSelectedItem().toString().trim();

            // create a validator for the user input
            MainFragmentContentValidator validator = new MainFragmentContentValidator();
            // create an error object holding possible errors
            ValidationError error = new ValidationError();
            // map for the errors
            Map<String, String> errorStrings;

            // Set entered information
            UserLocationContent userLocationContent = new UserLocationContent(
                    street,
                    streetNumber,
                    zip,
                    distance);

            // check for validity
            validator.validate(userLocationContent, error);

            if (!error.hasErrors()) {
                // Call the callback method implemented by the activity
                mListener.onMainFragmentInteraction(userLocationContent);

            } else {
                errorStrings = error.getErrors();

                if(errorStrings.containsKey(MainFragmentContentValidator.ATTRIBUTE_STREET)) {
                    if(errorStrings.get(MainFragmentContentValidator.ATTRIBUTE_STREET).equals(MainFragmentContentValidator.ATTRIBUTE_STREET_BLANK)) {
                        streetTextView.setError(getString(R.string.edit_message_street) + " " + getText(R.string.message_not_empty));
                    } else if(errorStrings.get(MainFragmentContentValidator.ATTRIBUTE_STREET).equals(MainFragmentContentValidator.ATTRIBUTE_STREET_INVALID)) {
                        streetTextView.setError(getString(R.string.edit_message_street) + " " + getText(R.string.message_not_valid));
                    }
                }

                if(errorStrings.containsKey(MainFragmentContentValidator.ATTRIBUTE_STREET_NUMBER)) {
                    if(errorStrings.get(MainFragmentContentValidator.ATTRIBUTE_STREET_NUMBER).equals(MainFragmentContentValidator.ATTRIBUTE_STREET_NUMBER_BLANK)) {
                        streetNumberTextView.setError(getString(R.string.edit_message_street_number) + " " + getText(R.string.message_not_empty));
                    } else if(errorStrings.get(MainFragmentContentValidator.ATTRIBUTE_STREET_NUMBER).equals(MainFragmentContentValidator.ATTRIBUTE_STREET_NUMBER_INVALID)) {
                        streetNumberTextView.setError(getString(R.string.edit_message_street_number) + " " + getText(R.string.message_not_valid));
                    }
                }

                if(errorStrings.containsKey(MainFragmentContentValidator.ATTRIBUTE_ZIP)) {
                    if(errorStrings.get(MainFragmentContentValidator.ATTRIBUTE_ZIP).equals(MainFragmentContentValidator.ATTRIBUTE_ZIP_BLANK)) {
                        zipTextView.setError(getString(R.string.edit_message_zip) + " " + getText(R.string.message_not_empty));
                    } else if(errorStrings.get(MainFragmentContentValidator.ATTRIBUTE_ZIP).equals(MainFragmentContentValidator.ATTRIBUTE_ZIP_INVALID)) {
                        zipTextView.setError(getString(R.string.edit_message_zip) + " " + getText(R.string.message_not_valid));
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
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
    public interface OnMainFragmentInteractionListener {
        /**
         * Method that hands over the
         * formula data provided by
         * the user to the activity
         *
         * @param contents formula data provided by the user
         */
        void onMainFragmentInteraction(UserLocationContent contents);
    }
}
