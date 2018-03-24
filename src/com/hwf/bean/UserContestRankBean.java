package com.hwf.bean;

import java.util.ArrayList;

public class UserContestRankBean {
	private int rank;
	private String username, nickname;
	private int solve, penalty;
	private ArrayList<UserContestProblemInfoBean> problemInfo;

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

	public int getSolve() {
		return solve;
	}

	public void setSolve(int solve) {
		this.solve = solve;
	}

	public int getPenalty() {
		return penalty;
	}

	public void setPenalty(int penalty) {
		this.penalty = penalty;
	}

	public ArrayList<UserContestProblemInfoBean> getProblemInfo() {
		return problemInfo;
	}

	public void setProblemInfo(ArrayList<UserContestProblemInfoBean> problemInfo) {
		this.problemInfo = problemInfo;
	}
}
