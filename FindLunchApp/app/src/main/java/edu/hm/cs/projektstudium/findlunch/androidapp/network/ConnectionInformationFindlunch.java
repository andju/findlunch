package edu.hm.cs.projektstudium.findlunch.androidapp.network;


 /**
 *
 * The type ConnectionInformationFindlunch
 * contains the information for
 * the connection to the findLunch REST APIs.
 *
 * Host changed to toplevel domain to enable secure SSL.
 * Port changed to 22001 for internal port forwarding.
  *
 * Extended by Maximilian Haag on 22.12.2016.
 *
 */
public class ConnectionInformationFindlunch extends ConnectionInformation {
    /**
     * Instantiates a new Connection information findLunch.
     */
    public ConnectionInformationFindlunch() {
        super("findlunch.de", 22001);
    }
}
