/**
 * 
 */
package it.unical.logic_santos.gameplay;

/**
 * @author Agostino
 *
 */
public class LifeBar {

	public static final float FULL_VALUE = 1.0f;
	public static final float EMPTY_VALUE = 0.0f;
	public static final LifeBar FULL_BAR = new LifeBar( FULL_VALUE );
	public static final float INCREASE_BY_TIME_VALUE_PER_SEC = 0.01f;
	
	protected float value=FULL_VALUE;
	protected float increaseByTimeAmount=0.0f;
	
	public LifeBar() {
		this.value = FULL_VALUE;
	}
	
	public LifeBar( final float value ) {
		this.initValue(value);
	}
	
	private void initValue( final float value ) {
		if ( value<EMPTY_VALUE )
			this.value = EMPTY_VALUE;
		else if ( value>FULL_VALUE )
			this.value = FULL_VALUE;
		else
			this.value = value;
	}
	
	public boolean isFull() {
		return ( this.value==FULL_VALUE );
	}
	
	public boolean isEmpty() {
		return ( this.value==EMPTY_VALUE );
	}
	
	public float getValue() {
		return value;
	}
	
	public void setFull() {
		this.value = FULL_VALUE;
	}
	
	public void reduce( final LifeBar reductionBar ) {
		if ( this.value>reductionBar.value )
			this.value -= reductionBar.value;
		else
			this.value = EMPTY_VALUE;
	}
	
	public void increaseByTime( final float tpf ) {
		increaseByTimeAmount+=tpf;
		if ( increaseByTimeAmount>=1.0f ) {
			increase( new LifeBar( INCREASE_BY_TIME_VALUE_PER_SEC ) );
			increaseByTimeAmount=0.0f;
		}
	}
	
	public LifeBarCookie getCookie() {
		LifeBarCookie cookie = new LifeBarCookie();
		cookie.value = this.value;
		cookie.increaseByTimeAmount = this.increaseByTimeAmount;
		return cookie;
	}
	
	public void setCookie( final LifeBarCookie cookie ) {
		this.value = cookie.value;
		this.increaseByTimeAmount = cookie.increaseByTimeAmount;
	}
	
	private void increase( final LifeBar increasenBar ) {
		this.value += increasenBar.value;
		if ( this.value>FULL_VALUE )
			this.value = FULL_VALUE;
	}
	
	
}
