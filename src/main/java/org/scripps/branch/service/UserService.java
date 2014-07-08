package org.scripps.branch.service;

import org.scripps.branch.entity.User;
import org.scripps.branch.entity.forms.RegistrationForm;

import Validation.DuplicateEmailException;

public interface UserService {

	/**
	 * Creates a new user account to the service.
	 * 
	 * @param userAccountData
	 *            The information of the created user account.
	 * @return The information of the created user account.
	 * @throws DuplicateEmailException
	 *             Thrown when the email address is found from the database.
	 */
	public User registerNewUserAccount(RegistrationForm userAccountData)
			throws DuplicateEmailException;

}
