/**
 * 
 */
package it.unical.logic_santos.editor.gui.components;

import javax.swing.JOptionPane;

/**
 * @author Agostino
 *
 */
public class CityNameInputDialog {

	private String cityName=null;
	
	
	public CityNameInputDialog() {
		
	}
	
	public boolean showInputDialog() {
		boolean showInputDialog = true;
		do {
			
			cityName = JOptionPane.showInputDialog(null, "Enter City Name", "Logic Santos");
			if (cityName == null) 
				showInputDialog = false;
			else {
				if (validCityName(cityName)) {
					return true;
				} else {
					JOptionPane.showMessageDialog(null, "City name not valid!");
					showInputDialog = true;
				}
			}
			
		} while(showInputDialog);
		
		return false;
	}
	
	public String getChoosenCityName() {
		return cityName;
	}
	
	private static boolean validCityName(final String cityName) {
		if (cityName == null)
			return false;
		if (cityName.length() == 0)
			return false;
		for(int i=0; i<cityName.length(); ++i)
			if ( (cityName.charAt(i) != ' ') && (!isLetter(cityName.charAt(i))) )
				return false;
		return true;
	}
	
	private static boolean isLetter(final char c) {
		return Character.isLetter(c);
	}
	
	
}
