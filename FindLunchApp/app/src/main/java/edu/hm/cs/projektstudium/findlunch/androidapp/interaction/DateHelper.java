package edu.hm.cs.projektstudium.findlunch.androidapp.interaction;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.R;
import edu.hm.cs.projektstudium.findlunch.androidapp.model.DayOfWeek;

/**
 * Helper class to support date-related tasks and conversions.
 *
 * Created by Andreas Juckel on 11.07.2016.
 */
public class DateHelper {

    private Context context;
    private String[] dayOfWeekArray = new String[7];

    public DateHelper(Context context) {
        this.context = context;
    }

    /**
     * Returns an array of weekday-names.
     *
     * @return weekday-names
     */
    public String[] getWeekDays() {
        dayOfWeekArray[0] = context.getResources().getString(R.string.weekday_mo);
        dayOfWeekArray[1] = context.getResources().getString(R.string.weekday_tu);
        dayOfWeekArray[2] = context.getResources().getString(R.string.weekday_we);
        dayOfWeekArray[3] = context.getResources().getString(R.string.weekday_th);
        dayOfWeekArray[4] = context.getResources().getString(R.string.weekday_fr);
        dayOfWeekArray[5] = context.getResources().getString(R.string.weekday_sa);
        dayOfWeekArray[6] = context.getResources().getString(R.string.weekday_su);

        return dayOfWeekArray;

    }

    /**
     * Converts a boolean array (weekday-selection) into a list of DayOfWeek.
     *
     * @param selectedWeekDays boolean array of selected weekdays (based on position)
     * @return List of DayOfWeek
     */
    public List<DayOfWeek> convertSelectionToWeekDays(boolean[] selectedWeekDays){
        String[] weekDayNamesFull = getWeekDays();
        List<DayOfWeek> weekDayNamesSelected = new ArrayList<>();
        for(int i = 0; i <= 6; i++) {
            if (selectedWeekDays[i] == true) {
                DayOfWeek newDayOfWeek = new DayOfWeek();
                newDayOfWeek.setName(weekDayNamesFull[i]);
                weekDayNamesSelected.add(newDayOfWeek);
            }
        }
        return weekDayNamesSelected;
    }
}
