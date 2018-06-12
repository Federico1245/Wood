package main;
/**
 * 
 */

import java.io.IOException;
import java.util.Calendar;

import design1.Design1FromTables;
import domain.DesignEntry;

/**
 * @author Fede
 *
 */
public class Main {

	public static final String PATH = "E:\\WOODART\\";
	
	public static final Integer SAW_DISK_THICKNESS = 3;	// Al sizes in mm
	
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Throwable {
		Design1FromTables design = new Design1FromTables();
		design.runMultipleTimes();
		//design.build(true, 1569417286796061768L, false );
	}
	
}
