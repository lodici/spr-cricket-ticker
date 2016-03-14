package spr.CricketTicker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

public class UpcomingMatchesXmlParser {
	
	private Document xmlDocument;
	private DateTime currentDateTime = DateTime.now();

	public UpcomingMatchesXmlParser(InputStream xmlStream) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		xmlDocument = builder.build(xmlStream);
	}	
	public UpcomingMatchesXmlParser(String xmlFile) throws JDOMException, IOException {
		xmlDocument = loadXmlDocument(xmlFile);
	}
	public UpcomingMatchesXmlParser(String xmlFile, DateTime currentDateTime) throws JDOMException, IOException {
		xmlDocument = loadXmlDocument(xmlFile);
		this.currentDateTime = currentDateTime;
	}

	public List<GameSelectorItem> getGameSelectorItems() {
		List<GameSelectorItem> items = new ArrayList<GameSelectorItem>();
		for (Element matchNode : getAllMatchNodes()) {
			items.add(extractUpcomingMatchDetailsFromXml(matchNode));
		}
		Collections.sort(items);
		return items;
	}

    private String extractVenue(Element matchNode) {
        return matchNode.getChildText("Venue");
    }

    private String extractSeries(Element matchNode) {
        return matchNode.getAttributeValue("series_name");
    }

	private GameSelectorItem extractUpcomingMatchDetailsFromXml(Element matchNode) {
		GameSelectorItem upcomingMatch = new GameSelectorItem();
		upcomingMatch.Title = extractMatchTitle(matchNode);
		upcomingMatch.StartDateTime = extractStartDateTime(matchNode);
		upcomingMatch.Status = extractMatchStatus(matchNode);
		upcomingMatch.MatchId = extractMatchId(matchNode);
        upcomingMatch.setVenue(extractVenue(matchNode));
        upcomingMatch.setSeries(extractSeries(matchNode));
		return upcomingMatch;
	}
	
	private int extractMatchId(Element matchNode) {
		return Integer.parseInt(matchNode.getAttributeValue("matchid"));
	}
	
	private String extractMatchStatus(Element matchNode) {
		DateTime startDateTime = extractStartDateTime(matchNode);
		if (startDateTime.toLocalDate().equals(currentDateTime.toLocalDate())) {
			return startDateTime.toString(DateTimeFormat.shortTime());
		} else {
			return startDateTime.toString(DateTimeFormat.shortDateTime());
		}
	}
	
	private DateTime extractStartDateTime(Element matchNode) {
		// Format of StartDate element = 2013-04-24T20:00:00+05:30
		String startDate = matchNode.getChildText("StartDate");
		DateTime dt = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(startDate);
		return dt;
	}

	private String extractMatchTitle(Element matchNode) {
		StringBuilder sb = new StringBuilder();
		sb.append(extractHomeTeamName(matchNode));
		sb.append(" v ");
		sb.append(extractAwayTeamName(matchNode));
		sb.append(", ").append(extractMatchNo(matchNode));
		return sb.toString();
	}
	
	private String extractMatchNo(Element matchNode) {
		return matchNode.getChildText("MatchNo");
	}

	private String extractAwayTeamName(Element matchNode) {
		return matchNode.getChildren("Team").get(1).getAttributeValue("Team");
	}

	private String extractHomeTeamName(Element matchNode) {
		return matchNode.getChildren("Team").get(0).getAttributeValue("Team");
	}
	
	private List<Element> getAllMatchNodes() {
		return xmlDocument.getRootElement().getChild("results").getChildren("Match");
	}
	
	private Document loadXmlDocument(String xmlFile) throws JDOMException, IOException {
		File f = new File(xmlFile);
		SAXBuilder builder = new SAXBuilder();
		return builder.build(f);
	}	

}
