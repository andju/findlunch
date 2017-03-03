package edu.hm.cs.projektstudium.findlunch.androidapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Locale;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.androidapp.view.DebouncedOnClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends Fragment {

    private static final String ARG_RESTAURANT_ID = "restaurant-id";
    private static final String ARG_OFFERLIST_POSITION = "offerList-position";

    private int mRestaurantId;
    private int mOfferListPosition;
    private OnReservationFragmentInteractionListener mListener;

    public ReservationFragment() {
        // Required empty public constructor
    }

    public static ReservationFragment newInstance(int restaurantId, int offerListPosition) {
        // create a new instance of the fragment
        ReservationFragment fragment = new ReservationFragment();
        // create a bundle for the arguments
        Bundle args = new Bundle();
        args.putInt(ARG_RESTAURANT_ID, restaurantId);
        args.putInt(ARG_OFFERLIST_POSITION, offerListPosition);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRestaurantId = getArguments().getInt(ARG_RESTAURANT_ID);
            mOfferListPosition = getArguments().getInt(ARG_OFFERLIST_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservation, container, false);//fragment_offer_item

        mListener.onReservationFragmentGetsActive();
        setReservationHeader();
        //mListener.setReservationAmountAndPrice(view,mRestaurantId,mOfferListPosition);
        setReservationAmountAndPrice(view);
        //updateReservationPoitnsBuy(view);

        Button buttonAddReservaition =  (Button) view.findViewById(R.id.reservationBuy);
        Button buttonUsePointsReservation = (Button) view.findViewById(R.id.reservationPointsBuy);

        buttonAddReservaition.setOnClickListener(new DebouncedOnClickListener(1500) {
            @Override
            public void onDebouncedClick(View v) {
                View view = getView();
                if(view != null) {
                    Reservation reservation = new Reservation();
                    TextView amountView = (TextView) view.findViewById(R.id.reservationAmount);
                    TextView reservationPriceView = (TextView) view.findViewById(R.id.reservationPrice);
                    TextView reservationDonationView = (TextView) view.findViewById(R.id.reservationDonation);

                    //set of totalPrice is not necessary
                    reservation.setAmount(Integer.parseInt(amountView.getText().toString()));
                    reservation.setDonation(Float.parseFloat(reservationDonationView.getText().toString()));
                    reservation.setTotalPrice(Float.parseFloat(reservationPriceView.getText().toString()));
                    onReservationAddButtonPressed(reservation);
                    mListener.refreshRestaurant(mRestaurantId);
                }
            }
        });

        buttonUsePointsReservation.setOnClickListener(new DebouncedOnClickListener(1500) {
            @Override
            public void onDebouncedClick(View v) {
                View view = getView();
                if(view != null){
                    Reservation reservation = new Reservation();
                    reservation.setUsedPoints(true);
                    TextView amountView = (TextView) view.findViewById(R.id.reservationAmount);
                    TextView reservationPriceView = (TextView) view.findViewById(R.id.reservationPrice);
                    TextView reservationDonationView = (TextView) view.findViewById(R.id.reservationDonation);

                    //set of totalPrice is not necessary
                    reservation.setAmount(Integer.parseInt(amountView.getText().toString()));
                    reservation.setDonation(Float.parseFloat(reservationDonationView.getText().toString()));
                    reservation.setTotalPrice(Float.parseFloat(reservationPriceView.getText().toString()));
                    onReservationUsePointsButtonPressed(reservation);
                    mListener.refreshRestaurant(mRestaurantId);
                }
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Sets the Header of the fragment.
     */
    private void setReservationHeader(){
        mListener.setReservationHeaderTopic(mRestaurantId,mOfferListPosition);
    }

    /**
     * Start a request to register a reservation.
     * @param reservation The reservation
     */
    private void onReservationAddButtonPressed(Reservation reservation){
        View view = getView();
        if(view != null){

            mListener.onReservationFragmentInteraction(mRestaurantId,mOfferListPosition,reservation);
        }
    }

    /**
     * Start a request to register a reservation by using points.
     * @param reservation The reservation
     */
    private void onReservationUsePointsButtonPressed(Reservation reservation){
        View view = getView();
        if(view != null){
            mListener.onReservationUsePointsFragmentInteraction(mRestaurantId,mOfferListPosition,reservation);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReservationFragmentInteractionListener) {
            mListener = (OnReservationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Notify the activity that the restaurant and offer fragment gets inactive
        mListener.onReservationFragmentGetsInactive();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets the amount and price for this fragment.
     * @param view The view
     */
    private void setReservationAmountAndPrice(View view){
        Offer offer = mListener.getOfferForAReservation(mRestaurantId, mOfferListPosition);
        //Menge und Preis

        final String minimumAmount = "1";
        final TextView amountView = (TextView) view.findViewById(R.id.reservationAmount);
        amountView.setText(minimumAmount);
        //mListener.updateReservationPoitnsBuy();

        final TextView reservationPriceView = (TextView) view.findViewById(R.id.reservationPrice);
        //final TextView price = (TextView) view.findViewById(R.id.offerPrice);
        final String priceForOne = String.format(Locale.US,"%.2f", offer.getPrice());//price.getText().toString().replace(",",".").replace("€","").replaceAll("\\s","");
        final double priceForOneDouble = Double.parseDouble(priceForOne);
        reservationPriceView.setText(priceForOne);

        final TextView reservationDonationView = (TextView) view.findViewById(R.id.reservationDonation);
        final String standartDonation = "0.00";
        reservationDonationView.setText(standartDonation);

        ImageView decreaseAmountView = (ImageView) view.findViewById(R.id.reservationDecreaseAmount);
        ImageView increaseAmountView = (ImageView) view.findViewById(R.id.reservationIncreaseAmount);
        ImageView decreaseDonationtView = (ImageView) view.findViewById(R.id.reservationDecreaseDonation);
        ImageView increaseDonationView = (ImageView) view.findViewById(R.id.reservationIncreaseDonation);

        final double donationStep = 0.10;

        decreaseAmountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(amountView.getText().toString());
                if(amount > 1){
                    amount -= 1;
                    amountView.setText(String.valueOf(amount));
                    double newPrice = amount * priceForOneDouble;
                    reservationPriceView.setText(String.format(Locale.US,"%.2f", newPrice));
                    reservationDonationView.setText(standartDonation);
                    mListener.updatePointsToBuy();
                }
            }
        });

        increaseAmountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(amountView.getText().toString());
                amount += 1;
                amountView.setText(String.valueOf(amount));
                double newPrice = amount * priceForOneDouble;
                reservationPriceView.setText(String.format(Locale.US,"%.2f", newPrice));
                reservationDonationView.setText(standartDonation);
                mListener.updatePointsToBuy();
            }
        });

        decreaseDonationtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentprice = Double.parseDouble(reservationPriceView.getText().toString());
                double actuellDonation = Double.parseDouble(reservationDonationView.getText().toString());
                if(actuellDonation > donationStep){
                    actuellDonation -= donationStep;
                    reservationDonationView.setText(String.format(Locale.US,"%.2f",actuellDonation));
                    reservationPriceView.setText(String.format(Locale.US,"%.2f",currentprice -donationStep));
                }
                else if(actuellDonation > 0){
                    reservationDonationView.setText(standartDonation);
                    int amount = Integer.parseInt(amountView.getText().toString());
                    double realAmountPrice = amount*Double.parseDouble(priceForOne);
                    reservationPriceView.setText(String.format(Locale.US,"%.2f",realAmountPrice));
                }
            }
        });

        increaseDonationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentprice = Double.parseDouble(reservationPriceView.getText().toString());
                double newPrice = (Math.ceil(currentprice*10))/10;
                //reservationPriceView.setText(String.format(Locale.US,"%.2f", newPrice));
                if(currentprice == newPrice){
                    newPrice += 0.10;
                }
                reservationPriceView.setText(String.format(Locale.US,"%.2f", newPrice));

                //Spenden berechnen
                int amount = Integer.parseInt(amountView.getText().toString());
                double realAmountPrice = amount*Double.parseDouble(priceForOne);
                double diff = Double.parseDouble(reservationPriceView.getText().toString()) - realAmountPrice;
                reservationDonationView.setText(String.format(Locale.US,"%.2f", diff));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.updatePointsToBuy();
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
    public interface OnReservationFragmentInteractionListener{

        /**
         * Method that is invoked
         * in the method onCreateView of {@link ReservationFragment},
         * when {@link ReservationFragment} becomes active.
         */
        void onReservationFragmentGetsActive();

        /**
         * Method that is invoked
         * in the method onDestroyView of {@link ReservationFragment},
         * when it gets inactive.
         */
        void onReservationFragmentGetsInactive();

        /**
         * Method that is invoked
         * in the method onCreateView of {@link ReservationFragment},
         * to set the header information.
         * @param restaurantId The restaurant id
         * @param offerListPosition The position of the offer
         */
        void setReservationHeaderTopic(final int restaurantId, final int offerListPosition);

        /**
         * Method that is invoked
         * in {@link ReservationFragment},
         * when a reservation is going to start.
         * @param restaurantId The restaurant id
         * @param offerListPosition The position of the offer
         * @param reservation The reservation
         */
        void onReservationFragmentInteraction(final int restaurantId, final int offerListPosition,Reservation reservation);

        /**
         * Method that is invoked
         * in {@link ReservationFragment},
         * when a costless reservation is going to start.
         * @param restaurantId The restaurant id
         * @param offerListPosition The position of the offer
         * @param reservation The reservation
         */
        void onReservationUsePointsFragmentInteraction(final int restaurantId, final int offerListPosition,Reservation reservation);

        /**
         * Method that is invoked
         * in {@link ReservationFragment},
         * after a reservation attempt.
         * @param restaurantId The restaurant id
         */
        void refreshRestaurant(int restaurantId);

        /**
         * Method that is invoked
         * in {@link ReservationFragment},
         * to gets the offer for a reservation.
         * @param restaurantId The restaurant id
         * @param offerListPosition The position of the offer
         * @return The offer
         */
        Offer getOfferForAReservation(int restaurantId, int offerListPosition);

        /**
         * Method that is invoked
         * in {@link ReservationFragment},
         * to update the needed points by changing the amount.
         */
        void updatePointsToBuy();
    }
}