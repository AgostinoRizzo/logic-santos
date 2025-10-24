/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import it.unical.logic_santos.io.universe.IOUniverseConfig;

/**
 * @author Agostino
 *
 */
public class PlayerCareerStatus {

	private float money=0.0f;
	
	public static final float  MAX_MONEY_AMOUNT = 100000.0f;
	public static final int    MAX_INT_DIGITS_COUNT = 6;
	public static final String MONEY_CURRENCY_STRING = "$";
	
	private static final Path statusFilePath = FileSystems.getDefault().
			getPath( IOUniverseConfig.PLAYER_CAREER_STATUS_FILE_PATH, "" ); 
	
	public PlayerCareerStatus() {}
	
	public float getMoney() {
		return money;
	}
	
	public void earn( final float gain ) {
		this.money+=gain;
		if ( this.money>MAX_MONEY_AMOUNT )
			this.money=MAX_MONEY_AMOUNT;
	}
	
	public boolean pay( final float cost ) {
		if ( this.money<cost )
			return false;
		this.money-=cost;
		return true;
	}
	
	public void loadStatus() {
		try {
			
			DataInputStream statusIn = new DataInputStream( Files.newInputStream( statusFilePath ) );
			if ( statusIn.available()>0 )
				readFromDataInputStream( statusIn );
			else
				initStatusToDefault();
			
			statusIn.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			initStatusToDefault();
		}
	}
	
	public void storeStatus() {
		try {
			
			DataOutputStream statusOut = new DataOutputStream( Files.newOutputStream( statusFilePath ) );
			writeOnDataOutputStream( statusOut );
			statusOut.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeOnDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat( money );
	}
	
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		money = in.readFloat();
	}
	
	public static String toStringMoney( final float money ) {
		return ( MONEY_CURRENCY_STRING + "   " + generateMoneyDigitsText( money ) );
	}
	
	public static String generateMoneyDigitsText( final float money ) {
		final String moneyText = Float.toString( money );
		final int length = moneyText.length();
		final int initialZeroDigits = MAX_INT_DIGITS_COUNT-countIntDigits( moneyText );
		
		StringBuilder moneyTextBuilder = new StringBuilder();
		boolean dotReached=false;
		int decimalDicits=0;
		int i;
		
		for( i=0; i<initialZeroDigits; ++i )
			moneyTextBuilder.append( '0' );
		
		for( i=0; ( (i<length) && (decimalDicits<2) ); ++i) {
			if ( (!dotReached) && (moneyText.charAt(i)=='.') )
				dotReached=true;
			else if ( dotReached && (moneyText.charAt(i)!='.') )
				decimalDicits++;
			
			moneyTextBuilder.append( moneyText.charAt(i) );
			
		}
		
		for( i=decimalDicits; i<2; ++i )
			moneyTextBuilder.append( '0' );
		
		return moneyTextBuilder.toString();
	}
	
	private static int countIntDigits( final String moneyText ) {
		final int length = moneyText.length();
		
		boolean dotReached=false;
		int count=0;
		for( int i=0; ( (i<length) && (!dotReached) ); ++i ) {
			if ( moneyText.charAt(i)=='.' )
				dotReached=true;
			else if ( Character.isDigit( moneyText.charAt(i)) )
				count++;
		}
		
		return count;
	}
	
	private void initStatusToDefault() {
		this.money=0.0f;
	}
}
