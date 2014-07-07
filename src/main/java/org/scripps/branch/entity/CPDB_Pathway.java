package org.scripps.branch.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "cpdb_pathway")
public class CPDB_Pathway {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private int id;

	@Column
	private String name;

	@Column
	private String source_db;

	@Column
	private String entrez_id;

	public String getEntrez_id() {
		return entrez_id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSource_db() {
		return source_db;
	}

	public void setEntrez_id(String entrez_id) {
		this.entrez_id = entrez_id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSource_db(String source_db) {
		this.source_db = source_db;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)

		.append("name", this.getName()).append("name", this.getName())
				.append("sourcedb", this.getSource_db())
				.append("entrez_id", this.getEntrez_id()).toString();
	}

}
