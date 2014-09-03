package it.frank.deferred;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/** 
 * Tests basic flow
 * 
 * @author f.torriani
 *
 */
public class DeferredExecutorTest {

	private DeferredExecutor executor;
	
	@Before
	public void prepare() {
		executor = DeferredExecutor.create();
	}
	
	@After
	public void dismiss() {
		executor = null;
	}
	
	@Test
	public void testInvalidArguments() {
		try {
			executor.add((Task) null);
			Assert.fail("Expected exception");
		} 
		catch (IllegalArgumentException e) {
			// OK
		}
		catch (Exception e) {
			Assert.fail("Expected IllegalArgumentException, not " + e.getClass().getName());
		}
		
		try {
			executor.addAll((ArrayList<Task>) null);
			Assert.fail("Expected exception");
		} 
		catch (IllegalArgumentException e) {
			// OK
		}
		catch (Exception e) {
			Assert.fail("Expected IllegalArgumentException, not " + e.getClass().getName());
		}
		
		try {
			executor.addAll(new ArrayList<Task>());
			Assert.fail("Expected exception");
		} 
		catch (IllegalArgumentException e) {
			// OK
		}
		catch (Exception e) {
			Assert.fail("Expected IllegalArgumentException, not " + e.getClass().getName());
		}
	}
	
	@Test
	public void testNoTask() {
//		List<Task> tasks = twoTasksOK();
		ExecutionStatus status = executor.execute();
		Assert.assertTrue(status.getResult() == ExecutionStatus.Result.KO);
		
	}
	
	@Test
	public void testAllOk() {
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform((ExecutionContext) Mockito.anyObject())).thenReturn(task1);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform((ExecutionContext) Mockito.anyObject())).thenReturn(task2);
		
		executor.add(task1);
		executor.add(task2);
		ExecutionStatus status = executor.execute();
				
		Assert.assertTrue(status.getResult() == ExecutionStatus.Result.OK);
	}
	
	@Test
	public void testSecondOfThreeFails() {
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform((ExecutionContext) Mockito.anyObject())).thenReturn(task1);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform((ExecutionContext) Mockito.anyObject())).thenThrow(new TaskExecutionException("Catch me"));
		Mockito.when(task2.blockOnError()).thenReturn(Boolean.TRUE);
		Task task3 = Mockito.mock(Task.class);
		Mockito.when(task3.perform((ExecutionContext) Mockito.anyObject())).thenReturn(task3);
				
		executor.add(task1);
		executor.add(task2);
		executor.add(task3);
		ExecutionStatus status = executor.execute();
		Assert.assertTrue(status.getResult() == ExecutionStatus.Result.KO);
		Assert.assertTrue(status.getContext().getStep() == 2);
		Assert.assertNotNull(status.getContext().getExecutionError());
		Assert.assertEquals(status.getContext().getExecutionError().getError().getMessage(), "Catch me");
	}
		
	@Test
	public void testSecondOfThreeFails_NoBlock() {
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform((ExecutionContext) Mockito.anyObject())).thenReturn(task1);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform((ExecutionContext) Mockito.anyObject())).thenThrow(new TaskExecutionException("Catch me"));
		Mockito.when(task2.blockOnError()).thenReturn(Boolean.FALSE);
		Task task3 = Mockito.mock(Task.class);
		Mockito.when(task3.perform((ExecutionContext) Mockito.anyObject())).thenReturn(task3);
				
		executor.add(task1);
		executor.add(task2);
		executor.add(task3);
		ExecutionStatus status = executor.execute();
		Assert.assertTrue(status.getResult() == ExecutionStatus.Result.OK);
		Assert.assertNotNull(status.getContext().getWarnings());
		Assert.assertTrue(status.getContext().getWarnings().size() == 1);
		
		ExecutionWarning warning = status.getContext().getWarnings().get(0);
		Assert.assertTrue(warning.getStep() == 2);
		Assert.assertEquals(warning.getMessage(), "Task failed with error: Catch me");
	}
}
