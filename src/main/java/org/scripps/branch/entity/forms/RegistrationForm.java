package org.scripps.branch.entity.forms;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.scripps.branch.service.SocialMediaService;
import org.scripps.branch.validation.PasswordsNotEmpty;
import org.scripps.branch.validation.PasswordsNotEqual;

@PasswordsNotEmpty(triggerFieldName = "signInProvider", passwordFieldName = "password", passwordVerificationFieldName = "passwordVerification")
@PasswordsNotEqual(passwordFieldName = "password", passwordVerificationFieldName = "passwordVerification")
public class RegistrationForm {

	public static final String FIELD_NAME_EMAIL = "email";

	private String background;

	@Email
	@NotEmpty
	@Size(max = 100)
	private String email;

	@NotEmpty
	@Size(max = 100)
	private String firstName;

	@NotEmpty
	@Size(max = 100)
	private String lastName;

	private String password;

	private String passwordVerification;

	private String purpose;

	private SocialMediaService signInProvider;

	public RegistrationForm() {

	}

	public String getBackground() {
		return background;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordVerification() {
		return passwordVerification;
	}

	public String getPurpose() {
		return purpose;
	}

	public SocialMediaService getSignInProvider() {
		return signInProvider;
	}

	public boolean isNormalRegistration() {
		return signInProvider == null;
	}

	public boolean isSocialSignIn() {
		return signInProvider != null;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordVerification(String passwordVerification) {
		this.passwordVerification = passwordVerification;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setSignInProvider(SocialMediaService signInProvider) {
		this.signInProvider = signInProvider;

	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("email", email)
				.append("firstName", firstName).append("lastName", lastName)
				.append("signInProvider", signInProvider)
				.append("background", background).append("purpose", purpose)
				.toString();

	}
}