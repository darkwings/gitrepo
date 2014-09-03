package it.frank.deferred.basiccontext;

import it.frank.deferred.DeferredExecutionContext;
import it.frank.deferred.ExecutionContextFactory;

public class BasicExecutionContextFactory implements ExecutionContextFactory {

	public DeferredExecutionContext build() {
		return new BasicExecutionContext();
	}

}
