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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "custom_feature")
public class CustomFeature {

	@Basic(optional = false)
	@Column(name = "created", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime created;

	@Column
	private String description;

	@Column
	private String expression;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parentCustomFeature")
	@JsonManagedReference
	private List<Component> components;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "cfeature")
	@JsonBackReference
	private List<Component> parentComponent;
	
	@ManyToOne
	@JoinTable(name = "feature_reference", joinColumns = { @JoinColumn(name = "reference_id") }, inverseJoinColumns = { @JoinColumn(name = "custom_feature_id") })
	@JsonManagedReference
	private Component reference;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column
	private String name;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "customFeatures")
	private List<Tree> tree;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = true, updatable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "dataset_id", insertable = true, updatable = true)
	@JsonManagedReference
	private Dataset dataset;

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public DateTime getCreated() {
		return created;
	}

	public String getDescription() {
		return description;
	}

	public String getExpression() {
		return expression;
	}

	public long getId() {
		return id;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public List<Component> getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(List<Component> parentComponent) {
		this.parentComponent = parentComponent;
	}
	
	public Component getReference() {
		return reference;
	}

	public void setReference(Component reference) {
		this.reference = reference;
	}

}
