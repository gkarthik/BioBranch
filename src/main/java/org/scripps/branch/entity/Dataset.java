package org.scripps.branch.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "dataset")
public class Dataset {

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataset")
	private List<Attribute> attributes;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dataset")
	@JsonBackReference
	private List<CustomFeature> customfeature;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "collection_id", insertable = true, updatable = true)
	@JsonManagedReference
	private Collection collection;

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@Column
	private String datasetfile;

	@Column
	private String datasetname;

	// description
	@Column(length = 1000)
	private String description;

	@Column
	private String featurefile;

	@Column
	private String featurename;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column
	private String mappingfile;

	@Column
	private String mappingname;

	@Column
	private String name;

	@Column
	private boolean privateset;
	
	@OneToMany(mappedBy = "dataset")
	@JsonBackReference
	private List<Score> score;
	
	@OneToMany(mappedBy = "testset")
	@JsonBackReference
	private List<Score> testscore;
	
	//holds class attribute of the dataset
		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "attribute_id", insertable = true, updatable = true)
		private Attribute attribute;

		public Attribute getAttribute() {
			return attribute;
		}

		public void setAttribute(Attribute attribute) {
			this.attribute = attribute;
		}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public Collection getCollection() {
		return collection;
	}

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

	public boolean isPrivateset() {
		return privateset;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;

	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
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

	public void setPrivateset(boolean privateset) {
		this.privateset = privateset;
	}

}
