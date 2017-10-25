package edu.xidian.petrinet.createnet;

import java.util.ArrayList;

/**
 *  辅助类 
 *
 */
public class PTNetTraversalStepInfo {

	private ArrayList<String> kuSuo;
	private ArrayList<String> bianQian;
	private ArrayList<String> chuShiKuSuo;
	private String liuGuanXi[] ; 
	
	public ArrayList<String> getKuSuo() {
		return kuSuo;
	}
	public void setKuSuo(ArrayList<String> kuSuo) {
		this.kuSuo = kuSuo;
	}
	public ArrayList<String> getBianQian() {
		return bianQian;
	}
	public void setBianQian(ArrayList<String> bianQian) {
		this.bianQian = bianQian;
	}
	public ArrayList<String> getChuShiKuSuo() {
		return chuShiKuSuo;
	}
	public void setChuShiKuSuo(ArrayList<String> chuShiKuSuo) {
		this.chuShiKuSuo = chuShiKuSuo;
	}
	public String[] getLiuGuanXi() {
		return liuGuanXi;
	}
	public void setLiuGuanXi(String[] liuGuanXi) {
		this.liuGuanXi = liuGuanXi;
	}
}
