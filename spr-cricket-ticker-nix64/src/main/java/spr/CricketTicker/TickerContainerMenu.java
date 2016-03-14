package spr.CricketTicker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import spr.CricketTicker.UserSettings.TickerColorScheme;

public class TickerContainerMenu {

	private ITickerContainerMenuListener listener;

	public TickerContainerMenu(ITickerContainerMenuListener listener) {
		this.listener = listener;
	}

	public Menu createPopupMenu() {

		Menu menu = new Menu(this.listener.getShell(), SWT.POP_UP);

//		createMenuItem_Refresh(menu);
		createMenuItem_TopMost(menu);
		createMenuItem_Separator(menu);

		createMenu_Color(menu);
		createMenu_Opacity(menu);
		createMenuItem_Font(menu);
		createMenuItem_Orientation(menu);
		createMenuItem_Separator(menu);

//		createMenuItem_ReverseScore(menu);
//		createMenuItem_Separator(menu);

		createMenuItem_RemoveTicker(menu);
		createMenuItem_SelectTicker(menu);
		createMenuItem_Separator(menu);

		createMenuItem_CloseContainer(menu);

		return menu;

	}

	private void createMenuItem_Separator(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
	}

	private void createMenuItem_TopMost(Menu menu) {
		final MenuItem item = new MenuItem(menu, SWT.CHECK);
		item.setSelection(UserSettings.getIsTickerTopMost());
		item.setText("Keep on Top");
		item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		boolean isTopMost = item.getSelection();
	    		TickerContainerMenu.this.listener.setTopMost(isTopMost);
	        }
	    });
	}

	private void createMenuItem_SelectTicker(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Select new Ticker...");
		item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.selectNewTicker();
	    	}
		});
	}

	private void createMenuItem_RemoveTicker(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Remove this Ticker");
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.removeTicker();
	        }
	    });
	}

	private void createMenuItem_ReverseScore(Menu menu) {
		new MenuItem(menu, SWT.CHECK).setText("Reverse Score");
		// TODO
	}

	private void createMenuItem_Orientation(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Toggle Orientation");
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setOrientation();
	        }
	    });
	}

	private void createMenuItem_Font(Menu menu) {
		MenuItem m = new MenuItem(menu, SWT.PUSH);
		m.setText("Font...");
	    m.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setFont();
	    	}
	    });
	}

	private void createMenu_Opacity(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Opacity");
		Menu subMenu = new Menu(menu);
		item.setMenu(subMenu);
		for (int i = 100; i >= 40; i -= 10) {
			createOpacityMenuItem(subMenu, i);
		}
	}
	private void createOpacityMenuItem(Menu menu, final int opacityPercent) {
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setText(opacityPercent + "%");
		item.setSelection(UserSettings.getTickerOpacity() == opacityPercent);
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setOpacity(opacityPercent);
	        }
	    });
	}

	private void createMenu_Color(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Color");
		Menu subMenu = new Menu(menu);
		item.setMenu(subMenu);
		createMenuItem_ColorPresetDefault(subMenu);
		createMenuItem_ColorPresetDesktop(subMenu);
		createMenuItem_ColorPresetTitlebar(subMenu);
		createMenuItem_Separator(subMenu);
		createMenuItem_ColorBackground(subMenu);
		createMenuItem_ColorForeground(subMenu);
	}

	private void createMenuItem_ColorForeground(Menu menu) {
	    MenuItem item = new MenuItem(menu, SWT.PUSH);
	    item.setText("Custom Foreground Color...");
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setCustomForegroundColor();
	    	}
	    });
	}

	private void createMenuItem_ColorPresetDefault(Menu subMenu) {
		MenuItem item = new MenuItem(subMenu, SWT.RADIO);
		item.setText("Default");
		item.setSelection(UserSettings.getTickerColorScheme() == TickerColorScheme.DEFAULT);
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setColorToDefault();
	        }
	    });
	}
	private void createMenuItem_ColorPresetDesktop(Menu menu) {
	    MenuItem item = new MenuItem(menu, SWT.RADIO);
	    item.setText("Desktop");
	    item.setSelection(UserSettings.getTickerColorScheme() == TickerColorScheme.DESKTOP);
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setColorToDesktop();
	    	}
	    });
	}
	private void createMenuItem_ColorPresetTitlebar(Menu menu) {
	    MenuItem item = new MenuItem(menu, SWT.RADIO);
	    item.setText("Window Titlebar");
	    item.setSelection(UserSettings.getTickerColorScheme() == TickerColorScheme.TITLEBAR);
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setColorToTitlebar();
	        }
	    });
	}

	private void createMenuItem_ColorBackground(Menu menu) {
	    MenuItem item = new MenuItem(menu, SWT.PUSH);
	    item.setText("Custom Background Color...");
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.setCustomBackgroundColor();
	    	}
	    });
	}

	private void createMenuItem_CloseContainer(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Exit");
	    item.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		TickerContainerMenu.this.listener.closeTickersContainer();
	        }
	    });
	}

}
