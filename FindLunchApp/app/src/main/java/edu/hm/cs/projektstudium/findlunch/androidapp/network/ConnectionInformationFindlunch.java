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
        super("findlunch.biz.tm", 8443);
    }
}
