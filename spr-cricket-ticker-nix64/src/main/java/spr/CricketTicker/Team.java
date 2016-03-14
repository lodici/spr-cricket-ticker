package spr.CricketTicker;

import java.util.ArrayList;
import java.util.List;

public class Team {
	
	public int id;
	public String abbreviation;
	public boolean isBatting = false;
	
	private List<Innings> innings = new ArrayList<Innings>();

	public int getInningsCount() {
		return innings.size();
	}

	public List<Innings> getInnings() {
		return this.innings;
	}
	public Innings getInnings(int i) {
		return this.innings.get(i);
	}

	public void setInnings(List<Innings> innings) {
		this.innings = innings;
		
	}

	public int getTotalRunsScored() {
		int totalRuns = 0;
		for (Innings innings : this.innings) {
			totalRuns += innings.runsScored;
		}
		return totalRuns;
	}
		
}