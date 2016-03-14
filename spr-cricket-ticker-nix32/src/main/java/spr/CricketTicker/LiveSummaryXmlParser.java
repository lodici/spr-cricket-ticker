package spr.CricketTicker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class LiveSummaryXmlParser {
		
	private List<Element> scorecardElements;
	
	public LiveSummaryXmlParser(String xmlFile) throws JDOMException, IOException {
		extractAllScorecardElements(loadXmlDocument(xmlFile));
	}	
	public LiveSummaryXmlParser(InputStream xmlStream) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		extractAllScorecardElements(builder.build(xmlStream));
	}
	
	private Document loadXmlDocument(String xmlFile) throws JDOMException, IOException {
		File f = new File(xmlFile);
		SAXBuilder builder = new SAXBuilder();
		return builder.build(f);
	}
	
	private void extractAllScorecardElements(Document xml) {
		this.scorecardElements =  xml.getRootElement().getChild("results").getChildren("Scorecard");
	}
	
	
	//
	// Game Selector Items
	//
	public List<GameSelectorItem> getGameSelectorItems() {
		List<GameSelectorItem> items = new ArrayList<GameSelectorItem>();
		for (Element scorecardElement : this.scorecardElements) {
			GameSelectorItem item = new GameSelectorItem();
			item.Title = buildGameSelectorItemCaption(scorecardElement);
			item.Status = extractMatchStatus(scorecardElement);
			item.MatchId = extractMatchId(scorecardElement);
			item.IsLive = true;
			items.add(item);
		}
		return items;
	}
	
	private String buildGameSelectorItemCaption(Element scorecardElement) {
		StringBuilder sb = new StringBuilder();
		sb.append(extractHomeTeamName(scorecardElement));
		sb.append(" v ");
		sb.append(extractAwayTeamName(scorecardElement));
		sb.append(", ");
		sb.append(extractMatchTitle(scorecardElement));
		return sb.toString();
	}
	
	private String extractMatchTitle(Element scorecardElement) {
		return scorecardElement.getChildText("mn");		
	}
		
	private String extractHomeTeamName(Element scorecardElement) {
		return getHomeTeamElement(scorecardElement).getChildText("fn");
	}
	private String extractAwayTeamName(Element scorecardElement) {
		return getAwayTeamElement(scorecardElement).getChildText("fn");
	}
		
	private Element getHomeTeamElement(Element scorecardElement) {
		return scorecardElement.getChildren("teams").get(0);
	}
	private Element getAwayTeamElement(Element scorecardElement) {
		return scorecardElement.getChildren("teams").get(1);
	}
	
	private String extractMatchStatus(Element scorecardElement) {
		return scorecardElement.getChildText("ms");
	}
	
	private int extractMatchId(Element scorecardElement) {
		return Integer.parseInt(scorecardElement.getChildText("mid"));
	}
	
		
	//
	// Scorecard Parser
	//
	public ScorecardXmlParser getScorecardParser(int matchId) {
		Element scorecardXml = getScorecardElement(matchId);
		if (scorecardXml != null) {
			return new ScorecardXmlParser(scorecardXml);
		} else {
			// Expected <Scorecard> element has disappeared from feed.
			return null;
		}
	}
	
	private Element getScorecardElement(int matchId) {
		for (Element scorecardElement : this.scorecardElements) {
			if (extractScorecardId(scorecardElement) == matchId) {
				return scorecardElement;
			}
		}
		return null;
	}	
	
	private int extractScorecardId(Element scorecardElement) {
		return Integer.parseInt(scorecardElement.getChildText("mid"));
	}
																											
}
