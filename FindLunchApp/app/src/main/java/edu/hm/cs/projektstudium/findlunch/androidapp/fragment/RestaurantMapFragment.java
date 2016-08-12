package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.activity.MainActivity;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantAndLocationProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.RestaurantContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.UserLoginCredentialsProvider;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.ViewHelper;


/**
 * A {@link Fragment} that shows the restaurants on a map.
 * <p>
 * Activities that contain this fragment must implement the
 * {@link RestaurantMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RestaurantMapFragment extends Fragment
        implements OnMapReadyCallback{

    /**
     * The listener whose implementation of {@link RestaurantMapFragment.OnFragmentInteractionListener}
     * gets called on interaction.
     */
    private OnFragmentInteractionListener mListener;

    /**
     * The support map fragment that contains the map.
     */
    private SupportMapFragment mSupportMapFragment;
    /**
     * The Google map.
     */
    private GoogleMap googleMap;
    /**
     * The Info window adapter
     * that is opened after a click
     * on the info window of a marker.
     */
    private GoogleMap.InfoWindowAdapter infoWindowAdapter;
    /**
     * The list of markers
     * that are shown on the map.
     * This is required to obtain the
     * marker the user clicked on.
     */
    private List<Marker> markerList;

    /**
     * The Root view.
     */
    private View rootView;

    /**
     * BroadcastReceiver that receives intents when
     * the REST-API for the restaurants was called successfully.
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMarkerOnMap();
        }
    };

    /**
     * Instantiates a new Restaurant map fragment.
     */
    public RestaurantMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create a info window adapter
        infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // get the index of the marker the user clicked on
                final int markerIndex = getMarkerIndex(marker);

                if (markerIndex != -1) {
                    // Getting view from the layout file info_window_layout
                    final ViewGroup nullParent = null;
                    View v = getActivity().getLayoutInflater().inflate(R.layout.information_window, nullParent);

                    // find the views by id
                    TextView infoName = (TextView) v.findViewById(R.id.info_window_name);
                    TextView infoOfferTime = (TextView) v.findViewById(R.id.info_window_offerTime);
                    TextView infoRestaurantType = (TextView) v.findViewById(R.id.info_window_restaurantType);
                    TextView infoKitchenType = (TextView) v.findViewById(R.id.info_window_kitchenType);
                    TextView infoDistance = (TextView) v.findViewById(R.id.info_window_distance);
                    ImageView infoFavourite = (ImageView) v.findViewById(R.id.info_window_favourite);

                    // get the restaurant content
                    RestaurantContent restaurantContent = mListener.getRestaurantContent();

                    // fill the info window with
                    // the information of the marker
                    infoName.setText(restaurantContent.getName(markerIndex));
                    infoOfferTime.setText(restaurantContent.getOpeningTimes(markerIndex));
                    ViewHelper.makeVisibleOnUpdate(infoRestaurantType, restaurantContent.getRestaurantTypes(markerIndex));
                    ViewHelper.makeVisibleOnUpdate(infoKitchenType, restaurantContent.getKitchenTypes(markerIndex));
                    infoDistance.setText(restaurantContent.getDistance(markerIndex));

                    if (infoFavourite != null) {
                        Drawable drawable;

                        // only show favourites icon if logged in
                        if (!mListener.getUserLoginCredentials().isLoggedIn()) {
                            infoFavourite.setVisibility(View.GONE);
                        } else {
                            infoFavourite.setVisibility(View.VISIBLE);

                            // get the icon for favourite or not favourite
                            if (restaurantContent.isFavorit(markerIndex)) {
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_black);
                            } else {
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_border_black);
                            }
                            // set the icon
                            infoFavourite.setImageDrawable(drawable);
                        }
                    }

                    // Returning the view containing InfoWindow contents
                    return v;
                }

                return null;
            }
        };
    }

    /**
     * Returns the index of the
     * marker.
     *
     * @param marker the marker
     * @return the index of the marker
     */
    private int getMarkerIndex(Marker marker) {
        return markerList.indexOf(marker);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpMapIfNeeded();
    }

    /**
     * Sets up map if needed.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mSupportMapFragment == null) {
            mSupportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout only for the first time the view is created
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_restaurant_map, container, false);
        }
        if(googleMap != null) {
            setMarkerOnMap();
        }
        return rootView;
    }

    /**
     * Method that is called
     * when the info window of a marker
     * gets clicked.
     *
     * @param position the position
     */
    private void onInfoWindowClicked(int position) {
        if(mListener != null) {
            mListener.onRestaurantMapInfoWindowInteraction(position);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            loadMap(googleMap);

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    onInfoWindowClicked(getMarkerIndex(marker));
                }
            });
        }
    }

    /**
     * Method that loads the map.
     *
     * @param googleMap the google map
     */
    private void loadMap(GoogleMap googleMap) {
        if (googleMap != null) {
            this.googleMap = googleMap;
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            // set the custom info window adapter
            googleMap.setInfoWindowAdapter(infoWindowAdapter);
            setMarkerOnMap();
        }
    }

    /**
     * Method that sets the markers on the map.
     */
    private void setMarkerOnMap() {
        // get the restaurant content
        RestaurantContent restaurantContent = mListener.getRestaurantContent();
        // get the user content
        UserContent userContent = mListener.getUserContent();

        // create a position from latitude and longitude
        // of the user content (search location of the user input)
        LatLng customerSearchLocation = new LatLng(userContent.getLatitude(), userContent.getLongitude());

        googleMap.clear();

        // add a marker for the customer search location
        googleMap.addMarker(new MarkerOptions()
                .position(customerSearchLocation)
                .title(getContext().getResources().getString(R.string.text_searchaddress))
                .snippet(userContent.getHeader())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if (restaurantContent != null) {
            // create a new list of markers
            markerList = new ArrayList<>();

            for(int i = 0; i < restaurantContent.size(); i++) {
                markerList.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(restaurantContent.getLatitude(i), restaurantContent.getLongitude(i)))
                        .title(restaurantContent.getName(i))
                        .snippet(restaurantContent.getOpeningTimes(i))));
            }
        }
        // draw a circle around the customer search location
        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(customerSearchLocation)
                .radius(userContent.getDistance())
                .fillColor(ContextCompat.getColor(getContext(),R.color.circleArea))
                .strokeWidth(4)
                .strokeColor(ContextCompat.getColor(getContext(),R.color.circleBorder)));

        // calculate the boundaries to show
        LatLngBounds bounds = new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(circle.getCenter(), circle.getRadius(), 0)).
                include(SphericalUtil.computeOffset(circle.getCenter(), circle.getRadius(), 90)).
                include(SphericalUtil.computeOffset(circle.getCenter(), circle.getRadius(), 180)).
                include(SphericalUtil.computeOffset(circle.getCenter(), circle.getRadius(), 270)).build();
        // move the camera to show the calculated boundaries
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
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
    public interface OnFragmentInteractionListener extends RestaurantAndLocationProvider, UserLoginCredentialsProvider {
        /**
         * Method that is invoked
         * in {@link RestaurantMapFragment}
         * when the info windows of a marker gets clicked.
         *
         * @param position the position of the restaurant
         */
        void onRestaurantMapInfoWindowInteraction(int position);
    }
}
