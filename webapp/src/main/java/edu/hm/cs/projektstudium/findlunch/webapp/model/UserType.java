package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The Class UserType.
 */
@Entity
@Table(name="user_type")
public class UserType {

	/** The id. */
	@Id
	private int id;

	/** The name. */
	private String name;

	/** The users. */
	//bi-directional many-to-one association to User
	@OneToMany(mappedBy="userType")
	private List<User> users;

	/**
	 * Instantiates a new user type.
	 */
	public UserType() {
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * Sets the users.
	 *
	 * @param users the new users
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 * @return the user
	 */
	public User addUser(User user) {
		getUsers().add(user);
		user.setUserType(this);

		return user;
	}

	/**
	 * Removes the user.
	 *
	 * @param user the user
	 * @return the user
	 */
	public User removeUser(User user) {
		getUsers().remove(user);
		user.setUserType(null);

		return user;
	}

}