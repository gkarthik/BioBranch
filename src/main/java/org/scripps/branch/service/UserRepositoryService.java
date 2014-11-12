package org.scripps.branch.service;

import org.scripps.branch.entity.User;
import org.scripps.branch.entity.forms.RegistrationForm;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Validation.DuplicateEmailException;

@Service
public class UserRepositoryService implements UserService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserRepositoryService.class);

	private PasswordEncoder passwordEncoder;

	private UserRepository repository;

	@Autowired
	public UserRepositoryService(PasswordEncoder passwordEncoder,
			UserRepository repository) {
		this.passwordEncoder = passwordEncoder;
		this.repository = repository;
	}

	private boolean emailExist(String email) {
		User user = repository.findByEmail(email);
		if (user != null) {
			return true;
		}
		return false;
	}

	private String encodePassword(RegistrationForm dto) {
		String encodedPassword = null;
		if (dto.isNormalRegistration()) {
			encodedPassword = passwordEncoder.encode(dto.getPassword());
		}

		return encodedPassword;
	}

	@Transactional
	@Override
	public User registerNewUserAccount(RegistrationForm userAccountData)
			throws DuplicateEmailException {
		if (emailExist(userAccountData.getEmail())) {
			LOGGER.debug("Email: {} exists. Throwing exception.",
					userAccountData.getEmail());
			throw new DuplicateEmailException("The email address: "
					+ userAccountData.getEmail() + " is already in use.");
		}
		String encodedPassword = encodePassword(userAccountData);

		User.Builder user = User.getBuilder().email(userAccountData.getEmail())
				.firstName(userAccountData.getFirstName())
				.lastName(userAccountData.getLastName())
				.password(encodedPassword)
				.background(userAccountData.getBackground())
				.purpose(userAccountData.getPurpose());

		if (userAccountData.isSocialSignIn()) {
			user.signInProvider(userAccountData.getSignInProvider());
		}
		User registered = user.build();
		return repository.save(registered);
	}
}