package org.scripps.branch.entity;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "custom_classifier")
public class CustomClassifier {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column
	private String name;

	@Column
	private int type;

	@Column
	private String Description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	private User user;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "custom_classifier_feature", joinColumns = { @JoinColumn(name = "feature_id") }, inverseJoinColumns = { @JoinColumn(name = "custom_classifier_id") })
	private List<Feature> feature;

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "customClassifiers")
	private List<Tree> tree;

	public DateTime getCreated() {
		return created;
	}

	public String getDescription() {
		return Description;
	}

	public List<Feature> getFeature() {
		return feature;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
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

	public void setDescription(String description) {
		Description = description;
	}

	public void setFeature(List<Feature> feature) {
		this.feature = feature;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
