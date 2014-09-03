package it.frank.deferred.observers.performance;

import it.frank.deferred.Task.TaskInfo;

public class TaskExecutionInfo {
	int step;
	TaskInfo taskInfo;
	long startTime;
	long endTime;
	
	public int getStep() {
		return step;
	}
	
	public TaskInfo getTaskInfo() {
		return taskInfo;
	}
	
	public long getExecutionTime() {
		return endTime - startTime;
	}		
	
	void start() {
		startTime = System.currentTimeMillis();
	}
	
	void stop() {
		endTime = System.currentTimeMillis();
	}
}