package org.scripps.branch.entity;

import org.joda.time.DateTime;

public class Custom_Feature {

	private int id;
	private String name;
	private String expression;
	private String description;
	private int player_id;
	private String dataset;
	private DateTime created;

	public DateTime getCreated() {
		return created;
	}

	public String getDataset() {
		return dataset;
	}

	public String getDescription() {
		return description;
	}

	public String getExpression() {
		return expression;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPlayer_id() {
		return player_id;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
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

	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}

}
