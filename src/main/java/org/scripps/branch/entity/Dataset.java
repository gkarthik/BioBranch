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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "dataset")
public class Dataset {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@Column
	private String datasetfile;

	@Column
	private String mappingfile;

	@Column
	private String featurefile;

	@Column
	private String datasetname;

	@Column
	private String mappingname;

	@Column
	private String featurename;

	@Column
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	private User user;

	// description
	@Column(length = 1000)
	private String description;

	public DateTime getCreated() {
		return created;
	}

	public String getDatasetfile() {
		return datasetfile;
	}

	public String getDatasetname() {
		return datasetname;
	}

	public String getDescription() {
		return description;
	}

	public String getFeaturefile() {
		return featurefile;
	}

	public String getFeaturename() {
		return featurename;
	}

	public long getId() {
		return id;
	}

	public String getMappingfile() {
		return mappingfile;
	}

	public String getMappingname() {
		return mappingname;
	}

	public String getName() {
		return name;
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

	public void setDatasetfile(String datasetfile) {
		this.datasetfile = datasetfile;
	}

	public void setDatasetname(String datasetname) {
		this.datasetname = datasetname;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFeaturefile(String featurefile) {
		this.featurefile = featurefile;
	}

	public void setFeaturename(String featurename) {
		this.featurename = featurename;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMappingfile(String mappingfile) {
		this.mappingfile = mappingfile;
	}

	public void setMappingname(String mappingname) {
		this.mappingname = mappingname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
