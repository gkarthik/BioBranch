package org.scripps.branch.entity.forms;


import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class ForgotPasswordForm {
	
	@Email
	@NotEmpty
	private String email;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}