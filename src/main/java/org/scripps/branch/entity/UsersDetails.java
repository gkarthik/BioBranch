package org.scripps.branch.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.scripps.branch.service.SocialMediaService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUser;

//http://docs.spring.io/spring-social/docs/1.1.x/apidocs/org/springframework/social/security/SocialUser.html

public class UsersDetails extends SocialUser {

	// builder to build object EUD
	public static class Builder {

		private Long id;
		private String username;
		private String firstName;
		private String lastName;
		private String password;
		private Role role;
		private SocialMediaService socialSignInProvider;
		private Set<GrantedAuthority> authorities;
		private String background;
		private String purpose;

		public Builder() {
			this.authorities = new HashSet<>();
		}

		public Builder background(String background) {
			this.background = background;
			return this;
		}

		public UsersDetails build() {
			UsersDetails user = new UsersDetails(username, password,
					authorities);

			user.id = id;
			user.firstName = firstName;
			user.lastName = lastName;
			user.role = role;
			user.socialSignInProvider = socialSignInProvider;
			user.background = background;
			user.purpose = purpose;

			return user;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder password(String password) {
			if (password == null) {
				password = "SocialUser";
			}

			this.password = password;
			return this;
		}

		public Builder purpose(String purpose) {
			this.purpose = purpose;
			return this;
		}

		public Builder role(Role role) {
			this.role = role;

			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
					role.toString());
			this.authorities.add(authority);

			return this;
		}

		public Builder socialSignInProvider(
				SocialMediaService socialSignInProvider) {
			this.socialSignInProvider = socialSignInProvider;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4685211753224104969L;

	public static Builder getBuilder() {
		return new Builder();
	}

	private Long id;
	private String firstName;
	private String lastName;
	private Role role;
	private SocialMediaService socialSignInProvider;

	private String background;

	private String purpose;

	// constructor
	public UsersDetails(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public String getBackground() {
		return background;
	}

	public String getFirstName() {
		return firstName;
	}

	// getters
	public Long getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPurpose() {
		return purpose;
	}

	public Role getRole() {
		return role;
	}

	public SocialMediaService getSocialSignInProvider() {
		return socialSignInProvider;
	}

	// returns a string with following values with the object
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("username", getUsername())
				.append("firstName", firstName).append("lastName", lastName)
				.append("role", role)
				.append("socialSignInProvider", socialSignInProvider)
				.append("background", background).append("purpose", purpose)
				.toString();
	}

}