package spr.CricketTicker;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class UserSettings {
	
//	// see http://www.vineetmanohar.com/2010/01/3-ways-to-serialize-java-enums/.
//	public enum TickerColor {
//		DEFAULT(101), DESKTOP(102), TITLEBAR(103), CUSTOM(104);		 
//		private int code;
//		private TickerColor(int c) {
//			code = c;
//		}
//		public int getCode() {
//			return code;
//		}
//		public static TickerColor getTickerColor(int colorCode) {
//			for (TickerColor c : values()) {
//				if (c.getCode() == colorCode) {
//					return c;
//				}
//			}
//			return DEFAULT;
//		}			 
//	}

	// @see http://www.vineetmanohar.com/2010/01/3-ways-to-serialize-java-enums/.
	public enum TickerColorScheme {
		DEFAULT, DESKTOP, TITLEBAR, CUSTOM;		 
	}
		
	private static Preferences _settings = Preferences.userRoot().node("spr.cricketticker");

	public static boolean getIsTickerTopMost() {
		return _settings.getBoolean("TopMost", true);
	}
	public static void setIsTickerTopMost(boolean value) {
		_settings.putBoolean("TopMost", value);
	}
	
	public static void setTickerColorScheme(TickerColorScheme value) {
		_settings.put("PresetColor", value.name());
	}	
	public static TickerColorScheme getTickerColorScheme() {
		return TickerColorScheme.valueOf(_settings.get("PresetColor", TickerColorScheme.DEFAULT.name()));
	}
	
	public static void setTickerBackgroundColor(RGB newRgb) {
    	String hexColor = String.format("%02x%02x%02x", newRgb.red, newRgb.green, newRgb.blue);
		_settings.put("BackgroundColor", hexColor);
	}
	public static RGB getTickerBackgroundColor() {
    	String hexColor = _settings.get("BackgroundColor", "FFFFFF");
        return getRGBFromHex(hexColor);
	}
	
	public static void setTickerForegroundColor(RGB newRgb) {
    	String hexColor = String.format("%02x%02x%02x", newRgb.red, newRgb.green, newRgb.blue);
		_settings.put("ForegroundColor", hexColor);
	}
	public static RGB getTickerForegroundColor() {
    	String hexColor = _settings.get("ForegroundColor", "000000");
        return getRGBFromHex(hexColor);
	}

	public static void setTickerOpacity(int percent) {
		_settings.putInt("TickerOpacity", percent);
	}
	public static int getTickerOpacity() {
		return _settings.getInt("TickerOpacity", 100);
	}
	
	public static void setTickerFontData(FontData fontData) {
		_settings.put("TickerFontData", fontData.toString());
	}
	public static FontData getTickerFontData() {
		String fontDataString = _settings.get("TickerFontData", "");
		if (fontDataString != "") {
			return new FontData(fontDataString);
		} else {
			return null;	
		}
	}

	private static RGB getRGBFromHex(String hexColor) {
		int color = (int)Long.parseLong(hexColor.replace("#", ""), 16);
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;
		return new RGB(r,g,b);
	}
	
}
