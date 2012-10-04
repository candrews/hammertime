package com.integralblue.hammertime.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author candrews
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Project.listAll", query = "select p from Project p"),
	@NamedQuery(name="Project.findByName", query = "select p from Project p where p.name = :name")
})
public class Project implements Serializable {
	@Id
	@GeneratedValue
	Long id;
	
	// name of the project
	@NotBlank(message="Please enter a project name")
	@Column(nullable=false,unique=true)
	String name;
	
	// categories this project has been tagged with
	@ManyToMany
	Set<Category> categories = new HashSet<Category>();
	
	// The budget, in dollars
	@Column(nullable=false)
	int budget;
	
	// participants (not including the owner)
	@ManyToMany
	Set<FacebookUser> participants = new HashSet<FacebookUser>();
	
	@NotNull
	@ManyToOne(optional=false)
	FacebookUser owner;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public Set<FacebookUser> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<FacebookUser> participants) {
		this.participants = participants;
	}

	public FacebookUser getOwner() {
		return owner;
	}

	public void setOwner(FacebookUser owner) {
		this.owner = owner;
	}
	
	// no setter for ID so Hibernate will always generate the ID for us
	
	
}
