package spr.CricketTicker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;

/**
 * @author SPR
 *
 */
public class ScorecardXmlParser {
		
	private Element xml;
	
	protected ScorecardXmlParser(Element xmlScorecardElement) {
		this.xml = xmlScorecardElement;		
	}
		
//
// <teams>
//
	public Team getHomeTeam() {
		return extractTeam(getHomeTeamElement());
	}	
	public Team getAwayTeam() {
		return extractTeam(getAwayTeamElement());
	}	
	private Team extractTeam(Element teamElement) {
		Team team = new Team();
		team.id = extractTeamId(teamElement);
		team.abbreviation = extractTeamAbbreviation(teamElement);
		team.setInnings(extractTeamInnings(team.id));
		team.isBatting = isTeamBattingNow(team.id);
		return team;		
	}
	
	private List<Innings> extractTeamInnings(int teamId) {
		List<Innings> teamInnings = new ArrayList<Innings>();
		List<Element> teamInningsElements = getTeamInningsElements(teamId);
		// Reverse order since <past_ings> elements are ordered with most recent innings first.
		Collections.reverse(teamInningsElements);
		for (Element inningsElement : teamInningsElements) {
			teamInnings.add(extractInningsDetails(inningsElement));
		}
		return teamInnings;
	}
	
	private Innings extractInningsDetails(Element inningsElement) {
		Innings innings = new Innings();
		innings.runsScored = extractRunsScoredInInnings(inningsElement);
		innings.wicketsLost = extractWicketsTakenInInnings(inningsElement);
		innings.oversBowled = extractOversBowledInInnings(inningsElement);
		innings.runRate = extractRunRateInInnings(inningsElement);
		innings.requiredRunRate = extractRunRateRequiredInInnings(inningsElement);
		return innings;
	}
		
	private BigDecimal extractRunRateRequiredInInnings(Element inningsElement) {
		Element rr = inningsElement.getChild("a").getChild("rr");
		if (rr != null) {
			return roundUpToOneDecimalPlace(rr.getText());	
		} else {
			return roundUpToOneDecimalPlace("0.0");
		}		
	}

	private BigDecimal extractRunRateInInnings(Element inningsElement) {
		return roundUpToOneDecimalPlace(inningsElement.getChild("a").getChildText("cr"));
	}

	private BigDecimal extractOversBowledInInnings(Element inningsElement) {		
		return roundUpToOneDecimalPlace(inningsElement.getChild("a").getChildText("o"));
	}
	
	// Why BigDecimal? Because of rounding issues when using float or double.
	// (See Effective Java : "Avoid  float and double if exact answers are required").
	private BigDecimal roundUpToOneDecimalPlace(String value) {
		return new BigDecimal(value).setScale(1, RoundingMode.UP);
	}
	
	private int extractRunsScoredInInnings(Element inningsElement) {
		return Integer.parseInt(inningsElement.getChild("a").getChildText("r"));		
	}
	
	private int extractWicketsTakenInInnings(Element inningsElement) {
		return Integer.parseInt(inningsElement.getChild("a").getChildText("w"));		
	}
			
	private List<Element> getTeamInningsElements(int teamId) {
		List<Element> elements = new ArrayList<Element>();		
		for (Element inningsElement : getAllInningsElements()) {
			if (extractBattingTeamId(inningsElement) == teamId) {
				elements.add(inningsElement.getChild("s"));
			}						
		}
		return elements;				
	}	
	
	private Element getHomeTeamElement() {
		return this.xml.getChildren("teams").get(0);
	}	
	private Element getAwayTeamElement() {
		return this.xml.getChildren("teams").get(1);
	}

	private int extractTeamId(Element teamElement) {
		return Integer.parseInt(teamElement.getChildText("i"));
	}		
	private String extractTeamAbbreviation(Element teamElement) {
		return teamElement.getChildText("sn");
	}
		
	public String getHomeTeamScore() {
		return buildTeamScoreCaption(extractTeamId(getHomeTeamElement()));
	}
		
