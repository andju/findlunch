package edu.hm.cs.projektstudium.findlunch.androidapp.network;


/**
 * The type ConnectionInformationFindlunch
 * contains the information for
 * the connection to the findLunch REST APIs.
 */
public class ConnectionInformationFindlunch extends ConnectionInformation {
    /**
     * Instantiates a new Connection information findLunch.
     */
    public ConnectionInformationFindlunch() {
        super("192.168.1.131", 8080, false);
    }
}
