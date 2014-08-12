package org.scripps.branch.entity;

import java.sql.Blob;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "serialized_custom_classifier")
public class SerializedCustomClassifier {

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "custom_classifier_id", insertable = true, updatable = true)
	private CustomClassifier customClassifier;
	
	@Column
    private byte[] serialized_object;

	public DateTime getCreated() {
		return created;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;

	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CustomClassifier getCustomClassifier() {
		return customClassifier;
	}

	public void setCustomClassifier(CustomClassifier customClassifier) {
		this.customClassifier = customClassifier;
	}
	
	public byte[] getSerialized_object() {
		return serialized_object;
	}

	public void setSerialized_object(byte[] serialized_object) {
		this.serialized_object = serialized_object;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
