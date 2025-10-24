/**
 * 
 */
package it.unical.logic_santos.environment;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * @author Agostino
 *
 */
public class Calendar {

	private DayClock dayClock=null;
	
	
	public Calendar() {
		dayClock = new DayClock();
	}


	public DayClock getDayClock() {
		return dayClock;
	}


	public void setDayClock(DayClock dayClock) {
		this.dayClock = dayClock;
	}
	
	public DayOfWeek getDayOfWeek() {
		return LocalDate.now().getDayOfWeek();
	}
	
	public int getDayOfMonth() {
		return LocalDate.now().getDayOfMonth();
	}
	
	public int getDayOfYear() {
		return LocalDate.now().getDayOfYear();
	}
	
	public int getMonth() {
		return LocalDate.now().getMonthValue();
	}
	
	public int getYear() {
		return LocalDate.now().getYear();
	}
	
	public LocalDate getCurrentLocalDate() {
		return LocalDate.now();
	}
	
	public String getHashCalendar() {
		return (getDayOfMonth() + " " + getMonthString(getMonth()) + " " + getYear() + " - " + dayClock.getHashClock());
	}
	
	public String getHashClock() {
		return dayClock.getHashClock();
	}
	
	public String stringifyClock() {
		dayClock.setCurrentRealTime();
		return dayClock.getUSAHashClock( false );
	}
	
	@Override
	public String toString() {
		return getHashCalendar();
	}
	
	public static final String getMonthString(final int month) {
		switch(month) {
		case 1:  return "January";
		case 2:  return "February";
		case 3:  return "March";
		case 4:  return "April";
		case 5:  return "May";
		case 6:  return "June";
		case 7:  return "July";
		case 8:  return "August";
		case 9:  return "September";
		case 10: return "October";
		case 11: return "November";
		case 12: return "December";
		}
		return "";
	}
	
	
	
}
