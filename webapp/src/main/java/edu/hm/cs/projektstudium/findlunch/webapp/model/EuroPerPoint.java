package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * The Class EuroPerPoint
 */
@Entity
@Table(name="euro_per_point")
public class EuroPerPoint {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The euro per point.*/
	@NumberFormat(style=Style.DEFAULT)
	private float euro;
	
	/** The reservations*/
	@OneToMany(mappedBy="euroPerPoint")
	private List<Reservation> reservations;

	/**
	 * Gets the Euro per point.
	 * @return
	 */
	public float getEuro() {
		return euro;
	}
	
	/**
	 * Sets the euro per point.
	 * @param euro the new euro per point
	 */
	public void setEuro(float euro) {
		this.euro = euro;
	}
	
}
