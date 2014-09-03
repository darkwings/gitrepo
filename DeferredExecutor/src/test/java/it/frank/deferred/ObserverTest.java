package it.frank.deferred;

import it.frank.deferred.ExecutionStatus.Result;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * Tests the internal mechanism to communicate errors and related warnings. Tests also
 * with mocks the observer flow
 * 
 * @author ubuntu
 */
public class ObserverTest {

	private DeferredExecutor executor;
	
	private DeferredExecutionContext context;
	private ExecutionContextFactory factory;
	private ExecutionObserver observer;
	
	@Before
	public void setUp() {
		context = Mockito.mock(DeferredExecutionContext.class);
		factory = Mockito.mock(ExecutionContextFactory.class);
		observer = Mockito.mock(ExecutionObserver.class);
		
		Mockito.when(factory.build()).thenReturn(context);
		executor = DeferredExecutor.create(factory);			
	}
	
	@After
	public void dismiss() {
		executor = null;
	}
	
	@Test
	public void testExecutionContext_StepsAndObserver_AllOk() {
					
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform(context)).thenReturn(task1);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform(context)).thenReturn(task2);
		
		executor.add(task1);
		executor.add(task2);
		executor.addObserver(observer);
		
		ExecutionStatus status = executor.execute();
		
		Mockito.verify(context).setNumberOfTasks(2);
		InOrder order = Mockito.inOrder(task1, task2, context, observer);
		order.verify(observer).executionWillStart(context);
		
		order.verify(context).setStepAndInfo(1, task1.getTaskInfo());
		order.verify(observer).taskWillStart(context);
		order.verify(task1).perform(context);
		order.verify(observer).taskEnded(context);
		
		order.verify(context).setStepAndInfo(2, task2.getTaskInfo());
		order.verify(observer).taskWillStart(context);
		order.verify(task2).perform(context);				
		order.verify(observer).taskEnded(context);
		
		ArgumentCaptor<ExecutionStatus> argumentStatus = ArgumentCaptor.forClass(ExecutionStatus.class);
		order.verify(observer).executionEnded(argumentStatus.capture());	
		Assert.assertTrue(argumentStatus.getValue().getResult() == Result.OK);
		Assert.assertEquals(argumentStatus.getValue().getContext(), context);	
		
		Assert.assertTrue(status.getResult() == Result.OK);
	}
	
	@Test
	public void testExecutionContext_StepsAndObserver_FirstFail() {
					
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform(context)).thenThrow(new TaskExecutionException("Task execution exception"));
		Mockito.when(task1.blockOnError()).thenReturn(Boolean.TRUE);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform(context)).thenReturn(task2);
	
		
		executor.add(task1);
		executor.add(task2);
		executor.addObserver(observer);
		
		ExecutionStatus status = executor.execute();
		
		Mockito.verify(context).setNumberOfTasks(2);
		
		InOrder order = Mockito.inOrder(task1, task2, context, observer);
		order.verify(observer).executionWillStart(context);
		
		order.verify(context).setStepAndInfo(1, task1.getTaskInfo());
		order.verify(observer).taskWillStart(context);
		order.verify(task1).perform(context);
		
		order.verify(observer).taskFailed(context);
		
		ArgumentCaptor<ExecutionError> argumentEx = ArgumentCaptor.forClass(ExecutionError.class);
		order.verify(context).setExecutionError(argumentEx.capture());
		Assert.assertEquals(argumentEx.getValue().getError().getMessage(), "Task execution exception");
		
			
		ArgumentCaptor<ExecutionStatus> argumentStatus = ArgumentCaptor.forClass(ExecutionStatus.class);
		order.verify(observer).executionEnded(argumentStatus.capture());	
		Assert.assertTrue(argumentStatus.getValue().getResult() == Result.KO);
		Assert.assertEquals(argumentStatus.getValue().getContext(), context);
		
		Assert.assertTrue(status.getResult() == Result.KO);
	}
	
	@Test
	public void testExecutionContext_StepsAndObserver_FirstFail_NoBlock() {
					
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform(context)).thenThrow(new TaskExecutionException("Task execution exception"));
		Mockito.when(task1.blockOnError()).thenReturn(Boolean.FALSE);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform(context)).thenReturn(task2);
	
				
		executor.add(task1);
		executor.add(task2);
		executor.addObserver(observer);	
		
		ExecutionStatus status = executor.execute();
		
		Mockito.verify(context).setNumberOfTasks(2);
		
		InOrder order = Mockito.inOrder(task1, task2, context, observer);
		order.verify(observer).executionWillStart(context);
		
		order.verify(context).setStepAndInfo(1, task1.getTaskInfo());
		order.verify(observer).taskWillStart(context);
		order.verify(task1).perform(context);
				
		order.verify(observer).taskFailed(context);
		
		ArgumentCaptor<ExecutionWarning> argumentW = ArgumentCaptor.forClass(ExecutionWarning.class);
		order.verify(context).addWarning(argumentW.capture());
		Assert.assertEquals(argumentW.getValue().getMessage(), "Task failed with error: Task execution exception");
		
		order.verify(observer).warningFired(context);
		
		Assert.assertTrue(status.getResult() == Result.OK);
	}
	
	
	protected void prepareTwoTasks() {
		Task task1 = Mockito.mock(Task.class);
		Mockito.when(task1.perform(context)).thenReturn(task1);
		Task task2 = Mockito.mock(Task.class);
		Mockito.when(task2.perform(context)).thenReturn(task2);
		
		executor.add(task1);
		executor.add(task2);
	}
	
	
}
