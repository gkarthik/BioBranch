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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "custom_feature")
public class Component {

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@OneToOne
	@JoinTable(name = "component_feature", joinColumns = { @JoinColumn(name = "feature_id") }, inverseJoinColumns = { @JoinColumn(name = "component_id") })
	private Feature feature;
	
	@OneToOne
	@JoinTable(name = "component_custom_feature", joinColumns = { @JoinColumn(name = "custom_feature_id") }, inverseJoinColumns = { @JoinColumn(name = "component_id") })
	private CustomFeature cfeature;
	
	@ManyToOne
	@JoinTable(name = "custom_feature_component", joinColumns = { @JoinColumn(name = "custom_feature_id") }, inverseJoinColumns = { @JoinColumn(name = "component_id") })
	private CustomFeature parentCustomFeature;
	
	@Column
	private long upperLimit;
	
	@Column
	private long lowerLimit;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public CustomFeature getCfeature() {
		return cfeature;
	}

	public void setCfeature(CustomFeature cfeature) {
		this.cfeature = cfeature;
	}

	public long getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(long upperLimit) {
		this.upperLimit = upperLimit;
	}

	public long getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(long lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Object getEntity(){
		if(feature != null){
			return feature;
		}
		return cfeature;
	}
	
}
