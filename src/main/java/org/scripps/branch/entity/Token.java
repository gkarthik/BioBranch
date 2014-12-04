package org.scripps.branch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name = "token")
public class Token {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(length = 255)
	private String uid;
	
	@Column(nullable = true) 
	private DateTime created;
	
	@OneToOne(mappedBy = "token")
	private User user;
	
	public String getToken() {
		return uid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setToken(String token) {
		this.uid = token;
	}

	public DateTime getTokenCreationTime() {
		return created;
	}
	
	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;
	}
	
	public void setTokenCreationTime(DateTime tokenCreationTime) {
		this.created = tokenCreationTime;
	}
}
