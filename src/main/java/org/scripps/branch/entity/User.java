package org.scripps.branch.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.scripps.branch.service.SocialMediaService;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "user_accounts")
public class User extends BaseEntity<Long> {

	public static class Builder {

		private User user;

		public Builder() {
			user = new User();
			user.role = Role.ROLE_USER;
		}

		public Builder background(String background) {
			user.background = background;
			return this;
		}

		public User build() {
			return user;
		}

		public Builder email(String email) {
			user.email = email;
			return this;
		}

		public Builder firstName(String firstName) {
			user.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			user.lastName = lastName;
			return this;
		}

		public Builder password(String password) {
			user.password = password;
			return this;
		}

		public Builder purpose(String purpose) {
			user.purpose = purpose;
			return this;
		}

		public Builder signInProvider(SocialMediaService signInProvider) {
			user.signInProvider = signInProvider;
			return this;
		}
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@JsonBackReference
	private List<Tree> trees;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@JsonBackReference
	private List<Collection> collection;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<CustomClassifier> customclassifiers;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<CustomFeature> customfeatures;

	@Column(name = "email", length = 100, nullable = false, unique = true)
	private String email;

	@Column(name = "first_name", length = 100, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 100, nullable = false)
	private String lastName;

	@Column(name = "password", length = 255)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", length = 20, nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(name = "sign_in_provider", length = 20)
	private SocialMediaService signInProvider;

	@Column
	private String background;

	@Column
	private String purpose;

	public User() {

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

	@Override
	public Long getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public String getPurpose() {
		return purpose;
	}

	public Role getRole() {
		return role;
	}

	public SocialMediaService getSignInProvider() {
		return signInProvider;
	}

	public List<Tree> getTrees_id() {
		return trees;
	}

	public List<CustomFeature> getUser_id() {
		return customfeatures;
	}

	public List<CustomClassifier> getUsers_id() {
		return customclassifiers;
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

	public void setId(Long id) {
		this.id = id;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setSignInProvider(SocialMediaService signInProvider) {
		this.signInProvider = signInProvider;
	}

	public void setTrees_id(List<Tree> trees_id) {
		this.trees = trees_id;
	}

	public void setUser_id(List<CustomFeature> cfList) {
		this.customfeatures = cfList;
	}

	public void setUsers_id(List<CustomClassifier> ccList) {
		this.customclassifiers = ccList;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("creationTime", this.getCreationTime())
				.append("email", email).append("firstName", firstName)
				.append("lastName", lastName)
				.append("modificationTime", this.getModificationTime())
				.append("signInProvider", this.getSignInProvider())
				.append("version", this.getVersion())
				.append("background", this.getBackground())
				.append("purpose", this.getPurpose()).toString();
	}
}
