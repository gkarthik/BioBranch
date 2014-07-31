package org.scripps.branch.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "pathway")
public class Pathway {

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "pathway_feature", joinColumns = { @JoinColumn(name = "pathway_id") }, inverseJoinColumns = { @JoinColumn(name = "feature_id") })
	private List<Feature> features;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;

	@Column
	private String name;

	@Column
	private String source_db;

	public List<Feature> getFeatures() {
		return features;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSource_db() {
		return source_db;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSource_db(String source_db) {
		this.source_db = source_db;
	}

}
