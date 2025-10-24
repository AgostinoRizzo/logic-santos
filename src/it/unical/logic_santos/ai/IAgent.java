/**
 * 
 */
package it.unical.logic_santos.ai;

/**
 * @author Agostino
 * The IAgent interface represents an interface to define and use an artificial intelligent agent that
 * acts in the game environment making reasonings
 * The methods "think" are used to invoke the reasoning processes of the agent 
 */
public interface IAgent {

	public void think();
	public void think( final float tpf );
}
