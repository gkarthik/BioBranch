package org.scripps.branch.entity;

import java.util.List;

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
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.postgresql.util.PGobject;

@Entity
@Table(name = "tree")
public class Tree {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	// @OneToMany(cascade={CascadeType.ALL}, mappedBy = "treedb")
	//
	// private List<Tree_FeatureDB> treefeatures;

	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "tree_feature", joinColumns = { @JoinColumn(name = "tree_id") }, inverseJoinColumns = { @JoinColumn(name = "feature_id") })
	private List<Feature> features;

	@Column
	private String comment;

	@Column(name = "created", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime created = null;

	@Column
	private boolean user_saved;

	@Column
	private int prev_tree_id = -1;

	@Column
	private boolean private_tree = false;

	@Column
	private PGobject json_tree;

	public String getComment() {
		return comment;
	}

	public DateTime getCreated() {
		return created;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public int getId() {
		return id;
	}

	public PGobject getJson_tree() {
		return json_tree;
	}

	public int getPrev_tree_id() {
		return prev_tree_id;
	}

	public User getUser() {
		return user;
	}

	public boolean isPrivate_tree() {
		return private_tree;
	}

	public boolean isUser_saved() {
		return user_saved;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;

	}

	@PreUpdate
	public void preUpdate() {

	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setJson_tree(PGobject json_tree) {
		this.json_tree = json_tree;
	}

	public void setPrev_tree_id(int prev_tree_id) {
		this.prev_tree_id = prev_tree_id;
	}

	public void setPrivate_tree(boolean private_tree) {
		this.private_tree = private_tree;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUser_saved(boolean user_saved) {
		this.user_saved = user_saved;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)

		.append("user_id", this.getUser())
				.append("feature", this.getFeatures())
				.append("comment", this.getComment())
				.append("user_saved", this.isUser_saved())
				.append("prev_tree_id", this.getPrev_tree_id())
				.append("isPrivate_tree", this.isPrivate_tree())
				.append("json_tree", this.getJson_tree()).toString();
	}

}
