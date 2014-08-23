package org.scripps.branch.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@MappedSuperclass
public abstract class BaseEntity<ID> {

	@Column(name = "creation_time", nullable = false, updatable = false, insertable = true)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime creationTime;

	@Column(name = "modification_time", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime modificationTime;

	@Version
	private long version;

	public DateTime getCreationTime() {
		return creationTime;
	}

	public abstract Long getId();

	public DateTime getModificationTime() {
		return modificationTime;
	}

	public long getVersion() {
		return version;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.creationTime = now;
		this.modificationTime = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.modificationTime = DateTime.now();
	}

}