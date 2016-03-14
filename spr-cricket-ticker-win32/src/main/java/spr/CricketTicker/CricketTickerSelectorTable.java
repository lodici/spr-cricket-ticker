package spr.CricketTicker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CricketTickerSelectorTable {

	public static Table createTable(Display display, Shell shell, Composite container) {
		
		Table table = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.NO_SCROLL);
		table.setHeaderVisible(true);			
		table.setFont(new Font(display, new FontData("Sans Serif", 8, SWT.NORMAL)));
		table.setEnabled(false);
        
		createTableColumns(table, shell);
		createTableItemPleaseWait(table);
		
		return table;
				
	}
	
	private static void createTableItemPleaseWait(Table table) {
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(0, "Requesting list of cricket matches from Yahoo...please wait");		
	}

	private static void createTableColumns(Table table, Shell shell) {
		TableColumn titleColumn = new TableColumn(table, SWT.NONE);
		titleColumn.setText("Game");
		titleColumn.setWidth(shell.getSize().x-100);
		titleColumn.setResizable(false);
		titleColumn.setMoveable(false);
		TableColumn statusColumn = new TableColumn(table, SWT.NONE);
		statusColumn.setText("Status");
		statusColumn.setWidth(shell.getSize().x - titleColumn.getWidth());
		statusColumn.setResizable(false);
		statusColumn.setMoveable(false);		
	}
	
}
