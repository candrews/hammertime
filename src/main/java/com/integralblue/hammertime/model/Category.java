package com.integralblue.hammertime.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@NamedQueries({
	@NamedQuery(name="Category.listAll", query = "select c from Category c"),
	@NamedQuery(name="Category.findByName", query = "select c from Category c where c.name = :name")
})
public class Category {
	
	@Id
	@GeneratedValue
	Long id;
	
	@Column(unique=true, nullable=false)
	@NotBlank
	String name;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// no setter for ID so Hibernate will always generate the ID for us
}
