package it.frank.deferred.observers.performance;

import it.frank.deferred.ExecutionContext;
import it.frank.deferred.ExecutionObserver;
import it.frank.deferred.ExecutionStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPerformanceObserver implements ExecutionObserver {

	protected long startTime;
	protected long endTime;
	
	private List<TaskExecutionInfo> executionInfos = new ArrayList<TaskExecutionInfo>();
	private TaskExecutionInfo currentInfo;
	
	public void executionWillStart(ExecutionContext context) {
		this.startTime = System.currentTimeMillis();
	}

	public void taskWillStart(ExecutionContext context) {		
		currentInfo = new TaskExecutionInfo();
		currentInfo.step = context.getStep();
		currentInfo.taskInfo = context.getTaskInfo();
		currentInfo.start();
	}

	public void taskEnded(ExecutionContext context) {		
		currentInfo.stop();
		executionInfos.add(currentInfo);
	}

	public void taskFailed(ExecutionContext context) {
		currentInfo.stop();
		executionInfos.add(currentInfo);
	}
	
	public void executionEnded(ExecutionStatus status) {
		currentInfo = null;
		this.endTime = System.currentTimeMillis();
	}

	public List<TaskExecutionInfo> getExecutionInfos() {
		return executionInfos;
	}
	
	

}
