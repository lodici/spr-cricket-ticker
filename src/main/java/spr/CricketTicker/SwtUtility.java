package spr.CricketTicker;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtUtility {
	
	public static void setShellToCenterOfDisplay(Shell shell, Display display) {		
	    Rectangle displaySize = display.getPrimaryMonitor().getBounds();
	    Rectangle shellSize = shell.getBounds();
	    int x = displaySize.x + (displaySize.width - shellSize.width) / 2;
	    int y = displaySize.y + (displaySize.height - shellSize.height) / 2;
	    shell.setLocation(x, y);				
	}

}
