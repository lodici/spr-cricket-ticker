package spr.CricketTicker;

import org.eclipse.swt.widgets.Shell;

public interface ITickerContainerMenuListener {
	Shell getShell();
	void closeTickersContainer();
	void setColorToDefault();	
	void setColorToDesktop();
	void setColorToTitlebar();
	void setCustomBackgroundColor();
	void setCustomForegroundColor();
	void setOpacity(int opacityPercent);
	void setFont();
	void setOrientation();
	void removeTicker();
	void selectNewTicker();
	void setTopMost(boolean isTopMost);
}
