package it.frank.deferred;


/** 
 * Represents the status of an execution.
 * @author f.torriani
 *
 */
public class ExecutionStatus {

	
	public static enum Result {
		OK, KO;
	}
	
	protected Result result;
	protected ExecutionContext context;
	
	public static ExecutionStatus create(Result result, ExecutionContext context) {
		return new ExecutionStatus(result, context);
	}
	
	public static ExecutionStatus ok()   {
		return new ExecutionStatus(Result.OK, null);
	}
	
	public static ExecutionStatus ko()   {
		return new ExecutionStatus(Result.KO, null);
	}
	
	private ExecutionStatus(Result result, ExecutionContext context) {
		this.result = result;
		this.context = context;
	}

	public Result getResult() {
		return result;
	}

	public ExecutionContext getContext() {
		return context;
	}
	
	
}
