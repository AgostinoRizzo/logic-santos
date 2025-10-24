/**
 * 
 */
package it.unical.logic_santos.gui.screen;

/**
 * @author Agostino
 *
 */
public interface IScreen {

	public void initComponents();
	public void finalizeComponents();
	
	public void update( final float tpf );
	
}
