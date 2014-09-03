package it.frank.deferred;

import javax.naming.Context;

/**
 * Represents a task that made up a step in the process of the deferred execution
 * 
 * @author f.torriani
 *
 */
public interface Task {

	
	/**
	 * Informations about the task
	 * 
	 * @author ubuntu
	 *
	 */
	public static class TaskInfo {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}			
	}
	
	/**
	 * Returns the informations of the task
	 * @return
	 */
	public TaskInfo getTaskInfo();
	
	/**
	 * Actual execution of the task. Make sure to throw a {@link TaskExecutionException}
	 * in case of errors, because any other exception will block immediately the execution, and will
	 * not be handled as a warning.
	 * 
	 * @params {@link Context} the Execution context for this task
	 * @return this task for fluent API calls
	 * @throws TaskExecutionException in case of errors
	 */
	public Task perform(ExecutionContext context);
	
	/**
	 * Returns <code>true</code> if an execution error of the task should block the whole pipeline.
	 * If you want an {@link TaskExecutionException error} to be handled as a warning, you must
	 * return <code>false</code>.
	 * 
	 * @return <code>true</code> if an execution error of the task should block the whole pipeline.
	 * 
	 */
	public boolean blockOnError();
	
}
