package edu.hm.cs.projektstudium.findlunch.androidapp.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.data.OfferContent;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.OfferItemFragment;

/**
 * {@link RecyclerView.Adapter} that can display a {@link edu.hm.cs.projektstudium.findlunch.androidapp.model.Offer} and makes a call to the
 * specified {@link edu.hm.cs.projektstudium.findlunch.androidapp.fragment.OfferItemFragment.OnOfferListFragmentInteractionListener}.
 */
public class OfferItemRecyclerViewAdapter extends RecyclerView.Adapter<OfferItemRecyclerViewAdapter.ViewHolder> {

    /**
     * The offers to display.
     */
    private final OfferContent mValues;
    /**
     * The listener whose implementation of {@link edu.hm.cs.projektstudium.findlunch.androidapp.fragment.OfferItemFragment.OnOfferListFragmentInteractionListener}}
     * gets called on interaction.
     */
    private final OfferItemFragment.OnOfferListFragmentInteractionListener mListener;

    /**
     * Instantiates a new Offer item recycler view adapter.
     *
     * @param items    the items to display
     * @param listener the listener to call on interaction
     */
    public OfferItemRecyclerViewAdapter(OfferContent items, OfferItemFragment.OnOfferListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_offer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mTitleView.setText(mValues.getTitle(position));
        holder.mDescriptionView.setText(mValues.getDescription(position));
        holder.mPriceView.setText(mValues.getPrice(position));
        holder.mPreparationTime.setText(mValues.getPreparationTime(position));

        // get the default photo
        Bitmap offerPhoto = mValues.getDefaultPhoto(position);
        if (offerPhoto != null) {
            holder.mOfferPhoto.setImageBitmap(offerPhoto);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onOfferListFragmentInteraction();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The view.
         */
        public final View mView;
        /**
         * The title view.
         */
        public final TextView mTitleView;
        /**
         * The description view.
         */
        public final TextView mDescriptionView;
        /**
         * The price view.
         */
        public final TextView mPriceView;
        /**
         * The preparation time.
         */
        public final TextView mPreparationTime;
        /**
         * The offer photo.
         */
        public final ImageView mOfferPhoto;

        /**
         * Instantiates a new View holder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.offerTitle);
            mDescriptionView = (TextView) view.findViewById(R.id.offerDescription);
            mPriceView = (TextView) view.findViewById(R.id.offerPrice);
            mPreparationTime = (TextView) view.findViewById(R.id.offerPreparationTime);
            mOfferPhoto = (ImageView) view.findViewById(R.id.offerPhoto);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + " '" + mDescriptionView.getText() + " '" + mPriceView.getText() +" '" + mPreparationTime.getText() +"'";
        }
    }
}
