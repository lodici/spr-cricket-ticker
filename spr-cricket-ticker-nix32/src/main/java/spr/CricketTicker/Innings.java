package spr.CricketTicker;

import java.math.BigDecimal;

public class Innings {
	
	public int runsScored = 0;
	public int wicketsLost = 0;
	
	// Using BigDecimal because of rounding issues when using float or double.
	// Ticker rounds following to 1 decimal place but XML source value can be 2 decimal.
	// (See Effective Java : "Avoid  float and double  if exact answers are required").		
	public BigDecimal oversBowled = new BigDecimal(0D);
	public BigDecimal runRate = new BigDecimal(0D);
	public BigDecimal requiredRunRate = new BigDecimal(0D);
	
}
