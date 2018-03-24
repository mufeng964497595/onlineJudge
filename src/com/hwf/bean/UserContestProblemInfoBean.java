package com.hwf.bean;

public class UserContestProblemInfoBean {
	private String pno;
	private int wrong, ac;
	private String acMinute;
	private int firstBlood;

	public String getPno() {
		return pno;
	}

	public void setPno(String pno) {
		this.pno = pno;
	}

	public int getWrong() {
		return wrong;
	}

	public void setWrong(int wrong) {
		this.wrong = wrong;
	}

	public int getAc() {
		return ac;
	}

	public String getAcMinute() {
		return acMinute;
	}

	public void setAcMinute(String acMinute) {
		this.acMinute = acMinute;
	}

	public void setAc(int ac) {
		this.ac = ac;
	}

	public int getFirstBlood() {
		return firstBlood;
	}

	public void setFirstBlood(int firstBlood) {
		this.firstBlood = firstBlood;
	}
}
