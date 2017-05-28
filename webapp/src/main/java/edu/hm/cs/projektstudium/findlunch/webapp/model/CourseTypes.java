package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.CourseTypeView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;

/**
 * The Class CourseType.
 * @author Niklas Klotz
 *
 */
@Entity
@Table(name="course_types")
public class CourseTypes {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({CourseTypeView.CourseTypeRest.class})
	private int id;
	
	/** The name. */
	@Column(name="name")
	@JsonView({CourseTypeView.CourseTypeRest.class})
	@NotBlank(message="{courstype.name.notBlank}")
	@Size(min=2, max=60, message= "{coursetype.name.lengthInvalid}")
	private String name;
	
	@Column(name="restaurant_id")
	@JsonView({CourseTypeView.CourseTypeRest.class})
	private int restaurantId;
	
	@Column(name="sort_by")
	@JsonView({CourseTypeView.CourseTypeRest.class})
	@NumberFormat(style = Style.NUMBER)
	@NotNull
	@Range(min=0, max=100, message= "{coursetype.sortby.Range}")
	private int sortBy;
	
	public CourseTypes(){
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public int getSortBy() {
		return sortBy;
	}

	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}
	
}
