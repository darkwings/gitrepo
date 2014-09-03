package it.frank.deferred;

/**
 * Represents a warning in the execution that has to be notified to the user
 * 
 * @author ubuntu
 *
 */
public class ExecutionWarning {

	/**
	 * The warning message
	 */
	private String message;
	
	/**
	 * The step that generates the warning
	 */
	private Integer step;
	
	public ExecutionWarning(Integer step, String message) {
		super();
		this.message = message;
		this.step = step;
	}
	
	public ExecutionWarning(String message) {
		super();
		this.message = message;		
	}

	public ExecutionWarning() {
		super();
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExecutionWarning [message=");
		builder.append(message);
		builder.append(", step=");
		builder.append(step);
		builder.append("]");
		return builder.toString();
	}
	
	
}
