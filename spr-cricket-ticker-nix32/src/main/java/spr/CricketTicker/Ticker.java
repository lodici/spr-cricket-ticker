package spr.CricketTicker;

import org.eclipse.swt.widgets.Label;

public class Ticker {
	
	private int matchId = 0;
	public void setMatchId(int id) {
		this.matchId = id;
	}
	public int getmatchId() {
		return matchId;
	}
	
	private Label separator = null;
	public void setSeparator(Label lbl) {
		this.separator = lbl;
	}
	public Label getSeparator() {
		return this.separator;
	}
	
	private Label swtLabel = null;
	public void setLabel(Label lbl) {
		this.swtLabel = lbl;
	}
	public Label getLabel() {
		return this.swtLabel;
	}
	
	private String caption = "";
	public boolean isMatchEnded = false;
	
	public void setText(String text) {
		this.caption = text;
	}
	public String getText() {
		return this.caption;
	}
	
	public void setCaption(String caption) {
		this.setText(caption);		
	}
	
//	@Override
//	public boolean equals(Object obj) {
//        if (obj == this) {
//            return true;
//        }
//        if (obj == null || obj.getClass() != this.getClass()) {
//            return false;
//        }
//        Ticker t = (Ticker) obj;
//        return t.swtLabel.equals(this.swtLabel);
//	}
}
