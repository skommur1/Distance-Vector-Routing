package com;

import java.io.Serializable;

public class Vector implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String node;
	private double cost;
	
	public void setNode(String node) {
		this.node = node;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public String getNode() {
		return this.node;
	}
	public double getCost() {
		return this.cost;
	}

}
