package edu.hm.cs.projektstudium.findlunch.androidapp.data.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The type Filter that contains
 * methods for the filtering of collections.
 */
public class Filter {
    /**
     * Method that filters the collection <code>col</code> according to the predicate <code>predicate</code>.
     *
     * @param <T>       the type parameter
     * @param col       the collection with the content to filter
     * @param predicate the predicate
     * @return the filtered list
     */
    public static <T> List<T> filter(Collection<T> col, Predicate<T> predicate) {
        // create a list for the results
        List<T> result = new ArrayList<>();
        if (col != null && predicate != null) {
            for (T element: col) {
                if (predicate.apply(element)) {
                    result.add(element);
                }
            }
        }
        return result;
    }
}
