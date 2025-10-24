/**
 * 
 */
package it.unical.logic_santos.io.universe;

import it.unical.logic_santos.universe.AbstractWorld;

/**
 * @author Agostino
 *
 */
public interface IWorldReader {

	public boolean readWorldFromEditor(AbstractWorld world);
	public boolean readWorld(AbstractWorld world);
}
