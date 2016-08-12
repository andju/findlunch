package edu.hm.cs.projektstudium.findlunch.androidapp.data.filter;


/**
 * The interface Predicate
 *
 * @param <T> the type parameter
 */
public interface Predicate<T> {
    /**
     * Returns <code>true</code> if <code>type</code>
     * meets the filter.
     *
     * @param type the type to filter
     * @return <code>true</code> if <code>type</code>
     * meets the filter.
     */
    boolean apply(T type);
}
