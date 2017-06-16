package edu.hm.cs.projektstudium.findlunch.webapp.controller.view;

/**
 * The Class OfferView.
 * 
 * @author Niklas Klotz
 */
public class OfferView {

	/**
	 * The Interface OfferRest. If used, only the fields marked with the
	 * annotation "JsonView(OfferView.OfferRest.class)" will be returned when
	 * accessing a rest controller (GET)
	 */
	public interface OfferRest {
	}

	/**
	 * The Interface OfferPhotoFull. If used, only the fields marked with the
	 * annotation "JsonView(OfferView.OfferPhotoFull.class)" will be returned
	 * when accessing a rest controller (GET)
	 */
	public interface OfferPhotoFull extends OfferRest {
	}
}
