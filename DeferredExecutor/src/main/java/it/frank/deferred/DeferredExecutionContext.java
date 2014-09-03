package it.frank.deferred;

import it.frank.deferred.Task.TaskInfo;

/**
 * Extends the {@link ExecutionContext} to include methods that will be used only by the
 * {@link DeferredExecutor} to set the number of tasks, the informations of the running task
 * and the blocking error
 *  
 * @author ubuntu
 *
 */
public interface DeferredExecutionContext extends ExecutionContext {

	/**
	 * The {@link DeferredExecutor} use this method to set the number of tasks
	 * 
	 * @param numberOfTasks
	 */
	public void setNumberOfTasks(Integer numberOfTasks);
	
	/**
	 * Sets the current step number and the basic info of the task
	 * 
	 * @param step
	 * @param taskInfo
	 */
	public void setStepAndInfo(Integer step, TaskInfo taskInfo);
	
	/**
	 * Sets an error. Implementations of this class should bind the error to the step
	 * 
	 * @param warning
	 */
	public void setExecutionError(ExecutionError error);
}
