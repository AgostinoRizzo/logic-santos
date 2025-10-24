/**
 * 
 */
package it.unical.logic_santos.ai;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Agostino
 *
 */
public abstract class ThreadBasedReasoning extends Thread implements IReasoning {

	protected boolean isAvailable=false;
	protected Lock lock = new ReentrantLock();
	protected Condition solutionAvailable = lock.newCondition();
	
	@Override
	public void execute() {
		setAvailability( false );
		this.start();
	}
	
	@Override
	public void run() {
		reasoning();
		setAvailability( true );
		lock.lock();
		solutionAvailable.signalAll();
		lock.unlock();
	}
	
	/**the method check the availability of the solution 
	 * the method is not blocking
	 * @return true if the solution is available (computed), false otherwise
	 */
	public boolean poll() {
		lock.lock();
		try {
			return isAvailable;
		} finally {
			lock.unlock();
		}
	}
	
	private void setAvailability( final boolean value ) {
		lock.lock();
		try {
			isAvailable = value;
		} finally {
			lock.unlock();
		}
	}
	
}
