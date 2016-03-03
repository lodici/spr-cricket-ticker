package spr.CricketTicker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DragMoveShellListener implements Listener {

	private Display display;
	private Shell shell;
	private Point origin;
	
	public DragMoveShellListener(Display d, Shell s) {
		this.display = d;
		this.shell = s;
	}
	
	public void attach(Control control) {
		control.addListener(SWT.MouseDown, this);
		control.addListener(SWT.MouseUp, this);
		control.addListener(SWT.MouseMove, this);		
	}
	
	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.MouseDown:
			if (event.button == 1) {
				this.origin = new Point(event.x, event.y);
			}
			break;
		case SWT.MouseUp:
			if (event.button == 1) {
				this.origin = null;
			}
			break;
		case SWT.MouseMove:
			if (this.origin != null) {
				Point p = this.display.map(this.shell, null, event.x, event.y);
				this.shell.setLocation(p.x - this.origin.x, p.y - this.origin.y);
			}
			break;
		}				
	}				

}
