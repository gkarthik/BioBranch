package org.scripps.branch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;

/*
 create table if not exists persistent_logins ( 
 username varchar (100) not null, 
 series varchar(64) primary key, 
 token varchar(64) not null, 
 last_used timestamp not null
 );
 */

@Entity
@Table(name = "persistent_logins")
public class Persistent_Logins {

	@Column(name = "last_used")
	private DateTime last_used;

	@Id
	@Column(name = "series")
	private String series;

	@Column(name = "token")
	private String token;

	@Column(name = "username")
	private String username;

	public DateTime getLast_used() {
		return last_used;
	}

	public String getSeries() {
		return series;
	}

	public String getToken() {
		return token;
	}

	public String getUsername() {
		return username;
	}

	public void setLast_used(DateTime last_used) {
		this.last_used = last_used;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
