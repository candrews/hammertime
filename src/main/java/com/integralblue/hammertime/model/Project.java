package com.integralblue.hammertime.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author candrews
 *
 */
public class Project implements Serializable {
	// name of the project
	@NotBlank(message="Please enter a project name")
	String name;
	
	// categories this project has been tagged with
	List<String> categories;
	
	// The budget, in dollars
	int budget;
	
	// facebook ids of the participants (not including the owner)
	List<String> participants;
	
	// facebook id of the owner
	@NotBlank
	String owner;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
}
