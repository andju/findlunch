package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserRegistrationContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.UserRegistrationFragmentContentValidator;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.ValidationError;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.DebouncedOnClickListener;


/**
 * A {@link Fragment} that allows the
 * user to register.
 * Activities that contain this fragment must implement the
 * {@link OnRegisterUserFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UserRegistrationFragment extends Fragment {

    /**
     * The listener whose implementation of {@link OnRegisterUserFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnRegisterUserFragmentInteractionListener mListener;

    /**
     * Instantiates a new Main fragment.
     */
    public UserRegistrationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_registration, container, false);

        // find the register button by id
        Button buttonRegisterUser = (Button) view.findViewById(R.id.buttonRegisterUser);
        buttonRegisterUser.setOnClickListener(new DebouncedOnClickListener(1500) {
            @Override
            public void onDebouncedClick(View v) {
                onButtonPressed(v);
            }
        });

        // set text for terms and conditions
        TextView termsConditionsTextView = (TextView) view.findViewById(R.id.text_terms_conditions);

        String termsAndConditions = getString(R.string.text_terms_conditions);
        String termsOfUse = getString(R.string.text_terms_of_use);
        String dataPrivacyStatement = getString(R.string.text_data_privacy_statement);

        // format the string with the arguments
        String formattetTermsAndConditions = String.format(termsAndConditions, termsOfUse, dataPrivacyStatement);

        // spannable string allows to click on certain words
        SpannableString ss = new SpannableString(formattetTermsAndConditions);

        // paths on the findlunch page
        String pathTerms = getString(R.string.url_path_terms);
        String pathDataPrivacy = getString(R.string.url_path_data_privacy);

        // clickable spans that open a page in the browser
        ClickableSpan clickableSpanTerms = getClickableSpan(pathTerms);
        ClickableSpan clickableSpanDataPrivacy = getClickableSpan(pathDataPrivacy);

        ss.setSpan(clickableSpanTerms,
                formattetTermsAndConditions.indexOf(termsOfUse),
                formattetTermsAndConditions.indexOf(termsOfUse) + termsOfUse.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpanDataPrivacy,
                formattetTermsAndConditions.indexOf(dataPrivacyStatement),
                formattetTermsAndConditions.indexOf(dataPrivacyStatement) + dataPrivacyStatement.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsConditionsTextView.setText(ss);
        termsConditionsTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Inflate the layout for this fragment
        return view;
    }

    @NonNull
    private ClickableSpan getClickableSpan(final String path) {
        return new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // open link in browser
                mListener.onUserRegistrationFragmentOpenLinkInteraction(path);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
    }

    /**
     * Method that gets invoked
     * by OnClickListener on button presses
     *
     * @param v the button view
     */
    private void onButtonPressed(View v) {
        // Check if there is a listener
        if (mListener != null) {

            // Get the view with all elements
            View view = getView();
            // Check which button was pressed
            switch (v.getId()) {
                case R.id.buttonRegisterUser:
                    if (view != null) {
                        // Get the views for access to the information entered
                        TextView usernameTextView = (TextView) view.findViewById(R.id.edit_username);
                        TextView passwordTextView = (TextView) view.findViewById(R.id.edit_password);
                        TextView passwordRepeatedTextView = (TextView) view.findViewById(R.id.edit_password_repeated);

                        // get the user input
                        String username = usernameTextView.getText().toString().trim();
                        String password = passwordTextView.getText().toString().trim();
                        String passwordRepeated = passwordRepeatedTextView.getText().toString().trim();

                        // create a validator for the user input
                        UserRegistrationFragmentContentValidator validator = new UserRegistrationFragmentContentValidator();
                        // create an error object holding possible errors
                        ValidationError error = new ValidationError();
                        // map for the errors
                        Map<String, String> errorStrings;

                        // Set entered information
                        UserRegistrationContent userRegistrationContent = new UserRegistrationContent(
                                username,
                                password,
                                passwordRepeated);

                        // check for validity
                        validator.validate(userRegistrationContent, error);

                        if (!error.hasErrors()) {
                            // Call the callback method implemented by the activity
                            mListener.onUserRegistrationFragmentInteraction(userRegistrationContent);

                        } else {
                            errorStrings = error.getErrors();

                            if(errorStrings.containsKey(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME)) {
                                if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME).equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME_BLANK)) {
                                    usernameTextView.setError(getString(R.string.edit_message_username) + " " + getText(R.string.message_not_empty));
                                } else if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME).equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME_INVALID)) {
                                    usernameTextView.setError(getString(R.string.edit_message_username) + " " + getText(R.string.message_not_valid));
                                }
                            }

                            if(errorStrings.containsKey(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD)) {
                                if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD).equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_BLANK)) {
                                    passwordTextView.setError(getString(R.string.edit_message_password) + " " + getText(R.string.message_not_empty));
                                } else if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD).equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_INVALID)) {
                                    passwordTextView.setError(getString(R.string.edit_message_password) + " " + getText(R.string.message_not_valid));
                                }
                            }

                            if(errorStrings.containsKey(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_REPEATED)) {
                                if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_REPEATED).equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_REPEATED_BLANK)) {
                                    passwordRepeatedTextView.setError(getString(R.string.edit_message_password_repeated) + " " + getText(R.string.message_not_empty));
                                } else if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_REPEATED).equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_REPEATED_NOT_EQUAL)) {
                                    passwordRepeatedTextView.setError(getString(R.string.edit_message_password_repeated) + " " + getText(R.string.message_not_equal));
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterUserFragmentInteractionListener) {
            mListener = (OnRegisterUserFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterUserFragmentInteractionListener");
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
    public interface OnRegisterUserFragmentInteractionListener {
        /**
         * Method that hands over the
         * formula data provided by
         * the user to the activity
         *
         * @param contents formula data provided by the user
         */
        void onUserRegistrationFragmentInteraction(UserRegistrationContent contents);

        void onUserRegistrationFragmentOpenLinkInteraction(String path);
    }
}
