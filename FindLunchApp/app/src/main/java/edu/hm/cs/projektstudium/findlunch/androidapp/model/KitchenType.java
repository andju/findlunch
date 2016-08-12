package edu.hm.cs.projektstudium.findlunch.androidapp.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Kitchen type.
 */
public class KitchenType implements Comparable<KitchenType> {
    /**
     * The Id.
     */
    @JsonProperty
    private int id;
    /**
     * The Name.
     */
    @JsonProperty
    private String name;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    @SuppressWarnings("unused")
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(@NonNull KitchenType another) {
        return name.compareTo(another.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KitchenType that = (KitchenType) o;

        return id == that.id && (name != null ? name.equals(that.name) : that.name == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
