/**
 * 
 */
package it.unical.logic_santos.gameplay;

/**
 * @author Agostino
 *
 */
public interface IObserver {

	public void onStateShanged();
	public void onStateShanged( ISubject subject );
}
