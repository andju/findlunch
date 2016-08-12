package edu.hm.cs.projektstudium.findlunch.androidapp.view;

import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 * This class is safe to use as an OnClickListener for multiple views,
 * and will debounce each one separately.
 * <p>
 * http://stackoverflow.com/questions/16534369/avoid-button-multiple-rapid-clicks
 */
public abstract class DebouncedOnClickListener implements View.OnClickListener {

    /**
     * The Minimum interval between two clicks.
     */
    private final long minimumInterval;
    /**
     * The Last click map.
     */
    private final Map<View, Long> lastClickMap;

    /**
     * Implement this in your subclass instead of onClick
     *
     * @param v The view that was clicked
     */
    public abstract void onDebouncedClick(View v);


    /**
     * Instantiates a new Debounced on click listener.
     *
     * @param minimumIntervalMsec The minimum allowed time between clicks - any click sooner
     *                            than this after a previous click will be rejected
     */
    public DebouncedOnClickListener(
            @SuppressWarnings("SameParameterValue") long minimumIntervalMsec) {
        this.minimumInterval = minimumIntervalMsec;
        this.lastClickMap = new WeakHashMap<>();
    }

    @Override
    public void onClick(View clickedView) {
        // the timestamp of the previous click
        Long previousClickTimestamp = lastClickMap.get(clickedView);
        // the current timestamp
        long currentTimestamp = SystemClock.uptimeMillis();

        lastClickMap.put(clickedView, currentTimestamp);
        if(previousClickTimestamp == null ||
                (currentTimestamp - previousClickTimestamp > minimumInterval)) {
            onDebouncedClick(clickedView);
        }
    }
}
