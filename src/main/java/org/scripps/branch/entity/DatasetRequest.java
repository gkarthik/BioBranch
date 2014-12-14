package org.scripps.branch.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "datasetRequest")
public class DatasetRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;
	
	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;
	
	public DatasetRequest(){
		
	}
	
	public DatasetRequest(String dataDescription,
			String reason, Boolean privateToken, String email,
			String firstName, String lastName) {
		super();
		this.dataDescription = dataDescription;
		this.reason = reason;
		this.privateToken = privateToken;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Column
	private String dataDescription;
	
	@Column
	private String reason;

	@Column
	private Boolean privateToken;
	
	@Column
	private String email;
	
	@Column
	private String firstName;
	
	@Column
	private String lastName;

	

	public String getDataDescription() {
		return dataDescription;
	}

	public void setDataDescription(String dataDescription) {
		this.dataDescription = dataDescription;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Boolean getPrivateToken() {
		return privateToken;
	}

	public void setPrivateToken(Boolean privateToken) {
		this.privateToken = privateToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public DateTime getCreated() {
		return created;
	}

	public int getId() {
		return id;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;
	}

	public void setId(int id) {
		this.id = id;
	}
}