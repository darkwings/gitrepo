package it.frank.deferred.basiccontext;

import it.frank.deferred.DeferredExecutionContext;
import it.frank.deferred.ExecutionError;
import it.frank.deferred.ExecutionWarning;
import it.frank.deferred.Task.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public class BasicExecutionContext implements DeferredExecutionContext {

	private Integer currentStep;
	private TaskInfo taskInfo;
	
	private List<ExecutionWarning> warnings = new ArrayList<ExecutionWarning>();
	private ExecutionError executionError;
	
	public ExecutionError getExecutionError() {
		return executionError;
	}

	public void setExecutionError(ExecutionError executionError) {
		executionError.setStep(currentStep);
		this.executionError = executionError;
	}

	public Integer getNumberOfTasks() {
		return numberOfTasks;
	}

	private Object data;
	private Integer numberOfTasks;
		
	public void setNumberOfTasks(Integer numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}
	
	public boolean isLastStep() { 
		return currentStep == numberOfTasks;
	}

	public boolean isFirstStep() {
		return currentStep == 1;
	}


	public Integer getStep() {
		return currentStep;
	}

	public void setStepAndInfo(Integer step, TaskInfo taskInfo) {
		this.currentStep = step;
		this.taskInfo = taskInfo;
	}
		
	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public void addWarning(ExecutionWarning warning) {
		warning.setStep(currentStep);
		warnings.add(warning);
	}

	public List<ExecutionWarning> getWarnings() {		
		return warnings;
	}
		
	public List<ExecutionWarning> getWarningsForLastStep() {
		List<ExecutionWarning> r = new ArrayList<ExecutionWarning>();
		for (ExecutionWarning w : warnings) {
			if (w.getStep() == currentStep) {
				r.add(w);
			}
		}
		return r;
	}

	public Object getTaskData() {
		return data;
	}

	public void setTaskData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BasicExecutionContext [getExecutionError()=");
		builder.append(getExecutionError());
		builder.append(", getNumberOfTasks()=");
		builder.append(getNumberOfTasks());
		builder.append(", isLastStep()=");
		builder.append(isLastStep());
		builder.append(", isFirstStep()=");
		builder.append(isFirstStep());
		builder.append(", getStep()=");
		builder.append(getStep());
		builder.append(", getWarnings()=");
		builder.append(getWarnings());
		builder.append(", getData()=");
		builder.append(getTaskData());
		builder.append("]");
		return builder.toString();
	}

	
}
