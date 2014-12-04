package org.scripps.branch.entity.forms;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.scripps.branch.service.SocialMediaService;
import org.scripps.branch.validation.PasswordsNotEmpty;
import org.scripps.branch.validation.PasswordsNotEqual;

@PasswordsNotEmpty(triggerFieldName = "", passwordFieldName = "password", passwordVerificationFieldName = "passwordVerification")
@PasswordsNotEqual(passwordFieldName = "password", passwordVerificationFieldName = "passwordVerification")
public class PasswordResetForm {

	private String password;

	private String passwordVerification;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordVerification() {
		return passwordVerification;
	}

	public void setPasswordVerification(String passwordVerification) {
		this.passwordVerification = passwordVerification;
	}

}