package it.frank.deferred.observers.performance;

import it.frank.deferred.DeferredExecutor;
import it.frank.deferred.ExecutionContext;
import it.frank.deferred.ExecutionStatus;
import it.frank.deferred.ExecutionStatus.Result;
import it.frank.deferred.Task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AbstractPerformanceObserverTest {

	private DeferredExecutor executor;
	
	@Before
	public void setUp() {
		executor = DeferredExecutor.create();			
	}
	
	@Test
	public void testTaskWarnings() {
		
		Task task1 = new Task() {

			public TaskInfo getTaskInfo() {				
				TaskInfo i = new TaskInfo();
				i.setName("Task 1");
				return i;
			}

			public Task perform(ExecutionContext context) {				
				try {
					Thread.sleep(100);
				} 
				catch (InterruptedException e) {				
					e.printStackTrace();
				}
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		Task task2 = new Task() {

			public TaskInfo getTaskInfo() {				
				TaskInfo i = new TaskInfo();
				i.setName("Task 2");
				return i;
			}

			public Task perform(ExecutionContext context) {						
				try {
					Thread.sleep(230);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				return null;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		Task task3 = new Task() {

			public TaskInfo getTaskInfo() {				
				TaskInfo i = new TaskInfo();
				i.setName("Task 3");
				return i;
			}

			public Task perform(ExecutionContext context) {	
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				return this;
			}

			public boolean blockOnError() {				
				return true;
			}
			
		};
		
		AbstractPerformanceObserver observer = new AbstractPerformanceObserver() {
			
			@Override
			public void executionWillStart(ExecutionContext context) {				
				super.executionWillStart(context);
				Assert.assertTrue(startTime > 0);
			}

			@Override
			public void taskWillStart(ExecutionContext context) {
				super.taskWillStart(context);
			}

			@Override
			public void taskEnded(ExecutionContext context) {
				super.taskEnded(context);
			}

			@Override
			public void taskFailed(ExecutionContext context) {
				super.taskFailed(context);
			}

			@Override
			public void executionEnded(ExecutionStatus status) {
				super.executionEnded(status);
				Assert.assertTrue(endTime > 0);
			}

			public void warningFired(ExecutionContext context) {
				
			}					
		};
		
		executor.add(task1);
		executor.add(task2);
		executor.add(task3);	
		
		executor.addObserver(observer);
		
		ExecutionStatus status = executor.execute();
		Assert.assertTrue(status.getResult() == Result.OK);
		
		Assert.assertNotNull(observer.getExecutionInfos());
		Assert.assertTrue(observer.getExecutionInfos().size() == 3);
		
		TaskExecutionInfo info = observer.getExecutionInfos().get(0);
		Assert.assertTrue(info.getExecutionTime() > 90);
		Assert.assertTrue(info.getStep() == 1);
		Assert.assertEquals(info.getTaskInfo().getName(), "Task 1");
		
		info = observer.getExecutionInfos().get(1);
		Assert.assertTrue(info.getExecutionTime() > 220);
		Assert.assertTrue(info.getStep() == 2);
		Assert.assertEquals(info.getTaskInfo().getName(), "Task 2");
		
		info = observer.getExecutionInfos().get(2);
		Assert.assertTrue(info.getExecutionTime() > 40);
		Assert.assertTrue(info.getStep() == 3);
		Assert.assertEquals(info.getTaskInfo().getName(), "Task 3");
	}
}
