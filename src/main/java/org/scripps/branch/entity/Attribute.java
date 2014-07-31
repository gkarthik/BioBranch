package org.scripps.branch.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "attribute", uniqueConstraints = { @UniqueConstraint(name = "dataset_index_name", columnNames = {
		"col_index", "name" }) })
public class Attribute {

	@Column(unique = true)
	private int col_index;

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dataset_id", insertable = true, updatable = true)
	private Dataset dataset;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feature_id", insertable = true, updatable = true)
	private Feature feature;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;

	@Column
	private String name;

	@Column
	private float relieff;

	@Basic(optional = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime updated;

	public Attribute() {

	}

	public Attribute(int id, int col_index, String name, String dataset,
			float relieff, DateTime created, DateTime updated) {
		super();
		this.id = id;
		this.col_index = col_index;
		this.name = name;

		this.relieff = relieff;
		this.created = created;
		this.updated = updated;
	}

	public int getCol_index() {
		return col_index;
	}

	public DateTime getCreated() {
		return created;
	}

	public DateTime getCreationTime() {
		return created;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public Feature getFeature() {
		return feature;
	}

	public int getId() {
		return id;
	}

	public DateTime getModificationTime() {
		return updated;
	}

	public String getName() {
		return name;
	}

	public float getRelieff() {
		return relieff;
	}

	public DateTime getUpdated() {
		return updated;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;
		this.updated = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.updated = DateTime.now();
	}

	public void setCol_index(int col_index) {
		this.col_index = col_index;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRelieff(float relieff) {
		this.relieff = relieff;
	}

	public void setUpdated(DateTime updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)

		.append("col_Index", this.getCol_index())
				.append("created", this.getCreated())
				.append("name", this.getName())
				.append("relieff", this.getRelieff())
				.append("updated", this.getUpdated())
				.append("feature_id", this.getFeature().getId()).toString();
	}
}