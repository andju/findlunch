package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The class PushToken
 * The class represents a firebase push token
 * @author Niklas Klotz
 *
 */
@Entity
@Table(name="user_pushtoken")
public class PushToken {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The user*/
	@Column(name="user_id")
	@NotNull
	private int userId;
	
	/** The token*/
	@Column(name="fcm_token")
	@NotNull
	private String fcmToken;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return userId;
	}

	public void setUser_id(int user_id) {
		this.userId = user_id;
	}

	public String getFcm_token() {
		return fcmToken;
	}

	public void setFcm_token(String fcm_token) {
		this.fcmToken = fcm_token;
	}
	
}
