package spr.CricketTicker;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TickerManager {
	
	private Map<Integer, Ticker> tickers =  new LinkedHashMap<Integer, Ticker>();
	private Map<Integer, Scorecard> scorecards = new HashMap<Integer, Scorecard>();

	public void addTicker(int tickerId) {
		if (!this.tickers.containsKey(tickerId)) {						
			Ticker t = new Ticker();
			t.setMatchId(tickerId);			
			tickers.put(tickerId, t);
		}		
	}

	public void updateTickers(LiveSummaryXmlParser feedParser) throws Exception {
		for (Ticker ticker : this.tickers.values()) {
			int matchId = ticker.getmatchId();
			setTickerCaption(ticker, feedParser.getScorecardParser(matchId));
		}	
	}
	
	public Map<Integer, Ticker> getTickers() {
		return this.tickers;
	}

	private void rememberLastScorecard(Scorecard scorecard, Ticker t) {
		int tickerId = t.getmatchId();
		this.scorecards.remove(tickerId);
		this.scorecards.put(tickerId, scorecard);
	}

	public int getTickerCount() {
		return tickers.size();
	}

	public Ticker getTicker(int tickerId) {
		return tickers.get(tickerId);
	}
	
	private void setTickerCaption(Ticker ticker, ScorecardXmlParser scorecardParser) {
		int tickerId = ticker.getmatchId();
		if (scorecardParser == null) {
			Scorecard scorecard = getPreviousScorecard(tickerId);
			if (scorecard != null) {
				if (!scorecard.isMatchEnded) {
					// Match not ended but has disappeared from live feed!
					ticker.setCaption(scorecard.matchScoreShort + " XML Feed Error");
				}			
			} else {
				ticker.setCaption("Xml Feed Error");
			}
		} else {
			Scorecard scorecard = new Scorecard(scorecardParser);
			String caption = (scorecard.matchScoreLong + " " + scorecard.matchStatus).trim();
			ticker.setCaption(caption);
			rememberLastScorecard(scorecard, ticker);			
		}
	}
	
	private Scorecard getPreviousScorecard(int tickerId) {
		return this.scorecards.get(tickerId);
	}
	
}
