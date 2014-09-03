package it.frank.deferred;

/**
 * Interface that must be implemented by observers of the execution.
 * The {@link ExecutionContext} object contains all the informations needed.
 * 
 * @author f.torriani
 *
 */
public interface ExecutionObserver {

	public void executionWillStart(ExecutionContext context);
	
	/**
	 * Notifies observers of a task about to start. The current step can be obtained by calling
	 * {@link ExecutionContext#getStep()}, while with {@link ExecutionContext#getTaskInfo()} user can
	 * get the basic informations of the task that is going to be executed
	 * 
	 * @param context
	 */
	public void taskWillStart(ExecutionContext context);
	
	/**
	 * Notifies observers of a task completion with success, or at least with some warnings. 
	 * To get the warning fired by the last step
	 * user can use the {@link ExecutionContext#getWarningsForLastStep()}. To get all the warnings
	 * user can use the method {@link ExecutionContext#getWarnings()}. 
	 * 
	 * @param context
	 */
	public void taskEnded(ExecutionContext context);
	
	/**
	 * Notifies observers of task failure
	 * @param context
	 */
	public void taskFailed(ExecutionContext context);
	
	/**
	 * Notifies observer about the warning fired. To get the warning fired by the last step
	 * user can use the {@link ExecutionContext#getWarningsForLastStep()}. To get all the warnings
	 * user can use the method {@link ExecutionContext#getWarnings()}
	 * 
	 * @param context
	 */
	public void warningFired(ExecutionContext context);
	
	/**
	 * Notifies observers of the end of an execution
	 * 
	 * @param status
	 */
	public void executionEnded(ExecutionStatus status);
}
