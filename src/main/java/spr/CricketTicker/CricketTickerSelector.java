package spr.CricketTicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jdom2.JDOMException;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class CricketTickerSelector implements ICricketTickerListener {

	private static final Boolean useSampleLiveFeed = false;

	private static Display display;
	private static Shell shell;

    private final Composite container;
    private final StyledText upcomingLabel;
	private Table _table;
	private List<GameSelectorItem> _games;
	private TickerContainer _tickersContainer;
	private LiveSummaryXmlParser _liveMatchParser = null;
	private UpcomingMatchesXmlParser _upcomingMatchParser = null;

	public static void main(String[] args) {
		Display.setAppName("SPR Cricket Ticker 2.1");
		display = new Display();
		new CricketTickerSelector();
		mainEventLoop();
	}

	private static void mainEventLoop() {
        System.out.println(Boolean.getBoolean("useSampleFeed"));
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
		    }
		}
		display.dispose();
	}

	private CricketTickerSelector() {

        createShell();

        container = new Composite(shell, SWT.NONE);
        final StackLayout stack = new StackLayout();
        container.setLayout(stack);

        upcomingLabel = new StyledText(container, SWT.NONE);
        upcomingLabel.setWordWrap(true);
        upcomingLabel.setEditable(false);
        upcomingLabel.setCaret(null);
        upcomingLabel.setMargins(4, 4, 4, 4);
        upcomingLabel.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
        upcomingLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                StackLayout layout = (StackLayout) container.getLayout();
                layout.topControl = _table;
                container.layout();
            }
        });

		createTable(stack);
		createTimer();
		displayShell();
	}


	//
	// SWT Shell
	//
	private void createShell() {
		shell = new Shell(display, SWT.DIALOG_TRIM);
        final FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		shell.setBounds(0, 0, 445, 160);
		setShellIcon();
		setShellCaption();
		createShellListener();
	}

	private void setShellIcon() {
		InputStream stream = getClass().getResourceAsStream("/sprcricketticker.png");
		shell.setImage(new Image(display, stream));
	}

	private void setShellCaption() {
		if (useSampleLiveFeed) {
			shell.setText("SPR Cricket Ticker 2.1 [!!SAMPLE LIVE FEED!!]");
		} else {
			shell.setText("SPR Cricket Ticker 2.1");
		}
	}

	private void createShellListener() {
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (_tickersContainer != null) {
					if (_tickersContainer.getTickersCount() > 0) {
						e.doit = false;
						shell.setVisible(false);
					}
				}
			}
			@Override
			public void shellIconified(ShellEvent e) {}
			@Override
			public void shellDeiconified(ShellEvent e) {}
			@Override
			public void shellDeactivated(ShellEvent e) {}
			@Override
			public void shellActivated(ShellEvent e) {}
		});
	}

	private void displayShell() {
		SwtUtility.setShellToCenterOfDisplay(shell, display);
		shell.open();
	}

	//
	// SWT Table
	//
	private void createTable(StackLayout layout) {		
        _table = CricketTickerSelectorTable.createTable(display, shell, container);
        layout.topControl = _table;
		createTableMouseListener();
	}

    private String getUpcomingMessage(GameSelectorItem gameItem) {
        Period pt = new Period(new LocalDateTime(), gameItem.StartDateTime.toLocalDateTime());
        PeriodFormatter pf = new PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix(" day", " days")
            .appendSeparator(", ")
            .appendHours()
            .appendSuffix(" hour", " hours")
            .appendSeparator(" and ")
            .appendMinutes()
            .appendSuffix(" minute", " minutes")
            .toFormatter();
        return String.format("Starting in %s...\n\n%s", pt.toString(pf), gameItem.toString());
    }

    private void showUpcomingLabel(GameSelectorItem gameItem) {
        upcomingLabel.setText(getUpcomingMessage(gameItem));
        StackLayout layout = (StackLayout) container.getLayout();
        layout.topControl = upcomingLabel;
        container.layout();
    }

	private void createTableMouseListener() {

		_table.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				TableItem tableItem = getTableItemUnderMousePointer(_table, e.x, e.y);
				GameSelectorItem gameItem = getGameSelectorItem(_table, tableItem);

				if (gameItem.IsLive) {
					try {
						LiveSummaryXmlParser parser = CricketTickerSelector.this._liveMatchParser;
						getTickersContainer().addTicker(gameItem.MatchId, parser);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
                    showUpcomingLabel(gameItem);
				}
			}

			private TickerContainer getTickersContainer() {
				if (CricketTickerSelector.this._tickersContainer == null) {
					CricketTickerSelector.this._tickersContainer = new TickerContainer(CricketTickerSelector.this, display, shell);
				}
				return CricketTickerSelector.this._tickersContainer;
			}

			@Override
			public void mouseUp(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) {}

		});

	}


	//
	// YQL Feed Request Timer
	//
	private void createTimer() {

		// Create `daemon` thread so that program is not prevented from exiting.
		Boolean isDaemonThread = true;
		Timer timer = new Timer(isDaemonThread);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateXmlFeeds();
		        display.syncExec(new Runnable() {
		            public void run() {
		            	refreshTableData();
		            	try {
							updateOpenTickers();
						} catch (Exception e) {
							e.printStackTrace();
						}
		            }
		          });
			}
		};
		final int INITIAL_DELAY = 1000; 	// 1 second
		final int TIMER_INTERVAL = 30000; 	// 30 seconds
		timer.schedule(task, INITIAL_DELAY, TIMER_INTERVAL);
	}

	private void updateOpenTickers() throws Exception {
		if (_tickersContainer != null) {
			_tickersContainer.updateTickers(_liveMatchParser);
		}
	}


	//
	// YQL Feeds
	///
	private void updateXmlFeeds() {
		System.out.println("Updating feeds...");
		updateLiveXmlFeed();
		updateUpcomingXmlFeed();
		System.out.println("...Feeds updated.");
	}

	private void updateLiveXmlFeed() {
		try {
			if (useSampleLiveFeed) {
				_liveMatchParser = new LiveSummaryXmlParser(getYqlLiveSummarySampleFeed());
			} else {
				_liveMatchParser = new LiveSummaryXmlParser(getYqlLiveSummaryXmlFeed());
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private InputStream getYqlLiveSummaryXmlFeed() throws ClientProtocolException, IOException {
		final String LIVE_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20cricket.scorecard.live.summary&diagnostics=true&env=store%3A%2F%2F0TxIGQMQbObzvU4Apia0V0";
		return Request.Get(LIVE_URL).execute().returnContent().asStream();
	}
	private InputStream getYqlLiveSummarySampleFeed() {
		return  getClass().getResourceAsStream("/MultipleScorecards.xml");
	}

	private void updateUpcomingXmlFeed() {
		try {
			if (useSampleLiveFeed) {
				_upcomingMatchParser = new UpcomingMatchesXmlParser(getYqlUpcomingXmlSampleFeed());
			} else {
				_upcomingMatchParser = new UpcomingMatchesXmlParser(getYqlUpcomingXmlFeed());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream getYqlUpcomingXmlFeed() throws ClientProtocolException, IOException {
		final String YQL_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20cricket.upcoming_matches&diagnostics=true&env=store%3A%2F%2F0TxIGQMQbObzvU4Apia0V0";
		return Request.Get(YQL_URL).execute().returnContent().asStream();
	}
	private InputStream getYqlUpcomingXmlSampleFeed() {
		return  getClass().getResourceAsStream("/yql.xml");
	}

	//
	// Game Selector
	//
	private void refreshTableData() {
		updateListOfGames();
		_table.removeAll();
		for (GameSelectorItem gameItem : _games) {
			TableItem tableItem = new TableItem(_table, SWT.NONE);
			tableItem.setText(0, gameItem.Title);
			tableItem.setText(1, gameItem.Status);
		}
		_table.setEnabled(_table.getItemCount() > 0);
//		autoResizeTable();
//		shell.pack();
	}

	private void updateListOfGames() {
		_games = new ArrayList<GameSelectorItem>();
		if (_liveMatchParser != null) {
			_games = _liveMatchParser.getGameSelectorItems();
		}
		if (_upcomingMatchParser != null) {
			_games.addAll(_upcomingMatchParser.getGameSelectorItems());
		}
	}

	private GameSelectorItem getGameSelectorItem(Table table, TableItem item) {
		int itemIndex = table.indexOf((TableItem) item);
		return _games.get(itemIndex);
	}

	private TableItem getTableItemUnderMousePointer(Table table, int mouseX, int mouseY) {
		Point mousePoint = new Point(mouseX, mouseY);
		return table.getItem(mousePoint);
	}

	@Override
	public void DisplaySelector() {
		if (!shell.getVisible()) {
			shell.setVisible(true);
		}
		shell.setActive();
	}

	@Override
	public void tickerContainerIsEmpty() {
		if (!shell.getVisible()) {
			shell.close();
		}
	}

}
