/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;

/**
 * @author Agostino
 *
 */
public class WidgetElementController implements Controller {

	private Element widgetElement=null;
	static  String widgetElementId="";
	
	
	public WidgetElementController() {}
	
	@Override
	public void bind(Nifty nifty, Screen screen, Element element, Parameters parameters) {
		this.widgetElement = element;
		widgetElementId = this.widgetElement.getId();
	}

	@Override
	public void init(Parameters arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean inputEvent(NiftyInputEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onFocus(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	
	public void onTakeMission() {
		widgetElementId = widgetElement.getId();
	}
}
