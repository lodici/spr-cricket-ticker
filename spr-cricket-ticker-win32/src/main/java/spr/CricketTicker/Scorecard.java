package spr.CricketTicker;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Scorecard {
		
	public String matchScoreLong;
	public String matchScoreShort;	
	public String matchStatus;
	public boolean isMatchEnded = false;

	public Scorecard(ScorecardXmlParser parser) {
		// Extract scorecard data then discard XML.
		buildTickerScore(parser);
		this.isMatchEnded = parser.isMatchEnded();
	}
	
	private void buildTickerScore(ScorecardXmlParser parser) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(parser.getHomeTeam().abbreviation).append(":");
		sb.append(buildTeamScore(parser.getHomeTeam(), parser.isMatchEnded())).append(" ");
		sb.append(parser.getAwayTeam().abbreviation).append(":");
		sb.append(buildTeamScore(parser.getAwayTeam(), parser.isMatchEnded()));
		this.matchScoreShort = sb.toString().trim();
		
		sb.append(buildSessionCaption(parser));
		sb.append(buildOversCaption(parser));
		this.matchScoreLong = sb.toString().trim();
		
		this.matchStatus = buildMatchStatusCaption(parser).trim();

	}
	
	private String buildOversCaption(ScorecardXmlParser parser) {
		StringBuilder sb = new StringBuilder();
		if (parser.getMatchType() != 1 && isPlayInProgress(parser)) {
			BigDecimal overs = parser.getCurrentInnings().oversBowled;
			sb.append(" ").append("Ov:").append(overs);
			BigDecimal runRate = parser.getCurrentInnings().runRate;
			sb.append(" ").append("Rt:").append(runRate);
			BigDecimal requiredRunRate = parser.getCurrentInnings().requiredRunRate;
			requiredRunRate.setScale(1, RoundingMode.UP);
			if (requiredRunRate.doubleValue() > 0 ) {
				sb.append("/").append(requiredRunRate.toString());
			}
		}		
		return sb.toString();
	}
	
	private String buildMatchStatusCaption(ScorecardXmlParser parser) {
		if (isPlayInProgress(parser)) {
			return "";
		} else {
			return " " + parser.getMatchStatusCaption();
		}
	}
	
	private boolean isPlayInProgress(ScorecardXmlParser parser) {
		String status = parser.getMatchStatusCaption().trim();
		return status.toLowerCase().equals("play in progress");		
	}
		
	private String buildSessionCaption(ScorecardXmlParser parser) {
		StringBuilder sb = new StringBuilder();
		if (parser.getMatchType() == 1 && !parser.isMatchEnded()) {
			sb.append(" D:");
			sb.append(parser.getDayNumber());
			if (isPlayInProgress(parser)) {
				sb.append(".").append(parser.getSessionNumber());			
			}			
		}
		return sb.toString();
	}

	private String buildTeamScore(Team team, boolean isMatchEnded) {		
		if (isMatchEnded) {
			return Integer.toString(team.getTotalRunsScored());
		} else {
			StringBuilder sb = new StringBuilder();			
			for (Innings innings : team.getInnings()) {
				String inningsScore = "";
				if (innings.wicketsLost < 10 && team.isBatting) {
					inningsScore = innings.runsScored + "/" + innings.wicketsLost;
				} else {
					inningsScore = Integer.toString(innings.runsScored);
				}
				sb.append(inningsScore).append("+");
			}		
			if (sb.length() > 0) {
				// Remove trailing "+"
				return sb.toString().substring(0, sb.toString().length() - 1);
			} else {
				return "0";
			}			
		}
		
	}
	
}
