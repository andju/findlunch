package edu.hm.cs.projektstudium.findlunch.androidapp.model;

/**
 * The type Restaurant type.
 */
public class RestaurantType {
    /**
     * The Id.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private int id;
    /**
     * The Name.
     */
    private String name;

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
     * Sets name.
     *
     * @param name the name
     */
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
