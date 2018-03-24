package com.hwf.bean;

public class TotalRankBean {
	private int rank;
	private String username, nickname;
	private int accept, submit;
	private double acRate;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getAccept() {
		return accept;
	}

	public void setAccept(int accept) {
		this.accept = accept;
	}

	public int getSubmit() {
		return submit;
	}

	public void setSubmit(int submit) {
		this.submit = submit;
	}

	public double getAcRate() {
		return acRate;
	}

	public void setAcRate(double acRate) {
		this.acRate = acRate;
	}

}
