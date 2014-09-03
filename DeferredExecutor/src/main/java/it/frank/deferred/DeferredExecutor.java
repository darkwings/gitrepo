package it.frank.deferred;

import it.frank.deferred.ExecutionStatus.Result;
import it.frank.deferred.basiccontext.BasicExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The actual executor of the tasks.
 * 
 * @author f.torriani
 *
 */
public class DeferredExecutor {

	private List<Task> tasks;
 	
	private List<ExecutionObserver> observers;
 	
 	private ExecutionContextFactory executionContextFactory;
 	
	private DeferredExecutor(ExecutionContextFactory executionContextFactory) {
		super();
		tasks = new ArrayList<Task>();
		observers = new ArrayList<ExecutionObserver>();
		this.executionContextFactory = executionContextFactory;
	}
	
	/**
	 * Creates a new executor with the default {@link ExecutionContextFactory}
	 * @return
	 */
	public static DeferredExecutor create() {
		return new DeferredExecutor(new ExecutionContextFactory() {
			
			public DeferredExecutionContext build() {
				return new BasicExecutionContext();
			}
		});
	}
	
	public static DeferredExecutor create(ExecutionContextFactory executionContextFactory) {
		return new DeferredExecutor(executionContextFactory);
	}
	
	/**
	 * 
	 * @param task
	 * @throws IllegalArgumentException if task null
	 */
	public void add(Task task) {
		if (task == null) {
			throw new IllegalArgumentException("task could not be null");
		}
		tasks.add(task);
	}
	
	/**
	 * 
	 * @param otherTasks
	 * @throws IllegalArgumentException if otherTasks null or empty
	 */
	public void addAll(List<Task> otherTasks) {
		if (otherTasks == null || otherTasks.isEmpty()) {
			throw new IllegalArgumentException("task list could not be null or empty");
		}
		tasks.addAll(otherTasks);
	}
	
	public void addObserver(ExecutionObserver observer) {
		observers.add(observer);
	}
	
	public ExecutionStatus execute() {
		if (tasks == null) {
			return ExecutionStatus.ko();
		}
		
		if (tasks.isEmpty()) {
			return ExecutionStatus.ko();		
		}
				
		DeferredExecutionContext context = executionContextFactory.build();
		int size = tasks.size();
		context.setNumberOfTasks(size);
		executionWillStart(context);
		for (int step = 0; step < size; step++) {
			int humanReadingStep = step + 1;
			Task task = tasks.get(step);
			if (task == null) {
				// should not happen because of the controls in the "add" methods
				continue; 
			}
			
			try {
				context.setStepAndInfo(humanReadingStep, task.getTaskInfo());
				
				taskWillStart(context);	
				
				int numberOfWarningBeforeTask = context.getWarnings().size();
				task.perform(context);
				int numberOfWarningAfterTask = context.getWarnings().size();
				if (numberOfWarningAfterTask > numberOfWarningBeforeTask) {
					warningFired(context);
				}
				
				taskEnded(context);
			} 
			catch (TaskExecutionException e) {
				e.printStackTrace(); // TODO: logging				
				taskFailed(context);
				if (task.blockOnError()) {
					ExecutionStatus status = handleBlockingError(context, e);
					return status;
				}
				else {
					ExecutionWarning warning = new ExecutionWarning("Task failed with error: " + e.getLocalizedMessage());
					context.addWarning(warning);
					warningFired(context);
				}
			}
			catch (Exception e) {
				e.printStackTrace(); // TODO: logging				
				taskFailed(context);
				ExecutionStatus status = handleBlockingError(context, e);
				return status;
				
			}
		}
		
		ExecutionStatus status =  ExecutionStatus.create(Result.OK, context);
		executionEnded(status);
		return status;
	}

	private ExecutionStatus handleBlockingError(DeferredExecutionContext context, Exception e) {
		context.setExecutionError(new ExecutionError(e));
		ExecutionStatus status = ExecutionStatus.create(Result.KO, context);
		executionEnded(status);
		return status;
	}
	
	private void warningFired(ExecutionContext context) {
		for (ExecutionObserver o : observers) {
			o.warningFired(context);
		}		
	}

	private void executionWillStart(ExecutionContext context) {
		for (ExecutionObserver o : observers) {
			o.executionWillStart(context);
		}
	}
	
	private void taskWillStart(ExecutionContext context) {
		for (ExecutionObserver o : observers) {
			o.taskWillStart(context);
		}
	}
	
	private void taskEnded(ExecutionContext context) {
		for (ExecutionObserver o : observers) {
			o.taskEnded(context);
		}
	}
	
	private void taskFailed(ExecutionContext context) {
		for (ExecutionObserver o : observers) {
			o.taskFailed(context);
		}
	}
	
	private void executionEnded(ExecutionStatus status) {
		for (ExecutionObserver o : observers) {
			o.executionEnded(status);
		}
	}
}
