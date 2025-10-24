/**
 * 
 */
package it.unical.logic_santos.gameplay;

/**
 * @author Agostino
 *
 */
public class IdentifierGenerator {

	private static IdentifierGenerator instance = null;
	private int nextIdentifier=0;
	
	public static IdentifierGenerator getInstance() {
		if ( instance==null )
			instance = new IdentifierGenerator();
		return instance;
	}
	
	private IdentifierGenerator() {
		this.nextIdentifier = 0;
	}
	
	public int getNextIdentifier() {
		final int id = nextIdentifier;
		nextIdentifier++;
		return id;
	}
	
	public void reset() {
		nextIdentifier = 0;
	}
}
