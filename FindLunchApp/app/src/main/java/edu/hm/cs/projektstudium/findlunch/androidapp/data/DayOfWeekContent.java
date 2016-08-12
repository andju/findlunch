package edu.hm.cs.projektstudium.findlunch.androidapp.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.hm.cs.projektstudium.findlunch.androidapp.model.DayOfWeek;


/**
 * Created by Andi on 10.07.2016.
 */
public class DayOfWeekContent {

    /**
     * The list of restaurants.
     */
    private List<DayOfWeek> daysOfWeek;

    public String[] getDaysOfWeek() {
        // create a list for the result
        ArrayList<String> resultList = new ArrayList<>();
        // the result
        String[] result;

        for(DayOfWeek a: daysOfWeek) {
            resultList.add(a.getName());
        }
        Collections.sort(resultList);
        result = new String[resultList.size()];
        result = resultList.toArray(result);

        return resultList.toArray(result);
    }

    public void addDaysOfWeek(DayOfWeek daysOfWeek) {
        this.daysOfWeek.add(daysOfWeek);
    }

    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

}
