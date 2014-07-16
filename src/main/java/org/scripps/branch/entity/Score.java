package org.scripps.branch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "tree_score")
public class Score {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "created", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime created = null;

	@OneToOne(mappedBy = "score")
	@JsonBackReference
	private Tree tree;

	@Column
	private double pct_correct;

	@Column
	private double novelty;

	@Column
	private double size;

	@Column
	private double score;

	@Column
	private String dataset;

	public String getDataset() {
		return dataset;
	}

	public double getNovelty() {
		return novelty;
	}

	public double getPct_correct() {
		return pct_correct;
	}

	public double getScore() {
		return score;
	}

	public double getSize() {
		return size;
	}

	public Tree getTree() {
		return tree;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.created = now;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public void setNovelty(double novelty) {
		this.novelty = novelty;
	}

	public void setPct_correct(double pct_correct) {
		this.pct_correct = pct_correct;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public void setTree(Tree tree) {
		this.tree = tree;
	}
}
