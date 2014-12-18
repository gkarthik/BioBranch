package org.scripps.branch.entity.forms;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.scripps.branch.service.SocialMediaService;
import org.scripps.branch.validation.PasswordsNotEmpty;
import org.scripps.branch.validation.PasswordsNotEqual;

public class DatasetRequestForm {
	
	@NotEmpty
	@Size(max = 200)
	private String dataDescription;
	
	@NotEmpty
	@Size(max = 200)
	private String reason;

	private Boolean privateToken = true;
	
	@NotEmpty
	@Email
	private String email;
	
	@NotEmpty
	private String firstName;
	
	@NotEmpty
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

}