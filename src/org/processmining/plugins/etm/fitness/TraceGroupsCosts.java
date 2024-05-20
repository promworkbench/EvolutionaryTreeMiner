package org.processmining.plugins.etm.fitness;

public class TraceGroupsCosts {

	public TraceGroupsCosts(String traceName, String groupName) {
		this.groupName = groupName;
		this.traceName = traceName;
	}
	
	public String traceName; 
	
	public String groupName; 
	
	public double cost = -1;
	
}