	private String buildTeamScoreCaption(int teamId) {
		
		boolean isTeamBatting = isTeamBattingNow(teamId);
		List<Element> teamInnings = getTeamInningsNodes(teamId);
		
		String scoreCaption = "";
		int totalRuns = 0;
		
		for (int i = 0; i < teamInnings.size(); i++) {
			Element inningsNode = teamInnings.get(i);
			boolean isCurrentInnings = (i==0 && isTeamBatting);
			if (isCurrentInnings) {
				scoreCaption = buildCurrentScoreCaption(inningsNode);
			} else {
				totalRuns += extractRunsScoredInInnings(teamInnings.get(i));
			}			
		}
		
		if (!scoreCaption.isEmpty()) {
			if (totalRuns > 0) {
				scoreCaption = String.valueOf(totalRuns) + "+" + scoreCaption;	
			}
		} else {
			scoreCaption = String.valueOf(totalRuns);
		}
		
		return scoreCaption;
		
	}
	
	private boolean isTeamBattingNow(int teamId) {
		int battingTeamId = extractBattingTeamId(getAllInningsElements().get(0));
		return (teamId == battingTeamId);		
	}
	
	private List<Element> getAllInningsElements() {
		return this.xml.getChildren("past_ings");		
	}
	
	private int extractBattingTeamId(Element inningsNode) {
		return Integer.parseInt(inningsNode.getChild("s").getChild("a").getChildText("i"));
	}
	
	private List<Element> getTeamInningsNodes(int teamId) {
		List<Element> teamInnings = new ArrayList<Element>();		
		for (Element inningsNode : getAllInningsElements()) {
			if (extractBattingTeamId(inningsNode) == teamId) {
				teamInnings.add(inningsNode.getChild("s").getChild("a"));
			}						
		}
		return teamInnings;				
	}
	
	private String buildCurrentScoreCaption(Element inningsNode) {
		String runsScored = String.valueOf(extractRunsScoredInInnings(inningsNode));
		String wicketsLost = String.valueOf(extractWicketsTakenInInnings(inningsNode));
		return runsScored + "/" + wicketsLost;		
	}
		
	public String getAwayTeamScore() {
		return buildTeamScoreCaption(extractAwayTeamId());
	}
	
	private int extractAwayTeamId() {
		return Integer.parseInt(getAwayTeamElement().getChildText("i"));
	}

	public String getDayNumber() {
		String matchDayText = getCurrentInningsNode().getChild("s").getChildText("dm");
		return matchDayText.replace("Day", "").trim();	
	}
	
	private Element getCurrentInningsNode() {
		return this.xml.getChild("past_ings");
	}

	public String getMatchStatusCaption() {
		String status;
		int resultCode = getResultCode();
		switch (resultCode) {
		case 0: 
			status = extractMatchStatus();			
			break;
		case 1:
			status = getWinningTeamAbbreviation() + " win";
			break;
		case 2:
			status = "Draw";
			break;
		case 3:
			status = "Tie";
			break;
		case 4:
			status = "Abandoned";
			break;
		case 5:
			status = "Cancelled";
			break;
		case 6 :
			status = "Postponed";
			break;
		default:
			status = "Unknown Result Code: " + resultCode;
			break;
		}
		return status;
	}

	private String getWinningTeamAbbreviation() {
		int winningTeamId = extractWinningTeamId();
		return extractTeamAbbreviation(getTeamElement(winningTeamId));
	}
	
	private Element getTeamElement(int requiredTeamId) {
		Element team = null;
		for (Element teamElement : this.xml.getChildren("teams")) {
			int thisTeamid = Integer.parseInt(teamElement.getChildText("i"));
			if (thisTeamid == requiredTeamId) {
				team = teamElement;
			}
		}
		return team;
	}
	
	private int extractWinningTeamId() {
		return Integer.parseInt(this.xml.getChild("result").getChildText("winner"));
	}

	private int getResultCode() {
		Element r = this.xml.getChild("result");
		if (r.getChildren().size() == 0) {
			return 0;
		} else {
			return Integer.parseInt(r.getChildText("r"));
		}
	}
	
	private String extractMatchStatus() {
		return this.xml.getChildText("ms");
	}

	public int getSessionNumber() {
		return extractSessionNumber();
	}
	
	private Element getCurrentInningsElement() {
		return this.xml.getChild("past_ings").getChild("s");
	}	
	
	private int extractSessionNumber() {
		return Integer.parseInt(getCurrentInningsElement().getChildText("sn"));
	}

	public int getMatchType() {
		return extractMatchType();
	}
	
	private int extractMatchType() {
		return Integer.parseInt(this.xml.getChildText("m"));
	}

	public Innings getCurrentInnings() {
		return extractInningsDetails(getCurrentInningsElement());
	}

	public boolean isMatchEnded() {
		return getResultCode() > 0;
	}

}
