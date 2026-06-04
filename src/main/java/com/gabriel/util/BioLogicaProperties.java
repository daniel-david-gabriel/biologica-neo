package com.gabriel.util;

/**
 * 
 * Would probably be better to use some properties loader, but we'll do this to
 * keep changes minimal.
 * 
 * Set to true in classes like com.gabriel.ui.AllInOneWindow to add Environment
 * Simulator button and related functionality.
 * 
 * @author dan
 *
 */

public class BioLogicaProperties {

	public static Boolean neoMode = false;
	
	public static void setNeoMode(Boolean newMode) {
		neoMode = newMode;
	}
}
