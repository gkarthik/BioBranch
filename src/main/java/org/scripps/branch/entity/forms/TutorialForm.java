package org.scripps.branch.entity.forms;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.scripps.branch.service.SocialMediaService;
import org.scripps.branch.validation.PasswordsNotEmpty;
import org.scripps.branch.validation.PasswordsNotEqual;

public class TutorialForm {
	
	@NotEmpty
	@Size(max = 100)
	private String title;
	
	@NotEmpty
	@Size(max = 200)
	private String description;
	
	@URL
	@NotEmpty
	@Size(max = 100)
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}