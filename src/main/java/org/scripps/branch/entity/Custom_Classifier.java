package org.scripps.branch.entity;

import org.joda.time.DateTime;

public class Custom_Classifier {

	private int id;
	private String name;
	private int type;
	private String Description;
	private int player_id;
	private DateTime created;

	public DateTime getCreated() {
		return created;
	}

	public String getDescription() {
		return Description;
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

	public int getType() {
		return type;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public void setDescription(String description) {
		Description = description;
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

	public void setType(int type) {
		this.type = type;
	}

}
