/**
 * 
 */
package it.unical.logic_santos.mission;

import it.unical.logic_santos.logging.ILoggingObserver;

/**
 * @author Agostino
 * The IMission interface represents the way the core of the game applies and checks a game Mission.
 * A game Mission is composed by a description, some details, and the amount of money that the Player earns 
 * when the Mission is compete.
 * The Mission class acts as an Observer of a game Logger: the method onLoggerEvent( final ILoggerEvent loggerEvent ) of the
 * ILoggingObserver interface is called when a new Logger event is detected in the game Logger ( Subject ).
 * A Mission is complete when its condition if verified:
 * 	- the methods check() and check( final float tpf ) allow the concrete Mission class to check 
 *    if the Mission is complete.
 */
public interface IMission extends ILoggingObserver {

	/** the methods allow the concrete Mission class to check if the Mission is complete.
	 * 
	 * @return true if the mission is complete after the verification of the Mission condition
	 */
	public boolean check();
	
	/** the methods allow the concrete Mission class to check if the Mission is complete.
	 * 
	 * @param tpf: time in seconds past from last call
	 * @return true if the mission is complete after the verification of the Mission condition
	 */
	public boolean check( final float tpf );
	
	/** the method returns true if the Mission condition is already verified.
	 * 
	 * @return true if the Mission condition is already verified.
	 */
	public boolean isComplete();
	
	/** the method returns the name of the Mission
	 * 
	 * @return the name of the Mission
	 */
	public String getName();
	
	/** the method returns the Company name of the creator of that Mission.
	 * 
	 * @return the Company name of the creator of that Mission.
	 */
	public String getCompanyName();
	
	/** the method returns the description of the Mission ( conditions, what the Player has to to, etc ).
	 * 
	 * @return returns the description of the Mission ( conditions, what the Player has to to, etc ).
	 */
	public String getDescription();
	
	/** the method returns the amount of money the player earns if he complete the mission.
	 * 
	 * @return the amount of money the player earns if he complete the mission.
	 */
	public float getGain();
	
	/** the method returns a new Mission clone instance.
	 * 
	 * @return new Mission clone instance.
	 */
	public IMission cloneMission();
	
}
