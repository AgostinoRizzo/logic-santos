/**
 * 
 */
package it.unical.logic_santos.environment;

import java.time.LocalDateTime;

/**
 * @author Agostino
 *
 */
public class DayClock {

	//private byte hours;
	//private byte minutes;
	private int seconds;
	
	private float dtCounter;
	
	private int secondsInOneRealSecond;
	
	public static final String AM = "AM";
	public static final String PM = "PM";
	
	
	
	public DayClock() {
		this.seconds=0;
		this.dtCounter=0.0f;
		this.secondsInOneRealSecond=1;
	}
	
	public DayClock(final byte _hours, final byte _minutes, final byte _seconds) {
		this.seconds=0;
		if (goodSeconds(_seconds))
			this.seconds=_seconds;
		if (goodMinutes(_minutes))
			this.seconds += _minutes*60;
		if (goodHours(_hours))
			this.seconds += _hours*3600;
		this.seconds = this.seconds % 86400;
		this.dtCounter=0.0f;
		this.secondsInOneRealSecond=1;
	}
	
	public DayClock(final byte _hours, final byte _minutes, final byte _seconds, final int _secondsInOneRealSecond) {
		this.seconds=0;
		if (goodSeconds(_seconds))
			this.seconds=_seconds;
		if (goodMinutes(_minutes))
			this.seconds += _minutes*60;
		if (goodHours(_hours))
			this.seconds += _hours*3600;
		this.seconds = this.seconds % 86400;
		this.dtCounter=0.0f;
		if (_secondsInOneRealSecond >= 1)
			this.secondsInOneRealSecond = _secondsInOneRealSecond;
		else
			this.secondsInOneRealSecond=1;
	}
	
	public DayClock(final DayClock c) {
		this.seconds=c.seconds;
		this.dtCounter=c.dtCounter;
		this.secondsInOneRealSecond=c.secondsInOneRealSecond;
	}

	public byte getHours() {
		return (byte)(seconds/3600);
	}

	public byte getMinutes() {
		return (byte)((seconds%3600)/60);
	}

	public byte getSeconds() {
		return (byte)(seconds%60);
	}
	
	public void setTime(final byte _hours, final byte _minutes, final byte _seconds) {
		this.seconds=0;
		if (goodSeconds(_seconds))
			this.seconds=_seconds;
		if (goodMinutes(_minutes))
			this.seconds += _minutes*60;
		if (goodHours(_hours))
			this.seconds += _hours*3600;
		this.seconds = this.seconds % 86400;
	}
	
	public int getSecondsInOneRealSecond() {
		return secondsInOneRealSecond;
	}
	
	public boolean setSecondsInOneRealSecond(final int _secondsInOneRealSecond) {
		if (_secondsInOneRealSecond >= 1) {
			this.secondsInOneRealSecond = _secondsInOneRealSecond;
			return true;
		} else
			return false;
	}
	
	public void resetToNoon() {
		seconds=43200;
		resetDtCounter();
	}
	
	public void resetToMidnight() {
		seconds=0;
		resetDtCounter();
	}
	
	public void resetDtCounter() {
		dtCounter=0.0f;
	}
	
	public void update(final float dt) { /* dt expressed in seconds */
		dtCounter += dt;
		if (dtCounter >= 1.0f) {
			increaseSeconds(secondsInOneRealSecond);
			dtCounter -= 1.0f;
		}
	}
	
	public void increaseSeconds() {
		seconds++;
		seconds = seconds % 86400;
	}
	
	public void increaseMinutes() {
		seconds+=60;
		seconds = seconds % 86400;
	}
	
	public void increaseHours() {
		seconds+=3600;
		seconds = seconds % 86400;
	}
	
	public void increaseSeconds(final int s) {
		if (s >= 0) {
			seconds+=s;
			seconds = seconds % 86400;
		}
	}
	
	public void increaseMinutes(final int m) {
		if (m >= 0) {
			seconds += m*60;
			seconds = seconds % 86400;
		}
	}
	
	public void increaseHours(final int h) {
		if (h >= 0) {
			seconds += h*3600;
			seconds = seconds % 86400;
		}
	}
	
	public void setCurrentRealTime() {
		seconds =  LocalDateTime.now().getSecond();
		seconds += LocalDateTime.now().getMinute()*60;
		seconds += LocalDateTime.now().getHour()*3600;
	}
	
	
	@Override
	public String toString() {
		return getHashClock();
	}
	
	public String getHashClock() {
		
		String h = toString(getHours());
		String m = toString(getMinutes());
		String s = toString(getSeconds());
		
		if (h.length()==1)
			h = "0" + h;
		if (m.length()==1)
			m = "0" + m;
		if (s.length()==1)
			s = "0" + s;
		
		return (h+":"+m+":"+s);
	}
	
	public String getUSAHashClock( final boolean showSeconds ) {
		
		String ampm = "";
		final int tmpSeconds = seconds;
		if (seconds < 46800) {
			ampm = AM;
		} else {
			ampm = PM;
			seconds -= 46800;
		}
		
		String h = toString(getHours());
		String m = toString(getMinutes());
		String s = (showSeconds) ? toString(getSeconds()) : "";
		
		if (h.length()==1)
			h = "0" + h;
		if (m.length()==1)
			m = "0" + m;
		if (s.length()==1)
			s = "0" + s;
		
		seconds = tmpSeconds;
		if ( showSeconds )
			return (h+":"+m+":"+s+" "+ampm);
		return (h+":"+m+" "+ampm);
	}
	
	private static String toString(byte n) {
		if (n<0)
			return "";
		else if (n == 0)
			return "0";
		String ans = "";
		int r;
		while(n>0) {
			r = n%10;
			n/=10;
			ans = getDigit(r) + ans; 
		}
		return ans;
	}
	
	private static String getDigit(final int n) {
		switch(n) {
		case 0: return "0";
		case 1: return "1";
		case 2: return "2";
		case 3: return "3";
		case 4: return "4";
		case 5: return "5";
		case 6: return "6";
		case 7: return "7";
		case 8: return "8";
		case 9: return "9";
		}
		return "0";
	}
	
	private static boolean goodSeconds(final byte seconds) {
		return ((seconds>=0) && (seconds<=59));
	}
	
	private static boolean goodMinutes(final byte minutes) {
		return ((minutes>=0) && (minutes<=59));
	}
	
	private static boolean goodHours(final byte hours) {
		return ((hours>=0) && (hours<=23));
	}
	
}
