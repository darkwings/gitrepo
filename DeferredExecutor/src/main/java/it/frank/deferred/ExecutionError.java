package it.frank.deferred;

/**
 * Represents a blocking error in the execution
 * 
 * @author ubuntu
 *
 */
public class ExecutionError {

	/**
	 * The step that generates the error
	 */
	private Integer step;
	
	/**
	 * The error
	 */
	private Exception error;
	
	public ExecutionError() {
		super();
	}

	public ExecutionError(Exception error) {
		super();
		this.error = error;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExecutionError [step=");
		builder.append(step);
		builder.append(", error=");
		builder.append(error);
		builder.append("]");
		return builder.toString();
	}

	
	
}
