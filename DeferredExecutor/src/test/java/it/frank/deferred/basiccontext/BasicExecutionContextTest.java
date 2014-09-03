package it.frank.deferred.basiccontext;

import it.frank.deferred.DeferredExecutor;
import it.frank.deferred.ExecutionContext;
import it.frank.deferred.ExecutionWarning;
import it.frank.deferred.Task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicExecutionContextTest {

	private BasicExecutionContext context;
	private DeferredExecutor executor;
	
	@Before
	public void setUp() {
		context = new BasicExecutionContext();
	}
	
	@Test
	public void testFirstStep() {
		context.setNumberOfTasks(3);
		context.setStepAndInfo(1, null);
		Assert.assertTrue(context.isFirstStep());
		Assert.assertFalse(context.isLastStep());
		
		context.setStepAndInfo(2, null);
		Assert.assertFalse(context.isFirstStep());
		Assert.assertFalse(context.isLastStep());
		
		context.setStepAndInfo(3, null);
		Assert.assertFalse(context.isFirstStep());
		Assert.assertTrue(context.isLastStep());
	}
	
	/**
	 * Test that the task receive the correct information about the order execution
	 */
	@Test
	public void testContextInfo() {
		Task task1 = new Task() {

			public Task perform(ExecutionContext context) {
				Assert.assertTrue(context.isFirstStep());
				Assert.assertFalse(context.isLastStep());
				Assert.assertTrue(context.getStep() == 1);
				return this;
			}

			public TaskInfo getTaskInfo() {				
				return null;
			}

			public boolean blockOnError() {				
				return true;
			}
		};

		Task task2 = new Task() {

			public Task perform(ExecutionContext context) {
				Assert.assertFalse(context.isFirstStep());
				Assert.assertFalse(context.isLastStep());
				Assert.assertTrue(context.getStep() == 2);
				return this;
			}

			public TaskInfo getTaskInfo() {				
				return null;
			}
			
			public boolean blockOnError() {				
				return true;
			}
		};	
		
		Task task3 = new Task() {

			public Task perform(ExecutionContext context) {
				Assert.assertFalse(context.isFirstStep());
				Assert.assertTrue(context.isLastStep());
				Assert.assertTrue(context.getStep() == 3);
				return this;
			}

			public TaskInfo getTaskInfo() {				
				return null;
			}
			
			public boolean blockOnError() {				
				return true;
			}
		};	
		
		executor = DeferredExecutor.create(new BasicExecutionContextFactory());
		executor.add(task1);
		executor.add(task2);
		executor.add(task3);
		
		executor.execute();
	}
	
	@Test
	public void testWarningsForLastStep() {

		context.setStepAndInfo(1, null);

		Assert.assertTrue(context.getWarningsForLastStep().size() == 0);

		// =========================================================

		context.setStepAndInfo(2, null);
		context.addWarning(new ExecutionWarning("1"));

		Assert.assertTrue(context.getWarningsForLastStep().size() == 1);

		Assert.assertEquals(context.getWarningsForLastStep().get(0).getMessage(), "1");
		
		
		// =========================================================

		context.setStepAndInfo(3, null);
		context.addWarning(new ExecutionWarning("2"));
		context.addWarning(new ExecutionWarning("3"));
		Assert.assertTrue(context.getWarningsForLastStep().size() == 2);
		
		Assert.assertEquals(context.getWarningsForLastStep().get(0).getMessage(), "2");
		Assert.assertEquals(context.getWarningsForLastStep().get(1).getMessage(), "3");
	}
}
