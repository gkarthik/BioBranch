package org.scripps.branch.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*

 BEGIN;

 create table UserConnection  (userId varchar(255) not null,
 providerId varchar(255) not null,
 providerUserId varchar(255),
 rank int not null,
 displayName varchar(255),
 profileUrl varchar(512),
 imageUrl varchar(512),
 accessToken varchar(255) not null,
 secret varchar(255),
 refreshToken varchar(255),
 expireTime bigint,
 primary key (userId, providerId, providerUserId));
 create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

 COMMIT;
 */

@Entity
@Table(name = "userconnection")
// , indexes = { @Index(name = "userconnectionrank", columnList =
// ("userId,providerId,rank")) })
public class UserConnection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "userid", nullable = false)
	private String userId;

	@Id
	@Column(name = "providerid", nullable = false)
	private String providerId;

	@Column(name = "provideruserid")
	private String providerUserId;

	@Id
	@Column(name = "rank", nullable = false)
	private int rank;

	@Column(name = "displayname")
	private String displayName;

	@Column(name = "profileurl")
	private String profileUrl;

	@Column(name = "imageurl")
	private String imageUrl;

	@Column(name = "accesstoken", nullable = false)
	private String accessToken;

	@Column(name = "secret")
	private String secret;

	@Column(name = "refreshtoken")
	private String refreshToken;

	@Column(name = "expiretime")
	private long expireTime;

	public String getAccessToken() {
		return accessToken;
	}

	public String getDisplayName() {
		return displayName;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public int getRank() {
		return rank;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getSecret() {
		return secret;
	}

	public String getUserId() {
		return userId;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}