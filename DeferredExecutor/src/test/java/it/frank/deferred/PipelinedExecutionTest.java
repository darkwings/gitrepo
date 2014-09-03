package it.frank.deferred;

import it.frank.deferred.ExecutionStatus.Result;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests the pipeline of data that are passed from one task to the next 
 * and the warnings fired by the task itself
 * @author ubuntu
 *
 */
public class PipelinedExecutionTest {

	private DeferredExecutor executor;
	private ExecutionObserver observer;
	
	@Before
	public void setUp() {
		observer = Mockito.mock(ExecutionObserver.class);
		
		executor = DeferredExecutor.create();	
		executor.addObserver(observer);
	}
	
	@After
	public void dismiss() {
		executor = null;
	}
	
	static class Data {
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
				
	}
	
	@Test
	public void testDataPassage() {
		
		Task task1 = new Task() {

			public TaskInfo getTaskInfo() {				
				return null;
			}

			public Task perform(ExecutionContext context) {				
				Assert.assertNull(context.getTaskData());
				Data data = new Data();
				data.setMessage("Message from task1");
				context.setTaskData(data);
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		Task task2 = new Task() {

			public TaskInfo getTaskInfo() {				
				return null;
			}

			public Task perform(ExecutionContext context) {		
				Assert.assertNotNull(context.getTaskData());
				Assert.assertTrue((context.getTaskData() instanceof Data));
				
				Data data = (Data) context.getTaskData();
				Assert.assertEquals(data.getMessage(), "Message from task1");
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		
		executor.add(task1);
		executor.add(task2);
		
		
		ExecutionStatus status = executor.execute();
		Assert.assertTrue(status.getResult() == Result.OK);
	}
	
	
	@Test
	public void testTaskWarnings() {
		
		Task task1 = new Task() {

			public TaskInfo getTaskInfo() {				
				return null;
			}

			public Task perform(ExecutionContext context) {				
				Assert.assertNotNull(context.getWarnings());
				Assert.assertTrue(context.getWarnings().size() == 0);
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		Task task2 = new Task() {

			public TaskInfo getTaskInfo() {				
				return null;
			}

			public Task perform(ExecutionContext context) {		
				context.addWarning(new ExecutionWarning("Warning from task 2"));
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		Task task3 = new Task() {

			public TaskInfo getTaskInfo() {				
				return null;
			}

			public Task perform(ExecutionContext context) {		
				context.addWarning(new ExecutionWarning("First warning from task 3"));
				context.addWarning(new ExecutionWarning("Second warning from task 3"));
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		ExecutionObserver observer = new ExecutionObserver() {
			
			public void warningFired(ExecutionContext context) {
				if (context.getStep() == 1) {
					Assert.assertTrue(context.getWarnings().isEmpty());
				}
				else if (context.getStep() == 2) {
					Assert.assertTrue(context.getWarnings().size() == 1);
					
					Assert.assertTrue(context.getWarningsForLastStep().size() == 1);
					
					Assert.assertEquals(context.getWarningsForLastStep().get(0).getMessage(), "Warning from task 2");
				}
				else if (context.getStep() == 3) {
					Assert.assertTrue(context.getWarnings().size() == 3);
					
					Assert.assertTrue(context.getWarningsForLastStep().size() == 2);
					
					Assert.assertEquals(context.getWarningsForLastStep().get(0).getMessage(), "First warning from task 3");
					Assert.assertEquals(context.getWarningsForLastStep().get(1).getMessage(), "Second warning from task 3");
				}
			}
			
			public void taskWillStart(ExecutionContext context) {
				
			}
			
			public void taskFailed(ExecutionContext context) {
				
			}
			
			public void taskEnded(ExecutionContext context) {
				
			}
			
			public void executionWillStart(ExecutionContext context) {
				
			}
			
			public void executionEnded(ExecutionStatus status) {
				
			}
		};
		
		executor.add(task1);
		executor.add(task2);
		executor.add(task3);	
		
		executor.addObserver(observer);
		
		ExecutionStatus status = executor.execute();
		Assert.assertTrue(status.getResult() == Result.OK);
	}
}
