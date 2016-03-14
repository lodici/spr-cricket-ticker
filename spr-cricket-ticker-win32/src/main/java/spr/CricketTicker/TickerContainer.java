package spr.CricketTicker;

import java.awt.SystemColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import spr.CricketTicker.UserSettings.TickerColorScheme;

public class TickerContainer implements ITickerContainerMenuListener {

	private static Display display;

	private TickerManager tickerManager;

	private Shell shell;
	private Menu popupMenu;
	private DragMoveShellListener dragMoveListener;
	private Control ticker = null;
	private ICricketTickerListener listener;

	private boolean _isForegroundStandardColor = false;
	private boolean _isBackgroundStandardColor = false;

	public TickerContainer(ICricketTickerListener listener, Display d, Shell parentShell) {
		display = d;
		this.listener = listener;
		createShell(parentShell);
		this.dragMoveListener = new DragMoveShellListener(display, this.shell);
		createPopupMenu(this.shell);
		this.tickerManager= new TickerManager();
	}


	///////////////////////////////////////////////////////////////////////////
	// Container Shell
	///////////////////////////////////////////////////////////////////////////
	private void createShell(Shell parentShell) {
		if (UserSettings.getIsTickerTopMost()) {
			this.shell = new Shell(display,  SWT.NO_TRIM | SWT.ON_TOP);
		} else {
			this.shell = new Shell(display, SWT.NO_TRIM);
		}
		setShellSizeAndPosition(parentShell);
		setShellLayout(this.shell);
		this.shell.open();
		this.shell.setVisible(false);
	}

	private void setShellLayout(Shell shell) {
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.fill = false;
//		rowLayout.justify = true;
//		rowLayout.pack = false;
//		layout.type = SWT.HORIZONTAL;
		layout.marginTop = 0;
		layout.marginBottom = 1;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.spacing = 0;
		layout.marginWidth = 0;
		shell.setLayout(layout);
	}

	private void setShellSizeAndPosition(Shell parentShell) {
		Rectangle parentSize = parentShell.getBounds();
		this.shell.setBounds(parentSize.x, parentSize.y + parentSize.height , 445, 40);
	}

	private void createPopupMenu(Shell shell) {
		TickerContainerMenu menuManager = new TickerContainerMenu(this);
		this.popupMenu = menuManager.createPopupMenu();
	}

	private void setTickerOpacity(Shell shell) {
		int percent = UserSettings.getTickerOpacity();
		// int/int = int = 0 if percent < 100 (eg. percent = 40 = 40/100 = 0).
		// Thus we use 100.0 to ensure division returns a double type (eg. 40 = 40/100.0 = 0.4).
		int alpha = (int)((percent/100.0) * 255);
		shell.setAlpha(alpha);
	}

	private Color getTickerBackgroundColor() {
		_isBackgroundStandardColor = false;
		Color c;
		TickerColorScheme colorScheme = UserSettings.getTickerColorScheme();
		System.out.println("getTickerBackgroundColor().colorScheme =" + colorScheme);
		switch (colorScheme) {
		case DESKTOP:
			c = getDesktopBackgroundColor();
			_isBackgroundStandardColor = true;
			break;
		case TITLEBAR:
			c = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
			_isBackgroundStandardColor = true;
			break;
		case CUSTOM:
			c = new Color(Display.getCurrent(), UserSettings.getTickerBackgroundColor());
			break;
		default:
			c = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			_isBackgroundStandardColor = true;
			break;
		}
		return c;
	}
	private Color getTickerForegroundColor() {
		_isForegroundStandardColor = false;
		Color c;
		TickerColorScheme colorScheme = UserSettings.getTickerColorScheme();
		System.out.println("getTickerForegroundColor().colorScheme =" + colorScheme);
		switch (colorScheme) {
		case DESKTOP: // TODO
			c = getDesktopForegroundColor();
			_isForegroundStandardColor = true;
			break;
		case TITLEBAR:
			c = display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
			_isForegroundStandardColor = true;
			break;
		case CUSTOM: // TODO
			c = new Color(Display.getCurrent(), UserSettings.getTickerForegroundColor());
			break;
		default:
			System.out.println("display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND)=" + display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND).toString());
			c = display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
			_isForegroundStandardColor = true;
			break;
		}
		return c;
	}
