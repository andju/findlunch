package edu.hm.cs.projektstudium.findlunch.androidapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.fragment.PushOverviewFragment;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification;

/**
 * {@link RecyclerView.Adapter} that displays the list of active
 * {@link edu.hm.cs.projektstudium.findlunch.androidapp.model.PushNotification} for the user.
 *
 * Created by Andreas Juckel on 18.07.2016.
 */
public class PushItemRecyclerViewAdapter extends RecyclerView.Adapter<PushItemRecyclerViewAdapter.ViewHolder> {

    /**
     * the push notifications to display.
     */
    private final List<PushNotification> mValues;
    /**
     * the listener whose implementation of {@link PushOverviewFragment.OnPushOverviewFragmentInteractionListener}
     * gets called on interaction.
     */
    private final PushOverviewFragment.OnPushOverviewFragmentInteractionListener mListener;

    /**
     * instantiates a new push item recycler view adapter.
     *
     * @param items    the items
     * @param listener the listener
     */
    public PushItemRecyclerViewAdapter(List<PushNotification> items, PushOverviewFragment.OnPushOverviewFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mTitleView;
        public final ImageView mDeleteView;
        public final ImageView mFilterView;
        public final TextView mWeekdaysView;
        public final TextView mKitchenTypesView;

        /**
         * Instantiates a new View holder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.pushName);
            mDeleteView = (ImageView) view.findViewById(R.id.iconDelete);
            mWeekdaysView = (TextView) view.findViewById(R.id.pushWeekdays);
            mKitchenTypesView = (TextView) view.findViewById(R.id.pushKitchenTypes);
            mFilterView = (ImageView) view.findViewById(R.id.iconFilter);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_push_overview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTitleView.setText(mValues.get(position).getTitle());

        // create string of weekday abbreviations
        StringBuilder weekdays = new StringBuilder();
        for (DayOfWeek d : mValues.get(position).getDayOfWeeks()) {
            weekdays.append(d.getName().substring(0,2));
            weekdays.append(", ");
        }

        // remove last ", " from string
        holder.mWeekdaysView.setText(weekdays.substring(0, weekdays.length() - 2));

        // create string of kitchen types
        if(mValues.get(position).getKitchenTypes().size() > 0) {
            StringBuilder kitchenTypes = new StringBuilder();
            for (KitchenType k : mValues.get(position).getKitchenTypes()) {
                kitchenTypes.append(k.getName());
                kitchenTypes.append(", ");
            }
            holder.mKitchenTypesView.setText(kitchenTypes.substring(0, kitchenTypes.length() - 2));
        } else {
            holder.mFilterView.setVisibility(View.INVISIBLE);
        }

        holder.mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the activity that a push notification shall be deleted.
                mListener.onPushOverviewFragmentDeleteInteraction(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}