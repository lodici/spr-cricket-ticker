package spr.CricketTicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class GameSelectorItem implements Comparable<GameSelectorItem> {

	public String Title;
	public DateTime StartDateTime;
	public String Status = "";
	public int MatchId;
	public boolean IsLive = false;
    private String venue;
    private String series;
	
	@Override
	public int compareTo(GameSelectorItem o) {
		if (this.StartDateTime.equals(o.StartDateTime)) {
			return 0;
		} else if (this.StartDateTime.isAfter(o.StartDateTime)) {
			return 1;
		} else {
			return -1;
		}
	}

    @Override
    public String toString() {
        return String.format("%s\n\n%s\nat %s\non %s, %s.",
            series, Title, venue,
            StartDateTime.dayOfWeek().getAsText(),
            StartDateTime.toString(DateTimeFormat.shortDateTime())
        );
    }

    void setVenue(String text) {
        this.venue = text;
    }

    void setSeries(String text) {
        this.series = text.split(",")[0];
    }
}
