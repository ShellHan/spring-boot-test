package com.springboot.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "User")
@Entity
public class User {

	private Integer id;
	private String lastName;
	private String email;
	
	public User() {
		
	}
	
	public User(Integer id, String lastName, String email) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.email = email;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", lastName=" + lastName + ", email=" + email
				+ "]";
	}
	
}