//	private boolean getTickerForegroundColor(Color c) {
//		boolean isSystemColor = false;
//		switch (UserSettings.getTickerColorScheme()) {
//		case DESKTOP: // TODO
//			c = getDesktopForegroundColor();
//			isSystemColor = true;
//			break;
//		case TITLEBAR:
//			c = display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
//			isSystemColor = true;
//			break;
//		case CUSTOM: // TODO
//			c = new Color(Display.getCurrent(), UserSettings.getTickerForegroundColor());
//			break;
//		default:
//			c = display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
//			isSystemColor = true;
//		}
//		return isSystemColor;
//	}

	private Color getDesktopForegroundColor() {
		if (isDesktopBackgroundColorBright()) {
			return display.getSystemColor(SWT.COLOR_BLACK);
		} else {
			return display.getSystemColor(SWT.COLOR_WHITE);
		}
	}

	private boolean isDesktopBackgroundColorBright() {
		final Color c = getDesktopBackgroundColor();
		boolean isBright = isColorBright(c);
		c.dispose();
		return isBright;
	}

	private Color getDesktopBackgroundColor() {
		SystemColor sysColor = SystemColor.desktop;
		Device device = Display.getCurrent();
		Color c = new Color(device, sysColor.getRed(), sysColor.getGreen(), sysColor.getBlue());
		return c;
    }

	private boolean isColorBright(Color c) {
		return (brightnessIndex(c) >= 130);
	}

	private int brightnessIndex(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		return (int) Math.sqrt(r * r * 0.241 + g * g * 0.691 + b * b * 0.068);
	}

	private void setShellTopMost(boolean isTopMost) {

		Shell newShell;
		if (isTopMost) {
			newShell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
		} else {
			newShell = new Shell(display, SWT.NO_TRIM);
		}

		Rectangle shellSize = this.shell.getBounds();
		newShell.setBounds(shellSize);

		setShellLayout(newShell);
//		removeExistingLabelsFromShell(this.shell);

		createPopupMenu(newShell);

		this.shell.close();
		this.shell = newShell;
		this.dragMoveListener = new DragMoveShellListener(display, this.shell);

		newShell.open();
		newShell.setVisible(false);

		renderShell(newShell);
		newShell.setActive();

	}

	private RowLayout getShellLayout() {
		return (RowLayout) this.shell.getLayout();
	}

	private Ticker getTickerCaptionFromLabel(Label tickerLabel) {
		Ticker target = null;
		for (Ticker t : this.tickerManager.getTickers().values()) {
			if (t.getLabel().equals(tickerLabel)) {
				target = t;
				break;
			}
		}
		return target;
	}

	private Label createSeparatorLabel(int lblHeight, Shell shell) {

		Label lbl = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);

		RowData layoutData = new RowData();
		layoutData.height = lblHeight;
		lbl.setLayoutData(layoutData);
		this.dragMoveListener.attach(lbl);

		return lbl;
	}

	private Label createTickerLabel(Shell shell) {

		Label lbl = new Label(shell, SWT.NONE);

		if (UserSettings.getTickerFontData() != null) {
			lbl.setFont(new Font(display, UserSettings.getTickerFontData()));
		}
		lbl.setText("");
		lbl.setMenu(this.popupMenu);

		addTickerLabelListener(lbl);

		return lbl;
	}

	private void addTickerLabelListener(Label lbl) {
		lbl.addMouseTrackListener(new MouseTrackListener() {
			@Override
			public void mouseEnter(MouseEvent e) {
				ticker = (Control) e.widget;
			}
			@Override
			public void mouseHover(MouseEvent e) {}
			@Override
			public void mouseExit(MouseEvent e) {}
		});
		this.dragMoveListener.attach(lbl);
	}

	private void closeContainer(){
		this.tickerManager.getTickers().clear();
		this.shell.setVisible(false);
		this.listener.tickerContainerIsEmpty();
	}

	public void addTicker(int matchId, LiveSummaryXmlParser feedParser) throws Exception {
		this.tickerManager.addTicker(matchId);
		renderShell(this.shell);
		updateTickers(feedParser);
		this.shell.setActive();
	}

	public void updateTickers(LiveSummaryXmlParser feedParser) throws Exception {
		this.tickerManager.updateTickers(feedParser);
		for (Ticker t : this.tickerManager.getTickers().values()) {
			setTickerLabelCaption(t.getLabel(), t.getText());
		}
		this.shell.pack();
	}

	private void renderShell(Shell shell) {

		shell.setRedraw(false);

		removeExistingLabelsFromShell(shell);

		Color foregroundColor = getTickerForegroundColor();
		Color backgroundColor = getTickerBackgroundColor();

		shell.setBackground(backgroundColor);
		shell.setForeground(foregroundColor);

		int lblHeight = 0;
		int tickerCount = 0;
		for (Ticker t : this.tickerManager.getTickers().values()) {
			boolean printSeparator = (tickerCount > 0 && isHorizontalLayout());
			if (printSeparator) {
				t.setSeparator(createSeparatorLabel(lblHeight, shell));
			}
			t.setLabel(createTickerLabel(shell));
			Label tickerLabel = t.getLabel();
			setTickerLabelCaption(tickerLabel, t.getText());
			if (lblHeight == 0) {
				lblHeight = tickerLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			}
			tickerLabel.setBackground(backgroundColor);
			tickerLabel.setForeground(foregroundColor);
			tickerCount++;
		}

		if (!_isForegroundStandardColor) {
			foregroundColor.dispose();
		}
		if (!_isBackgroundStandardColor) {
			backgroundColor.dispose();
		}

		setTickerOpacity(shell);

		shell.layout();
		shell.pack();
		shell.setRedraw(true);

		if (shell.getVisible() == false) {
			shell.setVisible(true);
		}

	}

	private void removeExistingLabelsFromShell(Shell shell) {
		for (Control c : shell.getChildren()) {
			if (c instanceof Label){
				// Disposing of a control that has a pop up menu will also dispose of the menu.
				// But we are using a shared single menu reference for all tickers so we do not want this.
				// To avoid this behavior, set the menu to null *before* the control is disposed.
				c.setMenu(null);
				c.dispose();
			}
		}
	}

	private boolean isHorizontalLayout() {
		return (getShellLayout().type == SWT.HORIZONTAL);
	}

	private void setTickerLabelCaption(Label lbl, String caption) {
		lbl.setText(" " + caption + " ");
		lbl.pack();
	}

	public int getTickersCount() {
		return this.tickerManager.getTickers().values().size();
	}


	@Override
	public Shell getShell() {
		return this.shell;
	}

	@Override
	public void closeTickersContainer() {
		closeContainer();
	}

	@Override
	public void setColorToDefault() {
		UserSettings.setTickerColorScheme(TickerColorScheme.DEFAULT);
		renderShell(this.shell);
	}

	@Override
	public void setColorToDesktop() {
		UserSettings.setTickerColorScheme(TickerColorScheme.DESKTOP);
		renderShell(this.shell);
	}

	@Override
	public void setColorToTitlebar() {
		UserSettings.setTickerColorScheme(TickerColorScheme.TITLEBAR);
		renderShell(this.shell);
	}

	@Override
	public void setCustomBackgroundColor() {
		ColorDialog dialog = new ColorDialog(this.shell);
        dialog.setText("Ticker Background Color ");
        dialog.setRGB(getTickerBackgroundColor().getRGB());
        RGB newRgb = dialog.open();
        if (newRgb != null) {
        	UserSettings.setTickerColorScheme(TickerColorScheme.CUSTOM);
        	UserSettings.setTickerBackgroundColor(newRgb);
        	renderShell(this.shell);
        }
	}

	@Override
	public void setCustomForegroundColor() {
		ColorDialog dialog = new ColorDialog(this.shell);
        dialog.setText("Ticker Foreground Color ");
        dialog.setRGB(getTickerForegroundColor().getRGB());
        RGB newRgb = dialog.open();
        if (newRgb != null) {
        	UserSettings.setTickerColorScheme(TickerColorScheme.CUSTOM);
        	UserSettings.setTickerForegroundColor(newRgb);
        	renderShell(this.shell);
        }
	}

	@Override
	public void setOpacity(int opacityPercent) {
		UserSettings.setTickerOpacity(opacityPercent);
		renderShell(this.shell);
	}

	@Override
	public void setFont() {
		FontDialog dialog = new FontDialog(this.shell);
		dialog.setEffectsVisible(false); // Hide color, strike-out/thru options etc.
        dialog.setText("Ticker Font");
        FontData[] fontDataList = {UserSettings.getTickerFontData()};
        dialog.setFontList(fontDataList);
        FontData newFont = dialog.open();
        if (newFont != null) {
        	UserSettings.setTickerFontData(newFont);
        	renderShell(this.shell);
        }
	}

	@Override
	public void setOrientation() {
		if (getShellLayout().type == SWT.HORIZONTAL) {
			getShellLayout().type = SWT.VERTICAL;
		} else {
			getShellLayout().type = SWT.HORIZONTAL;
		}
		renderShell(this.shell);
	}


	@Override
	public void removeTicker() {
		if (ticker != null) {
			Label tickerLabel = ((Label) ticker);
			Ticker ticker = getTickerCaptionFromLabel(tickerLabel);
			this.tickerManager.getTickers().remove(ticker.getmatchId());
			renderShell(this.shell);
		}
		if (this.tickerManager.getTickers().size() == 0) {
			selectNewTicker();
		}
	}

	@Override
	public void selectNewTicker() {
		this.listener.DisplaySelector();
	}

	@Override
	public void setTopMost(boolean isTopMost) {
		UserSettings.setIsTickerTopMost(isTopMost);
		setShellTopMost(isTopMost);
	}

}
