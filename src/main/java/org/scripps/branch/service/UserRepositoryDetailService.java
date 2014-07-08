package org.scripps.branch.service;

import org.scripps.branch.entity.User;
import org.scripps.branch.entity.UsersDetails;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserRepositoryDetailService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserRepositoryDetailService.class);

	private UserRepository repository;

	@Autowired
	public UserRepositoryDetailService(UserRepository repository) {
		this.repository = repository;
	}

	/**
	 * Loads the user information.
	 * 
	 * @param username
	 *            The username of the requested user.
	 * @return The information of the user.
	 * @throws UsernameNotFoundException
	 *             Thrown if no user is found with the given username.
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		LOGGER.debug("Loading user by username: {}", username);

		User user = repository.findByEmail(username);
		LOGGER.debug("Found user: {}", user);

		if (user == null) {
			throw new UsernameNotFoundException("No user found with username: "
					+ username);
		}

		UsersDetails principal = UsersDetails.getBuilder()
				.firstName(user.getFirstName()).id(user.getId())
				.lastName(user.getLastName()).password(user.getPassword())
				.role(user.getRole())
				.socialSignInProvider(user.getSignInProvider())
				.username(user.getEmail()).build();

		LOGGER.debug("Returning user details: {}", principal);

		return principal;
	}
}