package edu.hm.cs.projektstudium.findlunch.androidapp.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.DayOfWeekContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.OfferContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLocationContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginCredentials;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserRegistrationContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.FilterDialogFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.MainFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.OfferItemFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.PushOverviewFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.PushRegistrationDialogFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.ReservationFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.RestaurantFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.RestaurantItemFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.RestaurantMapFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.UserLoginFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.UserLogoutDialogFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.UserRegistrationFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.geocoding.GoogleGeoCodeResponse;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.DateHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.EmailHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.KeyboardHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.PhoneHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.interaction.WebHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Captcha;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.User;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformation;
import edu.hm.cs.projektstudium.findlunch.androidapp.network.ConnectionInformationFindlunch;
import edu.hm.cs.projektstudium.findlunch.androidapp.permissions.PermissionHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.push.AmazonPushListenerService;
import edu.hm.cs.projektstudium.findlunch.androidapp.push.AmazonSnsRegHandler;
import edu.hm.cs.projektstudium.findlunch.androidapp.push.FirebaseTokenReceiver;
import edu.hm.cs.projektstudium.findlunch.androidapp.push.FirebaseTokenRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.FavouriteRegistrationStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.FavouriteRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.OfferRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.OnHttpRequestFinishedCallback;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.PushNotificationDeleteStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.PushNotificationRegistrationStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.Request;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.RequestHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.RequestReason;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.RequestResult;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.RequestResultDetail;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.ReservationConfirmRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.ReservationConfirmStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.ReservationRegistrationRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.ReservationRegistrationStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.UserLoginRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.UserLoginStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.UserRegistrationRequest;
import edu.hm.cs.projektstudium.findlunch.androidapp.rest.UserRegistrationStatus;
import edu.hm.cs.projektstudium.findlunch.androidapp.storage.StorageHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.MessageHelper;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.ViewHelper;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnMainFragmentInteractionListener,
        RestaurantFragment.OnResultFragmentInteractionListener,
        RestaurantItemFragment.OnRestaurantListFragmentInteractionListener,
        OfferItemFragment.OnOfferListFragmentInteractionListener,
        RestaurantMapFragment.OnFragmentInteractionListener,
        FilterDialogFragment.FilterDialogInteractionListener,
        PushRegistrationDialogFragment.PushRegistrationDialogInteractionListener,
        UserLogoutDialogFragment.UserLogoutDialogInteractionListener,
        UserRegistrationFragment.OnRegisterUserFragmentInteractionListener,
        UserLoginFragment.OnLoginUserFragmentInteractionListener,
        PushOverviewFragment.OnPushOverviewFragmentInteractionListener,
        OnHttpRequestFinishedCallback,
        RestaurantAndLocationProvider,ReservationFragment.OnReservationFragmentInteractionListener {

    /**
     * The Request helper.
     */
    private final RequestHelper requestHelper = new RequestHelper();
    /**
     * The Message helper.
     */
    private final MessageHelper messageHelper = new MessageHelper(this);
    /**
     * The Storage helper.
     */
    private final StorageHelper storageHelper = new StorageHelper();
    /**
     * The Email helper.
     */
    private final EmailHelper emailHelper = new EmailHelper(this);
    /**
     * The Web helper.
     */
    private final WebHelper webHelper = new WebHelper(this);
    /**
     * The Phone helper.
     */
    private final PhoneHelper phoneHelper = new PhoneHelper(this);
    /**
     * The Permission helper.
     */
    private final PermissionHelper permissionHelper = new PermissionHelper(this);
    /**
     * The KeyboardHelper.
     */
    private final KeyboardHelper keyboardHelper = new KeyboardHelper();
    /**
     * The DateHelper.
     */
    private final DateHelper dateHelper = new DateHelper(this);
    /**
     * The Connection information.
     */
    private final ConnectionInformation connectionInformation = new ConnectionInformationFindlunch();
    /**
     * The Restaurant content.
     */
    private RestaurantContent restaurantContent;
    /**
     * The information provided by the user.
     */
    private UserContent userContent;
    /**
     * The DaysOfWeek content.
     */
    private DayOfWeekContent dayOfWeekContent;

    /**
     * The login credentials of the user.
     */
    private UserLoginCredentials userLoginCredentials;

    /**
     * Handler for Amazon SNS registration.
     */
    private AmazonSnsRegHandler amazonRegHandler;

    /**
     * Entry for no service.
     */
    public static final String NOT_AVAILABLE = "notAvailable";

    /**
     * Receive the FCM/ADM token, after it has been obtained from Google/Amazon.
     */
    private final BroadcastReceiver multiTokenBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {


            Boolean fcmSuccess = intent.getBooleanExtra(FirebaseTokenReceiver.FCM_TOKEN_MESSAGE_SUCCESS, false);
            Boolean admSuccess = intent.getBooleanExtra(AmazonPushListenerService.ADM_TOKEN_MESSAGE_SUCCESS, false);

            userLoginCredentials.setFcmToken(NOT_AVAILABLE);
            userLoginCredentials.setSnsToken(NOT_AVAILABLE);

            if (fcmSuccess) {
                String fcmToken = intent.getStringExtra(FirebaseTokenReceiver.FCM_TOKEN_FILTER_EXTRA);
                Log.e("MainActivity-FCM-BC-Receiver", "Set new Token to User: " + fcmToken);
                userLoginCredentials.setFcmToken(fcmToken);

                //Initial register token at webserver
                performInitialPush();

            } else if(admSuccess) {
                String snsToken = intent.getStringExtra(AmazonPushListenerService.ADM_TOKEN_FILTER_EXTRA);
                Log.e("MainActivity-FCM-BC-Receiver", "Set new Token to User: " + snsToken);
                userLoginCredentials.setSnsToken(snsToken);

                //Initial register token at webserver
                performInitialPush();

            } else {
                Log.e("ADM-BC-Receive", "No new token obtained");

            }
        }
    };

    /**
     * List of active push notifications for the user (push notification overview).
     */
    private List<PushNotification> activePushNotifications = new ArrayList<>();

    /**
     * Do not display registration push.
     */
    private boolean showPushRegToastMsgs = true;

    /**
     * The constant INTENT_REST_RESTAURANTS.
     */
    public static final String INTENT_REST_RESTAURANTS = "INTENT_REST_RESTAURANTS";
    /**
     * The constant INTENT_REST_OFFERS.
     */
    public static final String INTENT_REST_OFFERS = "INTENT_REST_OFFERS";

    /**
     * Gets the dateHelper.
     *
     * @return the dateHelper.
     */
    public DateHelper getDateHelper() {
        return dateHelper;
    }

    /**
     * Gets the restaurant content.
     *
     * @return the restaurant content.
     */
    public RestaurantContent getRestaurantContent() {
        return restaurantContent;
    }

    /**
     * Gets the user content.
     *
     * @return the user content.
     */
    public UserContent getUserContent() {
        return userContent;
    }

    /**
     * Gets the user content.
     *
     * @return the user content.
     */
    public DayOfWeekContent getDayOfWeekContent() {
        return dayOfWeekContent;
    }

    /**
     * Gets the user login credentials.
     *
     * @return the user login credentials.
     */
    public UserLoginCredentials getUserLoginCredentials() {
        return userLoginCredentials;
    }

    @Override
    public List<PushNotification> getActivePushNotifications() {
        return activePushNotifications;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        // create navigation view
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // place main fragment within frame layout
        changeFragment(new MainFragment(), false);

        // get an instance of user content, user login credentials and restaurant content
        userContent = UserContent.getInstance(this);
        userLoginCredentials = UserLoginCredentials.getInstance(this);
        restaurantContent = RestaurantContent.getInstance(this);

        // change visibility of drawer items, according to login state
        checkLogin();

        // read the connection information from external storage, if available
        readConnectionInformation();

        /* Check if the MainActivity was called from a defined Fragment.
         */
        Intent i = getIntent();

        int intentSource = i.getIntExtra("intent_source", 0);
        if (intentSource == 1) {

            // Get information from intent.
            String pushTitle = i.getStringExtra("title");
            Float pushLongitude = i.getFloatExtra("longitude", 0.0f);
            Float pushLatitude = i.getFloatExtra("latitude", 0.0f);
            int pushRadius = i.getIntExtra("radius", 0);
            int[] kitchenTypeIds = i.getIntArrayExtra("kitchenTypeIds");

            // Set information as search-parameters.
            userContent.setInformation("","","",pushRadius);
            userContent.setLongitude(pushLongitude);
            userContent.setLatitude(pushLatitude);
            setHeaderTopic(pushTitle);

            // Set kitchen type id numbers for filter
            restaurantContent.getFilter().setKitchenTypesPush(kitchenTypeIds);

            // Request Restaurants (based on search-parameters (and open RestaurantFragment).
            requestHelper.requestRestaurants(
                    RequestReason.SEARCH, userContent.getLongitude(),
                    userContent.getLatitude(), userContent.getDistance(),
                    userLoginCredentials.getUserName(), userLoginCredentials.getPassword(),
                    connectionInformation, this);
        }

    }

    /**
     * Changes visibility of drawer items,
     * according to login state.
     */
    private void checkLogin() {
        // navigation view that contains the items and the header
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // header view that contains the userName
        View navigationViewHeaderView;
        // view for the userName
        TextView userNameTextView;

        if (navigationView != null) {
            navigationViewHeaderView = navigationView.getHeaderView(0);

            if (navigationViewHeaderView != null) {
                userNameTextView = (TextView ) navigationViewHeaderView.findViewById(R.id.text_user_name);

                if (userNameTextView != null) {
                    MenuItem loginItem = navigationView.getMenu().findItem(R.id.nav_login);
                    MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);
                    MenuItem registerItem = navigationView.getMenu().findItem(R.id.nav_register);
                    MenuItem pushOverviewItem = navigationView.getMenu().findItem(R.id.nav_pushes);
                    MenuItem bonusPointItem = navigationView.getMenu().findItem(R.id.nav_bonusPoints);

                    if(userLoginCredentials.isLoggedIn()) {
                        loginItem.setVisible(false);
                        registerItem.setVisible(false);
                        logoutItem.setVisible(true);
                        pushOverviewItem.setVisible(true);
                        bonusPointItem.setVisible(true);
                        ViewHelper.makeVisibleOnUpdate(userNameTextView, userLoginCredentials.getUserName());
                    } else {
                        loginItem.setVisible(true);
                        registerItem.setVisible(true);
                        logoutItem.setVisible(false);
                        pushOverviewItem.setVisible(false);
                        userNameTextView.setVisibility(View.GONE);
                        bonusPointItem.setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * Reads connection information
     * from external storage, if available.
     */
    private void readConnectionInformation() {
        if (permissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Map<String, String> fileContent = storageHelper.readFile("connection.txt");
            String host = fileContent.get("host");
            String port = fileContent.get("port");
            String https = fileContent.get("https");


            if(host != null) {
                messageHelper.printLogMessage(getClass().getName(), "Host from custom configuration: " + host);
                connectionInformation.setHost(host);
            }
            if(port != null) {
                messageHelper.printLogMessage(getClass().getName(), "Port from custom configuration: " + port);
                connectionInformation.setPort(Integer.valueOf(port));
            }
            if(https != null) {
                messageHelper.printLogMessage(getClass().getName(), "Https from custom configuration: " + https);
                connectionInformation.setHttps(Boolean.valueOf(https));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                permissionHelper.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // create drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p/>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // evaluate which menu item was selected
        if (id == R.id.action_settings) {
            messageHelper.printToastMessage("Options Item selected: " + id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestaurantFragmentGetsActive() {
        setHeaderFooterVisibility(true, R.id.headerRestaurant);
    }

    @Override
    public void onRestaurantFragmentGetsInactive() {
        setHeaderFooterVisibility(false, R.id.headerRestaurant);
    }

    @Override
    public void onOfferFragmentGetsActive() {
        setHeaderFooterVisibility(true, R.id.headerOffer);
    }

    @Override
    public void onOfferFragmentGetsInactive() {
        setHeaderFooterVisibility(false, R.id.headerOffer);
    }

    @Override
    public void onPushOverviewFragmentGetsActive() {
        setHeaderFooterVisibility(true, R.id.headerPushOverview);
    }

    @Override
    public void onPushOverviewFragmentGetsInactive() {
        setHeaderFooterVisibility(false, R.id.headerPushOverview);
    }

    @Override
    public void onReservationFragmentGetsActive() {
        setHeaderFooterVisibility(true, R.id.headerOffer);
        setHeaderFooterVisibility(true, R.id.headerReservationOffer);
    }

    @Override
    public void onReservationFragmentGetsInactive() {
        setHeaderFooterVisibility(false, R.id.headerOffer);
        setHeaderFooterVisibility(false, R.id.headerReservationOffer);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Fragment to change to
        Fragment fragment = null;
        // Dialog fragment to show
        DialogFragment dialogFragment;

        switch (id) {
            case R.id.nav_search:
                // Handle the search action
                fragment = new MainFragment();
                break;
            case R.id.nav_login:
                // Handle the login action
                fragment = new UserLoginFragment();
                break;
            case R.id.nav_logout:
                // Handle the logout action
                dialogFragment = new UserLogoutDialogFragment();
                // show the logout dialog
                dialogFragment.show(getSupportFragmentManager(), "fragment_edit_name");
                break;
            case R.id.nav_register:
                // Handle the register action
                fragment = new UserRegistrationFragment();
                break;
            case R.id.nav_pushes:
                /*
                 * Handle the push notification overview (retrieve active push notifications).
                 * Uses direct request, because option is only visible if user is logged in.
                 */
                requestHelper.requestPushOverview(
                        RequestReason.SEARCH, userLoginCredentials.getUserName(),
                        userLoginCredentials.getPassword(), connectionInformation, this);
                break;
            case R.id.nav_favorites:
                messageHelper.printToastMessage(getString(R.string.nav_favourites));
                break;
            case R.id.nav_faq:
                openFindLunchPage(getString(R.string.url_path_faq));
                break;
            case R.id.nav_about:
                openFindLunchPage(getString(R.string.url_path_about));
                break;
            case R.id.nav_reset_pw:
                openFindLunchPage(getString(R.string.url_path_reset_pw));
                break;
            case R.id.nav_contact_support:
                emailHelper.composeEmail(new String[]{getString(R.string.email_contact)},
                        getString(R.string.text_support_email_subject),
                        getString(R.string.text_support_email_text));
                break;
            case R.id.nav_get_points:
                scanQRCode();
                break;
        }

        if(fragment != null) {
            changeFragment(fragment, true);
        }
        // find drawer by id
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * Open the QR-Scanner go get the points.
     */
    private void scanQRCode(){
        messageHelper.printToastMessage(getResources().getString(R.string.text_try_to_scann));
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        integrator.setOrientationLocked(false);
    }

    @Override
    public void onRestReservationConfirmFinished(Request<ReservationConfirmStatus> requestResponse) {
        if(requestResponse instanceof ReservationConfirmRequest){
            RequestResult requestResult = requestResponse.getRequestResult();

            if(requestResult == RequestResult.SUCCESS){
                messageHelper.printToastMessage(getResources().getString(R.string.text_get_bonuspoints_successful));
            }
            else{
                messageHelper.printToastMessage(getResources().getString(R.string.text_fail_to_get_bonus_points));
            }
            changeFragment(new MainFragment(), false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                messageHelper.printToastMessage(getResources().getString(R.string.text_scanner_closed));
            }
            else{
                //Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                Restaurant restaurant = new Restaurant();
                restaurant.setRestaurantUuid(result.getContents());
                requestHelper.requestReservationConfirm(this,restaurant,connectionInformation, userLoginCredentials.getUserName(), userLoginCredentials.getPassword());
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Opens the given path of the
     * FindLunch page in the default browser.
     *
     * @param path the path to open
     */
    private void openFindLunchPage(String path) {
        webHelper.openWebPage(
                webHelper.constructUrl(
                        connectionInformation.getHost(),
                        connectionInformation.getPort(),
                        path, connectionInformation.isHttps()));
    }

    /**
     * Sets header footer visibility.
     *
     * @param visible  the visibility
     * @param headerId the header id
     */
    private void setHeaderFooterVisibility(boolean visible, int headerId) {
        // find the header view by id
        View headerView = findViewById(headerId);
        if (headerView != null) {
            if(visible) {
                headerView.setVisibility(View.VISIBLE);
            } else {
                headerView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Sets header topic.
     *
     * @param locationString the location string to display at the header.
     */
    private void setHeaderTopic(String locationString) {
        // find the header location view by id
        TextView headerLocationView = (TextView) findViewById(R.id.headerRestaurantLocation);
        if (headerLocationView != null) {
            headerLocationView.setText(locationString);
        }
    }

    /**
     * Sets offer header topic.
     *
     * @param restaurantListPosition the restaurant list position of the restaurant
     *                               to display information at the header.
     */
    public void setOfferHeaderTopic(final int restaurantListPosition) {
        // find the required views by id
        TextView restaurantNameView = (TextView) findViewById(R.id.headerOfferRestaurantname);
        TextView offerTimesView = (TextView) findViewById(R.id.headerOfferRestaurantOfferTime);
        TextView restaurantType = (TextView) findViewById(R.id.headerOfferRestaurantType);
        TextView kitchenType = (TextView) findViewById(R.id.headerOfferKitchenType);
        TextView distanceView = (TextView) findViewById(R.id.headerOfferDistance);
        ImageView phoneView = (ImageView) findViewById(R.id.phoneImageView);
        ImageView emailView = (ImageView) findViewById(R.id.emailImageView);
        ImageView favouriteView = (ImageView) findViewById(R.id.favouriteImageView);

        if (restaurantNameView != null) {
            restaurantNameView.setText(restaurantContent.getName(restaurantListPosition));
        }
        if (offerTimesView != null) {
            offerTimesView.setText(restaurantContent.getOpeningTimes(restaurantListPosition));
        }
        ViewHelper.makeVisibleOnUpdate(restaurantType, restaurantContent.getRestaurantTypes(restaurantListPosition));
        ViewHelper.makeVisibleOnUpdate(kitchenType, restaurantContent.getKitchenTypes(restaurantListPosition));
        if (distanceView != null) {
            distanceView.setText(restaurantContent.getDistance(restaurantListPosition));
        }
        if (phoneView != null && restaurantContent.getPhone(restaurantListPosition) != null) {
            phoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneHelper.dialPhoneNumber(restaurantContent.getPhone(restaurantListPosition));
                }
            });
        }
        if (emailView != null && restaurantContent.getEmail(restaurantListPosition) != null) {
            emailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailHelper.composeEmail(new String[]{restaurantContent.getEmail(restaurantListPosition)}, "", "");
                }
            });
        }
        TextView actualPointsView = (TextView) findViewById(R.id.headerOfferActualPoints);
        if(userLoginCredentials.isLoggedIn()){
            if (actualPointsView != null) {
                actualPointsView.setText(restaurantContent.getActualPoints(restaurantListPosition));
                actualPointsView.setVisibility(View.VISIBLE);
            }
        } else{
            if(actualPointsView != null)
                actualPointsView.setVisibility(View.GONE);
        }

        setOfferHeaderFavouriteIcon(restaurantListPosition, favouriteView);
    }


    /**
     * Sets the header for the Reservation Fragment.
     * @param restaurantId The restaurant id
     * @param offerListPosition The position from the offer
     */
    public void setReservationHeaderTopic(final int restaurantId, final int offerListPosition){
        ImageView offerPhotoView = (ImageView) findViewById(R.id.offerPhoto);
        TextView offerTitleView = (TextView) findViewById(R.id.offerTitle);
        TextView offerDescriptionView = (TextView) findViewById(R.id.offerDescription);
        TextView offerPriceView = (TextView) findViewById(R.id.offerPrice);
        TextView offerPreparationTimeView = (TextView) findViewById(R.id.offerPreparationTime);
        TextView offerNeededPointsView = (TextView) findViewById(R.id.offerNeededPoints);

        OfferContent offerContent = getRestaurantContent().getOfferContent(restaurantId);

        if(offerPhotoView != null)
            offerPhotoView.setImageBitmap(offerContent.getDefaultPhoto(offerListPosition));

        if(offerTitleView != null)
            offerTitleView.setText(offerContent.getTitle(offerListPosition));

        if(offerDescriptionView != null)
            offerDescriptionView.setText(offerContent.getDescription(offerListPosition));

        if(offerPriceView != null)
            offerPriceView.setText(offerContent.getPrice(offerListPosition));

        if(offerPreparationTimeView != null)
            offerPreparationTimeView.setText(offerContent.getPreparationTime(offerListPosition));

        if(offerNeededPointsView != null)
            offerNeededPointsView.setText(offerContent.getNeededPoints(offerListPosition));
    }

    /**
     * Gets the Offer for the Reservation.
     * @param restaurantId The restaurant id
     * @param offerListPosition The position from the offer
     * @return The offer
     */
    public Offer getOfferForAReservation(int restaurantId, int offerListPosition){
        OfferContent offerContent = getRestaurantContent().getOfferContent(restaurantId);
        return offerContent.getOffer(offerListPosition);
    }

    /**
     *Updates the need points to for the reservation.
     */
    public void updatePointsToBuy(){
        final Button reservationPointsBuyView = (Button) findViewById(R.id.reservationPointsBuy);
        final TextView headerOfferActualPointsView = (TextView) findViewById(R.id.headerOfferActualPoints);
        final TextView offerNeededPointsView = (TextView) findViewById(R.id.offerNeededPoints);
        final TextView amountView = (TextView) findViewById(R.id.reservationAmount);


        int actualPoints = Integer.parseInt(headerOfferActualPointsView.getText().toString().replace("Punkte Stand: ",""));
        int neededPoints = Integer.parseInt(offerNeededPointsView.getText().toString().replace(" benötigte Punkte",""));
        int amount = Integer.parseInt(amountView.getText().toString());

        if (actualPoints >= neededPoints * amount) {
            reservationPointsBuyView.setVisibility(View.VISIBLE);
        } else {
            reservationPointsBuyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReservationFragmentInteraction(int restaurantId, int offerListPosition, Reservation reservation) {
        OfferContent offerContent = getRestaurantContent().getOfferContent(restaurantId);
        Offer offer = offerContent.getOffer(offerListPosition);
        reservation.setOffer(offer);
        requestHelper.requestReservationRegistration(this,reservation, connectionInformation,  userLoginCredentials.getUserName(), userLoginCredentials.getPassword());
    }

    @Override
    public void onReservationUsePointsFragmentInteraction(int restaurantId, int offerListPosition, Reservation reservation){
        OfferContent offerContent = getRestaurantContent().getOfferContent(restaurantId);
        Offer offer = offerContent.getOffer(offerListPosition);
        reservation.setOffer(offer);
        requestHelper.requestReservationRegistration(this,reservation, connectionInformation,  userLoginCredentials.getUserName(), userLoginCredentials.getPassword());
    }

    @Override
    public void refreshRestaurant(int restaurantId) {
        onRestaurantFragmentRefreshRestaurants();
        onOfferFragmentRefreshOffers(restaurantId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        }, 1000);
    }

    /**
     * Sets offer header favourite icon.
     *
     * @param restaurantListPosition the restaurant list position
     * @param favouriteView          the favourite view
     */
    private void setOfferHeaderFavouriteIcon(final int restaurantListPosition, ImageView favouriteView) {
        if (favouriteView != null) {
            Drawable drawable;

            // only show favourites icon if logged in
            if (!userLoginCredentials.isLoggedIn()) {
                favouriteView.setVisibility(View.GONE);
            } else {
                favouriteView.setVisibility(View.VISIBLE);

                // get the icon for favourite or not favourite
                if (restaurantContent.isFavorit(restaurantListPosition)) {
                    drawable = ContextCompat.getDrawable(this, R.drawable.ic_star_black);
                } else {
                    drawable = ContextCompat.getDrawable(this, R.drawable.ic_star_border_black);
                }
                // set the icon
                favouriteView.setImageDrawable(drawable);

                // dont't allow to unregister the favourite, if called with filter favourites only
                if (!restaurantContent.getFilter().isOnlyFavourites()) {
                    favouriteView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            registerFavourite(restaurantContent.getItems().get(restaurantListPosition).getId(),
                                    restaurantContent.getItems().get(restaurantListPosition).isFavorite());
                        }
                    });
                } else {
                    favouriteView.setOnClickListener(null);
                }
            }
        }
    }

    @Override
    public void setPushOverviewHeaderTopic(final boolean pushNotificationAvailable) {
        try {
            TextView pushOverviewTitle = (TextView) findViewById(R.id.headerPushOverviewTitle);
            if (pushNotificationAvailable) {
                pushOverviewTitle.setText(getResources().getString(R.string.nav_pushes));
            } else {
                pushOverviewTitle.setText(getResources().getString(R.string.text_not_found_push_overview));
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Change fragment.
     *
     * @param fragment       the fragment to change to
     * @param addToBackStack the fragment is added to the back stack if <code>true</code>
     */
    private void changeFragment(Fragment fragment, boolean addToBackStack) {
        changeFragment(fragment, null, addToBackStack);
    }

    /**
     * Change fragment.
     *
     * @param fragment       the fragment to change to
     * @param args           the bundle of arguments to add to the fragment
     * @param addToBackStack the fragment is added to the back stack if <code>true</code>
     */
    private void changeFragment(Fragment fragment, Bundle args, boolean addToBackStack) {
        // create an instance of fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // begin a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (args != null) {
            fragment.setArguments(args);
        }

        // replace the fragment by the new one
        fragmentTransaction.replace(R.id.flContent, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        // commit the fragment transaction
        fragmentTransaction.commit();
    }

    /**
     * Method that pops all element of
     * the back stack. After this got
     * invoked the user can't go back
     * anymore.
     */
    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void onMainFragmentInteraction(UserLocationContent contents) {
        keyboardHelper.closeKeyboard(this);
        userContent.setSearchLocationEntered(contents);
        requestHelper.requestAddresses(
                userContent.getSearchLocationEntered().getLocationName(), this);
    }

    @Override
    public void onRestaurantListFragmentInteraction(RecyclerView.ViewHolder holder) {
        // get the list position of the restaurant the user clicked on
        int restaurantPosition = holder.getAdapterPosition();
        selectRestaurant(restaurantPosition);
    }

    @Override
    public void onRestaurantListFragmentFavouriteInteraction(RecyclerView.ViewHolder holder) {
        int restaurantPosition = restaurantContent.getRestaurantUnfilteredPosition(
                restaurantContent.getItems().get(holder.getAdapterPosition()).getId());
        boolean isFavourite = restaurantContent.getItemsUnfiltered().get(restaurantPosition).isFavorite();
        int restaurantId = restaurantContent.getItems().get(holder.getAdapterPosition()).getId();

        registerFavourite(restaurantId, isFavourite);
    }

    @Override
    public void onRestaurantMapInfoWindowInteraction(int position) {
        selectRestaurant(position);
    }

    /**
     * Show the offers of a restaurant.
     *
     * @param restaurantPosition the position of the restaurant
     */
    private void selectRestaurant(int restaurantPosition) {
        // get the restaurant id (required for the REST-Request)
        int restaurantId = restaurantContent.getItems().get(restaurantPosition).getId();

        if (restaurantContent.getItems().get(restaurantPosition).getOffers() == null ||
                restaurantContent.getItems().get(restaurantPosition).getOffers().size() == 0) {
            requestHelper.requestOffers(RequestReason.SEARCH, restaurantId, connectionInformation, this);
        } else {
            // create a result fragment
            Fragment fragment = OfferItemFragment.newInstance(restaurantId);

            // Switch to result Fragment, provide information through args
            changeFragment(fragment, true);
        }
    }

    @Override
    public void onPushOverviewFragmentDeleteInteraction(RecyclerView.ViewHolder holder) {
        // get the pushNotificationId to delete and send request
        int pushNotificationId = getActivePushNotifications().get(holder.getAdapterPosition()).getId();
        requestHelper.requestPushDelete(
                RequestReason.SEARCH, userLoginCredentials.getUserName(),
                userLoginCredentials.getPassword(), pushNotificationId,
                connectionInformation, this);
    }

    @Override
    public void onOfferListFragmentInteraction(RecyclerView.ViewHolder holder, OfferContent mvlaues) {
        // method invoked, when the user clicks on an offer
        int selectedOffer = holder.getAdapterPosition();
        if(userLoginCredentials.isLoggedIn()) {
            ReservationFragment fragment = ReservationFragment.newInstance(mvlaues.getRestaurantId(), selectedOffer);
            changeFragment(fragment, true);
        }
        else {
            UserLoginFragment userLoginFragment = new UserLoginFragment();
            changeFragment(userLoginFragment, true);
        }
    }

    @Override
    public void onRestRestaurantFinished(Request<Restaurant> requestResponse) {

        // Add empty restaurants list if restaurant list of response is empty
        // an there are no data currently
        if(requestResponse.getResponseData() != null) {
            restaurantContent.setItems(requestResponse.getResponseData());
        } else {
            if(restaurantContent.getItems() == null) {
                restaurantContent.setItems(new ArrayList<Restaurant>());
            }
        }

        // notify the fragments through an intent to update
        sendIntentRestaurantRequestReturned();
        // get the result of the request
        RequestResult requestResult = requestResponse.getRequestResult();
        // get the detailed result of the request
        RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
        // get the reason for the request
        RequestReason requestReason = requestResponse.getRequestReason();

        if(requestResult == RequestResult.SUCCESS) {
            // change to result fragment only if there are actual results
            if (restaurantContent.getItems().size() > 0) {
                // create a result fragment
                Fragment fragment = new RestaurantFragment();
                Bundle args = new Bundle();

                if (requestReason != RequestReason.REFRESH) {
                    // Switch to result Fragment, provide information through args
                    changeFragment(fragment, args, true);
                }
            } else {
                Button button = (Button) findViewById(R.id.buttonFindOffers);
                messageHelper.printSnackbarMessage(
                        button, getResources().getString(R.string.text_no_restaurants_found));
            }
        } else if (requestResult == RequestResult.FAILED) {
            // print a toast message if the request failed due to unavailability of a connection
            switch (requestResultDetail) {
                case FAILED_NO_NETWORK_CONNECTION:
                    messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                    break;
                case FAILED_REST_REQUEST_FAILED:
                    messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                    break;
            }
        }
    }

    @Override
    public void onRestOfferFinished(Request<Offer> requestResponse) {
        // the offer request response
        OfferRequest offerRequestResponse;

        if (requestResponse instanceof OfferRequest) {
            offerRequestResponse = (OfferRequest) requestResponse;

            int restaurantPosition = restaurantContent.getRestaurantUnfilteredPosition(offerRequestResponse.getRestaurantId());

            // Add empty offer list if offer list of response is empty
            // an there are no data currently
            if(requestResponse.getResponseData() != null) {
                restaurantContent.getItemsUnfiltered().get(restaurantPosition).setOffers(requestResponse.getResponseData());
            } else {
                restaurantContent.getItemsUnfiltered().get(restaurantPosition).setOffers(new ArrayList<Offer>());
            }

            // notify the fragments through an intent to update
            sendIntentOfferRequestReturned();

            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
            // get the reason for the request
            RequestReason requestReason = requestResponse.getRequestReason();

            if(requestResult == RequestResult.SUCCESS) {
                // change to result fragment only if there are actual results
                if (restaurantContent.getItemsUnfiltered().get(restaurantPosition).getOffers().size() > 0) {
                    // create a result fragment
                    Fragment fragment = OfferItemFragment.newInstance(offerRequestResponse.getRestaurantId());

                    if (requestReason != RequestReason.REFRESH) {
                        // Switch to result Fragment, provide information through args
                        changeFragment(fragment, true);
                    }
                } else {
                    // display the message at the bottom
                    View button = findViewById(R.id.flContent);
                    messageHelper.printSnackbarMessage(
                            button, getResources().getString(R.string.text_no_offers_found));
                }
            } else if (requestResult == RequestResult.FAILED) {
                // print a toast message if the request failed due to unavailability of a connection
                switch (requestResultDetail) {
                    case FAILED_NO_NETWORK_CONNECTION:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                        break;
                    case FAILED_REST_REQUEST_FAILED:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                        break;
                }
            }
        }
    }

    @Override
    public void onRestAddressFinished(Request<GoogleGeoCodeResponse> requestResponse) {

        // get the result of the request
        RequestResult requestResult = requestResponse.getRequestResult();
        // get the detailed result of the request
        RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();

        if(requestResult == RequestResult.SUCCESS) {
            if (requestHelper.checkAndUpdateCustomerSearchLocation(
                    requestResponse, userContent)) {
                // Add the formular information to the header
                setHeaderTopic(userContent.getHeader());

                requestHelper.requestRestaurants(
                        RequestReason.SEARCH, userContent.getLongitude(),
                        userContent.getLatitude(), userContent.getDistance(),
                        userLoginCredentials.getUserName(), userLoginCredentials.getPassword(),
                        connectionInformation, this);
            } else {
                messageHelper.printToastMessage(getResources().getString(R.string.text_address_information_wrong));
            }
        } else if (requestResult == RequestResult.FAILED) {
            // print a toast message if the request failed due to unavailability of a connection
            switch (requestResultDetail) {
                case FAILED_NO_NETWORK_CONNECTION:
                    messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                    break;
                case FAILED_REST_REQUEST_FAILED:
                    messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                    break;
            }
        }
    }

    @Override
    public void onRestReservationRegistrationFinished(Request<ReservationRegistrationStatus> requestResponse) {
        //ReservationRegistrationRequest reservationRegistrationRequestResponse;

        if(requestResponse instanceof ReservationRegistrationRequest){
            RequestResult requestResult = requestResponse.getRequestResult();
            if(requestResult == RequestResult.SUCCESS){
                messageHelper.printToastMessage(getResources().getString(R.string.text_reservation_successful));
            }
            else{
                messageHelper.printToastMessage(getResources().getString(R.string.text_reservation_fail));
            }
        }
    }

    @Override
    public void onRestUserRegistrationFinished(Request<UserRegistrationStatus> requestResponse) {
        // the user registration request response
        UserRegistrationRequest userRegistrationRequestResponse;

        if (requestResponse instanceof UserRegistrationRequest) {
            userRegistrationRequestResponse = (UserRegistrationRequest) requestResponse;

            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
            // display the message at the bottom
            View button = findViewById(R.id.flContent);
            // get the body of the response
            UserRegistrationStatus registrationStatus = requestResponse.getResponseData() != null &&
                    requestResponse.getResponseData().size() > 0 ?
                    requestResponse.getResponseData().get(0) : null;

            if(requestResult == RequestResult.SUCCESS) {

                if(registrationStatus != null &&
                        registrationStatus == UserRegistrationStatus.SUCCESS) {
                    // message registration successfully
                    // print the message
                    messageHelper.printSnackbarMessage(
                            button, getResources().getString(
                                    R.string.text_registration_successfully));
                    // open login fragment with the login credentials
                    changeFragment(UserLoginFragment.newInstance(
                            userRegistrationRequestResponse.getUser().getUsername(),
                            userRegistrationRequestResponse.getUser().getPassword()), true);
                }

            } else if (requestResult == RequestResult.FAILED) {

                if (requestResultDetail != null) {
                    // print a toast message if the request failed due to unavailability of a connection
                    switch (requestResultDetail) {
                        case FAILED_NO_NETWORK_CONNECTION:
                            messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                            break;
                        case FAILED_REST_REQUEST_FAILED:
                            messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                            break;
                    }
                }

                if (registrationStatus != null) {
                    switch (registrationStatus) {
                        case FAILED_USERNAME_INVALID:
                            // message registration failed username invalid
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_registration_failed_username_invalid));
                            break;
                        case FAILED_PASSWORD_INVALID:
                            // message registration failed password invalid
                            messageHelper.printMultilineSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_registration_failed_password_invalid));
                            break;
                        case FAILED_USERNAME_EXISTS:
                            // message registration failed user exists
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_registration_failed_username_exists));
                            break;
                        case FAILED_CAPTCHA_WRONG:
                            // message captcha was not solved correctly
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_registration_failed_captcha_invalid));
                            break;

                    }
                }
            }
        }
    }

    @Override
    public void onRestUserLoginFinished(Request<UserLoginStatus> requestResponse) {
        // the user login request response
        UserLoginRequest userLoginRequestResponse;

        if (requestResponse instanceof UserLoginRequest) {
            userLoginRequestResponse = (UserLoginRequest) requestResponse;

            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
            // display the message at the bottom
            View button = findViewById(R.id.flContent);
            // get the body of the response
            UserLoginStatus userLoginStatus = requestResponse.getResponseData() != null &&
                    requestResponse.getResponseData().size() > 0 ?
                    requestResponse.getResponseData().get(0) : null;

            Log.e("onRestUserLoginFinished", requestResult.toString());


            if(requestResult == RequestResult.SUCCESS) {

                if(userLoginStatus != null &&
                        userLoginStatus == UserLoginStatus.SUCCESS) {
                    // log the user in
                    userLoginCredentials.persistCredentials(
                            userLoginRequestResponse.getUserName(),
                            userLoginRequestResponse.getPassword());
                    // check if the user is logged in
                    if (userLoginCredentials.isLoggedIn()) {
                        // message login successfully
                        messageHelper.printSnackbarMessage(
                                button, getResources().getString(R.string.text_login_successful));


                        // Obtain a FCM-Token / ADM-Token
                        IntentFilter fbFilter = new IntentFilter(FirebaseTokenReceiver.FCM_TOKEN_MESSAGE);
                        IntentFilter admFilter = new IntentFilter(AmazonPushListenerService.ADM_TOKEN_MESSAGE);

                        LocalBroadcastManager.getInstance(this).registerReceiver(multiTokenBroadcastReceiver, fbFilter);
                        LocalBroadcastManager.getInstance(this).registerReceiver(multiTokenBroadcastReceiver, admFilter);


                        if(android.os.Build.MANUFACTURER.equals("Amazon") && (android.os.Build.MODEL.equals("Kindle Fire") || android.os.Build.MODEL.startsWith("Fire") || android.os.Build.MODEL.startsWith("KF"))) {
                            Log.e("DEBUG", "ADM Device found, using AD Messaging " + android.os.Build.MANUFACTURER + android.os.Build.MODEL);

                            //Start Amazon push listener service
                            Intent amaIDServ = new Intent(this, AmazonPushListenerService.class);
                            startService(amaIDServ);

                            //Start Amazon registration handler
                            amazonRegHandler = new AmazonSnsRegHandler(this);



                        } else {
                            Log.e("DEBUG", "Pure Android found, using Google Firebase Messaging " + android.os.Build.MANUFACTURER + android.os.Build.MODEL);

                            //Start Firebase token receiver
                            Intent intent = new Intent(this, FirebaseTokenReceiver.class);
                            startService(intent);


                            //If no FCM token available, force onTokenRefresh() at FirebaseTokenReceiver
                            if(userLoginCredentials.getFcmToken() == null) {
                                Intent delToken = new Intent(this, FirebaseTokenRequest.class);
                                startService(delToken);
                            }
                        }


                        // open the main fragment
                        changeFragment(new MainFragment(), true);
                        checkLogin();

                    }
                }
            } else if (requestResult == RequestResult.FAILED) {
                if (requestResultDetail != null) {
                    // print a toast message if the request failed due to unavailability of a connection
                    switch (requestResultDetail) {
                        case FAILED_NO_NETWORK_CONNECTION:
                            messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                            break;
                        case FAILED_REST_REQUEST_FAILED:
                            messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                            break;
                    }
                }

                if(userLoginStatus != null) {
                    switch (userLoginStatus) {
                        case FAILED_UNAUTHORIZED:
                            // message login failed user unauthorized
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_login_failed_uauthorized));
                            break;
                        case FAILED:
                            // message login failed
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_login_failed));
                            break;
                    }
                }
            }
        }
    }



    /**
     * Perform initial push for database update.
     * Webserver ignores initial push, using for token update of current service ADM/FCM.
     * New/changed device requires database update, performing update by dummy push registration.
     * Filtered at: PushNotificationRestController on webserver.
     */
    public void performInitialPush () {

        showPushRegToastMsgs = false;

        //Dummy registration data for webserver database update.
        String pushTitle = "INIT_PUSH";
        boolean[] selectedWeekdays = {true, false, false, false, false, false, false};
        List<DayOfWeek> daysOfWeek = getDateHelper().convertSelectionToWeekDays(selectedWeekdays);
        List<KitchenType> kitchenTypes = new ArrayList<>();

        Log.e("DEBUG", userLoginCredentials.getFcmToken());
        Log.e("DEBUG", userLoginCredentials.getSnsToken());

        PushNotification pushNotification = new PushNotification(userLoginCredentials.getFcmToken(), userLoginCredentials.getSnsToken(), pushTitle, userContent.getLatitude(), userContent.getLongitude(), userContent.getDistance(),
                new User(userLoginCredentials.getUserName(), userLoginCredentials.getPassword(), new Captcha()), daysOfWeek, kitchenTypes);
        requestHelper.requestPushRegistration(
                RequestReason.SEARCH, userLoginCredentials.getUserName(),userLoginCredentials.getPassword(), pushNotification, connectionInformation, this);

    }

    @Override
    public void onRestFavouriteRegistrationFinished(Request<FavouriteRegistrationStatus> requestResponse) {
        // the favourite request response
        FavouriteRequest favouriteRequestResponse;

        if (requestResponse instanceof FavouriteRequest) {
            favouriteRequestResponse = (FavouriteRequest) requestResponse;

            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
            // display the message at the bottom
            View button = findViewById(R.id.flContent);
            // get the body of the response
            FavouriteRegistrationStatus favouriteRegistrationStatus = requestResponse.getResponseData() != null &&
                    requestResponse.getResponseData().size() > 0 ?
                    requestResponse.getResponseData().get(0) : null;

            if(requestResult == RequestResult.SUCCESS &&
                    favouriteRegistrationStatus == FavouriteRegistrationStatus.SUCCESS) {
                // set the favourite information for the restaurant
                int restaurantPosition = restaurantContent.getRestaurantUnfilteredPosition(favouriteRequestResponse.getRestaurantId());
                restaurantContent.getItemsUnfiltered().get(restaurantPosition).setIsFavorite(favouriteRequestResponse.isRegistration());
                sendIntentRestaurantRequestReturned();
                sendIntentOfferRequestReturned();
            } else if (requestResult == RequestResult.FAILED) {
                if (requestResultDetail != null) {
                    // print a toast message if the request failed due to unavailability of a connection
                    switch (requestResultDetail) {
                        case FAILED_NO_NETWORK_CONNECTION:
                            messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                            break;
                        case FAILED_REST_REQUEST_FAILED:
                            messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                            break;
                    }
                }

                if(favouriteRegistrationStatus != null) {
                    switch (favouriteRegistrationStatus) {
                        case FAILED_UNAUTHORIZED:
                            // message favourite registration failed user unauthorized
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_login_failed_uauthorized));
                            break;
                        case FAILED_INVALID_RESTAURANT_ID:
                            // message favourite registration failed, restaurant id unknown
                            messageHelper.printSnackbarMessage(
                                    button, getResources().getString(
                                            R.string.text_favourite_registration_failed_restaurant_id_unknown));
                            break;
                    }
                }
            }
        }
    }


    /**
     * No popup at initial database update push.
     */
    @Override
    public void onRestPushNotificationRegistrationFinished(Request<PushNotificationRegistrationStatus> requestResponse) {

        try {
            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
            // get the body of the response
            PushNotificationRegistrationStatus pushNotificationRegistrationStatus = requestResponse.getResponseData() != null &&
                    requestResponse.getResponseData().size() > 0 ?
                    requestResponse.getResponseData().get(0) : null;

            if (requestResult == RequestResult.SUCCESS &&
                    pushNotificationRegistrationStatus == PushNotificationRegistrationStatus.SUCCESS) {

                //Do not show first push registration
                if(showPushRegToastMsgs) {
                    // displays a message about the creation of the push notification
                    messageHelper.printToastMessage(getResources().getString(R.string.text_finished_push_registration));
                }
                //Now message display at device
                showPushRegToastMsgs = true;

            } else if (requestResult == RequestResult.FAILED && requestResultDetail != null) {
                // print a toast message if the request failed due to unavailability of a connection
                switch (requestResultDetail) {
                    case FAILED_NO_NETWORK_CONNECTION:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                        break;
                    case FAILED_REST_REQUEST_FAILED:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                        break;
                }
            } else {
                messageHelper.printToastMessage(getResources().getString(R.string.text_error_push_registration));
            }
        } catch (Exception e) {
            messageHelper.printToastMessage(getResources().getString(R.string.text_error_push_registration) + ": " + e.getCause());
        }
    }

    @Override
    public void onRestPushNotificationOverviewFinished(Request<PushNotification> requestResponse) {

        if(requestResponse.getResponseData() != null) {
            activePushNotifications = requestResponse.getResponseData();
        }

        try {
            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();

            if (requestResult == RequestResult.SUCCESS) {
                // create a fragment to display the push overview
                Fragment fragment = new PushOverviewFragment();
                Bundle args = new Bundle();
                changeFragment(fragment, args, true);
            } else if (requestResult == RequestResult.FAILED && requestResultDetail != null) {
                // print a toast message if the request failed due to unavailability of a connection
                switch (requestResultDetail) {
                    case FAILED_NO_NETWORK_CONNECTION:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                        break;
                    case FAILED_REST_REQUEST_FAILED:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                        break;
                }
            } else {
                messageHelper.printToastMessage(getResources().getString(R.string.text_error_push_overview));
            }
        } catch (Exception e) {
            messageHelper.printToastMessage(getResources().getString(R.string.text_error_push_overview) + ": " + e.getCause());
        }
    }

    @Override
    public void onRestPushNotificationDeleteFinished(Request<PushNotificationDeleteStatus> requestResponse) {
        try {
            // get the result of the request
            RequestResult requestResult = requestResponse.getRequestResult();
            // get the detailed result of the request
            RequestResultDetail requestResultDetail = requestResponse.getRequestResultDetail();
            // get the body of the response
            PushNotificationDeleteStatus pushNotificationDeleteStatus = requestResponse.getResponseData() != null &&
                    requestResponse.getResponseData().size() > 0 ?
                    requestResponse.getResponseData().get(0) : null;

            if (requestResult == RequestResult.SUCCESS &&
                    pushNotificationDeleteStatus == PushNotificationDeleteStatus.SUCCESS) {
                // reload and show push notifications.
                requestHelper.requestPushOverview(
                        RequestReason.SEARCH, userLoginCredentials.getUserName(),userLoginCredentials.getPassword(), connectionInformation, this);

            } else if (requestResult == RequestResult.FAILED && requestResultDetail != null) {
                // print a toast message if the request failed due to unavailability of a connection
                switch (requestResultDetail) {
                    case FAILED_NO_NETWORK_CONNECTION:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_no_network_connection));
                        break;
                    case FAILED_REST_REQUEST_FAILED:
                        messageHelper.printToastMessage(getResources().getString(R.string.text_rest_request_failed));
                        break;
                }
            } else {
                messageHelper.printToastMessage(getResources().getString(R.string.text_error_push_delete));
            }
        } catch (Exception e) {
            messageHelper.printToastMessage(getResources().getString(R.string.text_error_push_delete) + ": " + e.getCause());
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.MAIN_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    readConnectionInformation();
                }
            }
        }
    }

    /**
     * Send an intent to notify other fragments that the
     * restaurant request has returned.
     */
    private void sendIntentRestaurantRequestReturned() {
        // create the intent to send
        Intent intent = new Intent(INTENT_REST_RESTAURANTS);
        // send the intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Send an intent to notify other fragments that the
     * offer request has returned.
     */
    private void sendIntentOfferRequestReturned() {
        // create the intent to send
        Intent intent = new Intent(INTENT_REST_OFFERS);
        // send the intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Send an intent to notify other fragments that the
     * restaurant request has returned.
     */
    @Override
    public void onFilterDialogPositiveClick() {
        // notify the fragments through an intent to update
        sendIntentRestaurantRequestReturned();
        sendIntentOfferRequestReturned();
    }




    /**
     * Both tokens possible at userLoginCredentials.
     * Register push to webserver database.
     * Called from "new push message".
     */
    @Override
    public void onRestaurantFragmentPushNotificationRegistration() {

        //Token
        String fcmToken = userLoginCredentials.getFcmToken();
        String snsToken = userLoginCredentials.getSnsToken();

        //Log info
        Log.e("Main.onRestaurantFragmentPushNotificationRegistration()", "Curr SNS Token " + snsToken);
        Log.e("Main.onRestaurantFragmentPushNotificationRegistration()", "Curr FCM Token " + fcmToken);


        if ((fcmToken !=  null && !fcmToken.equals(NOT_AVAILABLE)) || (snsToken != null && !snsToken.equals(NOT_AVAILABLE))) {
            // open push notification registration dialog
            PushRegistrationDialogFragment pushRegistrationDialogFragment = PushRegistrationDialogFragment.newInstance();
            pushRegistrationDialogFragment.show(getSupportFragmentManager(), "fragment_edit_name");
        } else {
            messageHelper.printToastMessage(getString(R.string.text_login_push_dialog));
        }

    }

    /**
     * Performing push registration.
     * Called from PushRegistrationDialogFragment.PushRegistrationDialogInteractionListener
     */
    @Override
    public void onPushRegistrationDialogPositiveClick(boolean[] selectedWeekdays, String pushTitle) {

        // convert days of week
        List<DayOfWeek> daysOfWeek = getDateHelper().convertSelectionToWeekDays(selectedWeekdays);

        // get filtered kitchenTypes
        List<KitchenType> kitchenTypes = new ArrayList<>();
        if(getRestaurantContent().getFilter().getKitchenTypesSelected() != null &&
                getRestaurantContent().getFilter().getKitchenTypesSelected().size() > 0) {
            kitchenTypes = getRestaurantContent().getFilter().getKitchenTypesSelected();
        }


        // 05.12.2016
        // Generation of new push notification
        PushNotification pushNotification = new PushNotification(userLoginCredentials.getFcmToken(), userLoginCredentials.getSnsToken(), pushTitle, userContent.getLatitude(), userContent.getLongitude(), userContent.getDistance(),
                new User(userLoginCredentials.getUserName(), userLoginCredentials.getPassword(), new Captcha()), daysOfWeek, kitchenTypes);

        requestHelper.requestPushRegistration(
                RequestReason.SEARCH, userLoginCredentials.getUserName(),userLoginCredentials.getPassword(), pushNotification, connectionInformation, this);

    }

    /**
     * Method that opens the filter dialog fragment.
     * @param itemsEnabled if <code>true</code>, the items
     *                     for the selection of the kitchen type
     *                     as well as the selection of the restaurants
     *                     (all restaurants / only favourites) are enabled
     */
    private void openFilterDialogFragment(boolean itemsEnabled) {
        // instantiate the fragment of the filter dialog
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance(itemsEnabled);
        // show the filter dialog
        filterDialogFragment.show(getSupportFragmentManager(), "fragment_edit_name");
    }

    @Override
    public void onUserRegistrationFragmentInteraction(UserRegistrationContent contents) {
        keyboardHelper.closeKeyboard(this);
        User user = new User(contents.getUserName(), contents.getPassword(), contents.getCaptcha());
        requestHelper.requestUserRegistration(
                user,
                connectionInformation,
                this);
    }

    @Override
    public void onUserLoginFragmentLoginInteraction(UserLoginContent contents) {
        keyboardHelper.closeKeyboard(this);
        requestHelper.requestUserLogin(
                contents.getUserName(),
                contents.getPassword(),
                connectionInformation,
                this);
    }

    @Override
    public void onUserLoginFragmentRegisterInteraction() {
        // change from login fragment to registration fragment
        changeFragment(new UserRegistrationFragment(), true);
    }

    @Override
    public void onUserLogoutDialogPositiveClick() {
        // display the message at the bottom
        View button = findViewById(R.id.flContent);

        if (!userLoginCredentials.isLoggedIn()) {
            // message logout failed
            messageHelper.printSnackbarMessage(
                    button, getResources().getString(R.string.text_logout_failed_not_logged_in));
        } else {
            // delete the login credentials and change the login state
            userLoginCredentials.deleteCredentials();

            // message logout successfully
            messageHelper.printSnackbarMessage(
                    button, getResources().getString(R.string.text_logout_successful));
            clearBackStack();
            // open the main fragment
            changeFragment(new MainFragment(), true);
            checkLogin();
        }
    }

    @Override
    public void onUserRegistrationFragmentOpenLinkInteraction(String path) {
        openFindLunchPage(path);
    }

    /**
     * Method that request the registration or unregistration
     * of a restaurant as a favourite.
     * @param restaurantId the id of the restaurant
     * @param isFavourite <code>true</code> if the restaurant is currently a favourite
     */
    private void registerFavourite(int restaurantId, boolean isFavourite) {
        if (isFavourite) {
            requestHelper.requestFavouriteUnregistration(
                    userLoginCredentials.getUserName(),
                    userLoginCredentials.getPassword(),
                    restaurantId,
                    connectionInformation, this);
        } else {
            requestHelper.requestFavouriteRegistration(
                    userLoginCredentials.getUserName(),
                    userLoginCredentials.getPassword(),
                    restaurantId,
                    connectionInformation, this);
        }
    }

    @Override
    public void onRestaurantFragmentRefreshRestaurants() {
        // refresh the restaurants
        requestHelper.requestRestaurants(
                RequestReason.REFRESH, userContent.getLongitude(),
                userContent.getLatitude(), userContent.getDistance(),
                userLoginCredentials.getUserName(), userLoginCredentials.getPassword(),
                connectionInformation, this);
    }

    @Override
    public void onOfferFragmentRefreshOffers(int restaurantId) {
        // refresh the offers
        requestHelper.requestOffers(RequestReason.REFRESH, restaurantId,
                connectionInformation, this);
    }

    @Override
    public void onOfferFragmentFilterOffers() {
        // open the filter dialog fragment
        openFilterDialogFragment(false);
    }

    @Override
    public void onRestaurantFragmentFilterRestaurants() {
        // open the filter dialog fragment
        openFilterDialogFragment(true);
    }

}
