package edu.hm.cs.projektstudium.findlunch.webapp.distance;

/**
 * The Class DistanceCalculator.
 */
public class DistanceCalculator {
	
	/** The Constant AVERAGE_RADIUS_OF_EARTH. */
	private final static double AVERAGE_RADIUS_OF_EARTH = 6371;
	
	/**
	 * Calculates the distance between userLocation and venueLocation.
	 *
	 * @param userLat the users latitude
	 * @param userLng the users longitude
	 * @param venueLat the venue latitude
	 * @param venueLng the venue longitude
	 * @return the distance
	 */
	public static int calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {

		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(userLat))
				* Math.cos(Math.toRadians(venueLat)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		// multiply by 1000 to get distance in meters
		return (int) Math.round(AVERAGE_RADIUS_OF_EARTH * c * 1000);
	}
}
