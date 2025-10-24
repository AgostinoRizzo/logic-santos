/**
 * 
 */
package it.unical.logic_santos.ai;

/**
 * @author Agostino
 * The IReasoning interface is used to define and use a generic Reasoning process took by the Agent
 */
public interface IReasoning {

	/** starts the execution of the reasoning process */
	public void execute();
	
	/** this method contains the entire reasoning process code */
	public void reasoning();
}
