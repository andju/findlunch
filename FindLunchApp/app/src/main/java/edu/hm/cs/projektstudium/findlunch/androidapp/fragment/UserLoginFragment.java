package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.UserLoginFragmentContentValidator;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.UserRegistrationFragmentContentValidator;
import edu.hm.cs.projektstudium.findlunch.androidapp.validation.ValidationError;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.DebouncedOnClickListener;

/**
 * A {@link Fragment} that allows the
 * user to login with the credentials
 * from the registration.
 * Activities that contain this fragment must implement the
 * {@link OnLoginUserFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UserLoginFragment extends Fragment {

    /**
     * The constant ARG_ITEMS_ENABLED.
     */
    private static final String ARG_USERNAME = "userName";


    /**
     * The constant ARG_ITEMS_ENABLED.
     */
    private static final String ARG_PASSWORD = "password";

    /**
     * The information
     * whether the item selection
     * is enabled or not.
     */
    private String userName;

    /**
     * The information
     * whether the item selection
     * is enabled or not.
     */
    private String password;

    /**
     * The listener whose implementation of {@link OnLoginUserFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnLoginUserFragmentInteractionListener mListener;

    /**
     * Instantiates a new Main fragment.
     */
    public UserLoginFragment() {
        // Required empty public constructor
    }

    /**
     * New instance of user login fragment.
     *
     * @param userName the user name
     * @param password the password of the user
     * @return the user login fragment
     */
    public static UserLoginFragment newInstance(String userName, String password) {
        // create a new instance of the fragment
        UserLoginFragment fragment = new UserLoginFragment();
        // create a bundle for the arguments
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, userName);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USERNAME);
            password = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the view
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);

        // find the login button by id
        Button buttonLoginUser = (Button) view.findViewById(R.id.buttonLoginUser);

        // Get the text views for userName an password
        TextView usernameTextView = (TextView) view.findViewById(R.id.edit_login_username);
        TextView passwordTextView = (TextView) view.findViewById(R.id.edit_login_password);



        // Get the view that should link to the registration form
        View register = view.findViewById(R.id.view_login_register);

        // set the onClickListener for the buttons
        buttonLoginUser.setOnClickListener(new DebouncedOnClickListener(1500) {
            @Override
            public void onDebouncedClick(View v) {
                onLoginButtonPressed();
            }
        });

        // set the onClickListener for the registration reference
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onUserLoginFragmentRegisterInteraction();
            }
        });

        if(userName != null) {
            usernameTextView.setText(userName);
        }
        if(password != null) {
            passwordTextView.setText(password);
        }

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Method that gets invoked
     * by OnClickListener on button presses
     *
     */
    private void onLoginButtonPressed() {
        // Get the view with all elements
        View view = getView();
        if (view != null) {
            // Get the views for access to the information entered
            TextView usernameTextView = (TextView) view.findViewById(R.id.edit_login_username);
            TextView passwordTextView = (TextView) view.findViewById(R.id.edit_login_password);

           // get the user input
           String username = usernameTextView.getText().toString().trim();
           String password = passwordTextView.getText().toString().trim();


            // create a validator for the user input
            UserLoginFragmentContentValidator validator = new UserLoginFragmentContentValidator();
            // create an error object holding possible errors
            ValidationError error = new ValidationError();
            // map for the errors
            Map<String, String> errorStrings;

            // Set entered information
            UserLoginContent userLoginContent = new UserLoginContent(
                    username,
                    password);

            // check for validity
            validator.validate(userLoginContent, error);

            if (!error.hasErrors()) {
                // Call the callback method implemented by the activity
                mListener.onUserLoginFragmentLoginInteraction(userLoginContent);
            } else {
                errorStrings = error.getErrors();

                if(errorStrings.containsKey(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME)) {
                    if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME)
                            .equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME_BLANK)) {
                        usernameTextView.setError(getString(R.string.edit_message_username)
                                + " " + getText(R.string.message_not_empty));
                    } else if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME)
                            .equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_USERNAME_INVALID)) {
                        usernameTextView.setError(getString(R.string.edit_message_username)
                                + " " + getText(R.string.message_not_valid));
                    }
                }

                if(errorStrings.containsKey(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD)) {
                    if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD)
                            .equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_BLANK)) {
                        passwordTextView.setError(getString(R.string.edit_message_password)
                                + " " + getText(R.string.message_not_empty));
                    } else if(errorStrings.get(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD)
                            .equals(UserRegistrationFragmentContentValidator.ATTRIBUTE_PASSWORD_INVALID)) {
                        passwordTextView.setError(getString(R.string.edit_message_password)
                                + " " + getText(R.string.message_not_valid));
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginUserFragmentInteractionListener) {
            mListener = (OnLoginUserFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginUserFragmentInteractionListener");
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
    public interface OnLoginUserFragmentInteractionListener {
        void onUserLoginFragmentLoginInteraction(UserLoginContent contents);
        void onUserLoginFragmentRegisterInteraction();
    }
}
