package org.processmining.plugins.etm.fitness.metrics;

import nl.tue.astar.Trace;

public interface CostCallback {
	
	void record(Trace trace, long cost);

}
