package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;


/**
 * The Class OfferPhoto.
 */
@Entity
@Table(name="offer_photo")
public class OfferPhoto {

	/** The id. */
	@Id
	@JsonView(OfferView.OfferRest.class)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The photo. */
	@Lob
	@JsonView(OfferView.OfferPhotoFull.class)
	private byte[] photo;

	/** The offer. */
	//bi-directional many-to-one association to Offer
	@ManyToOne(fetch=FetchType.LAZY)
	private Offer offer;
	
	/** The thumbnail. */
	@Lob
	@JsonView(OfferView.OfferRest.class)
	private byte[] thumbnail;
	
	/** The base 64 encoded. */
	@Transient
	@JsonIgnore
	private String base64Encoded;
	
	/** The image format. */
	@Transient
	@JsonIgnore
	private String imageFormat;

	/**
	 * Instantiates a new offer photo.
	 */
	public OfferPhoto() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the photo.
	 *
	 * @return the photo
	 */
	public byte[] getPhoto() {
		return this.photo;
	}

	/**
	 * Sets the photo.
	 *
	 * @param photo the new photo
	 */
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	/**
	 * Gets the offer.
	 *
	 * @return the offer
	 */
	public Offer getOffer() {
		return this.offer;
	}

	/**
	 * Sets the offer.
	 *
	 * @param offer the new offer
	 */
	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	/**
	 * Gets the base 64 encoded.
	 *
	 * @return the base 64 encoded
	 */
	public String getBase64Encoded() {
		return base64Encoded;
	}

	/**
	 * Sets the base 64 encoded.
	 *
	 * @param base64Encoded the new base 64 encoded
	 */
	public void setBase64Encoded(String base64Encoded) {
		this.base64Encoded = base64Encoded;
	}

	/**
	 * Gets the thumbnail.
	 *
	 * @return the thumbnail
	 */
	public byte[] getThumbnail() {
		return thumbnail;
	}

	/**
	 * Sets the thumbnail.
	 *
	 * @param thumbnail the new thumbnail
	 */
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * Gets the image format.
	 *
	 * @return the image format
	 */
	public String getImageFormat() {
		return imageFormat;
	}

	/**
	 * Sets the image format.
	 *
	 * @param imageFormat the new image format
	 */
	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

}