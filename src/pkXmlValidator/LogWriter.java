package pkXmlValidator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Klasse zur Ausgabe von Log-Eintraegen in eine Datei, auf den Bildschirm, oder beides.
 * Die verwendete Methode zum Schreiben ist PrintWriter.
 * Jede Zeile wird sofort in die Ausgabedatei via flush()-Methode geschrieben.
 * <br><br>
 * Um den LogWriter verwenden zu koennen, muss zuerst die Klasse initialisiert werden.
 * Das geschieht mit der Methode {@link #init(verboseFlagSet, String) init()}.
 * <br><br>
 * Um die Klasse ordnungsgemaess zu beenden gibt es eine Implementierung der Methode {@link #close() close()}
 * Diese schreibt etwaige gepufferte Texte in die Logdatei und schliesst sie daraufhin.
 * 
 * @author Thomas Schicklberger
 * @version 2.0
 *
 */
public class LogWriter implements AutoCloseable {
	
	/** Konstante Zeichenkette mit 80 Bindestrichen */
	public final String SDASH80  = "--------------------------------------------------------------------------------";
	/** Konstante Zeichenkette mit 120 Bindestrichen */
	public final String SDASH120 = "------------------------------------------------------------------------------------------------------------------------";
	
	/** Auflistung moeglicher Ausgabekanaele fuer LogWriter */
	public enum verboseFlagSet {
		/** Ausgabe in Datei */ 
		VB_FILE, 
		/** Ausgabe auf Bildschirm/Konsole */ 
		VB_SCREEN, 
		/** Ausgabe in Datei und Bildschirm/Konsole */ 
		VB_BOTH, 
		/** Ausgabe wird unterdrueckt */ 
		VB_OMIT
	};
	
	/** Gewaehlter Ausgabekanal */
	public verboseFlagSet verboseFlag = null;
	
	public LogWriter verboseFlag(verboseFlagSet verboseFlag) {
		this.verboseFlag = verboseFlag;
		return this;
	}
	
	public verboseFlagSet verboseFlag() {
		return verboseFlag;
	} 

	/** Dateiname fuer Logdatei-Ausgabe */
	public String logFileName = null;
		
	public LogWriter logFileName(String logFileName) {
		this.logFileName = logFileName;
		return this;
	}
	
	public String logFileName() {
		return logFileName;
	}
	
	private static FileWriter fileWriter = null;
	private static PrintWriter printWriter = null;

	/**
	 * Die Methode init() legt die im Argument LogFileName uebergebene Datei an, sofern diese noch
	 * nicht existiert. Sollte die Datei schon vorhanden sein, wird sie zum anhaengend schreiben
	 * geoeffnet. <br><br>
	 * Mit dem Parameter VerboseFlag wird der Methode {@link #writeLog(String) writeLog()} mitgeteilt,
	 * wo die Ausgabe erfolgen soll.
	 * <br>
	 * VB_FILE ... Datei<br>
	 * VB_SCREEN ... Bildschirm/Konsole<br>
	 * VB_BOTH ... Datei und Bildschirm/Konsole<br>
	 * VB_OMIT ... Ausgabe unterdrücken<br>
	 * <br>
	 * Der Parameter VB_OMIT scheint im ersten Moment sonderbar, die Ausgabe unterdruecken zu koennen,
	 * kann bei Testfaellen und grossen Ausgabedatenmengen aber durchaus sinnvoll sein.
	 * <br><br>
	 * Beispiel:<br>
	 * LogWriter lw = new LogWriter();<br>
	 * lw.init(LogWriter.verboseFlagSet.VB_BOTH,"c:\\data\\mytest.log");
	 * 
	 * @author Thomas Schicklberger
	 * @version 2.0
	 * @param verboseFlag wo soll die Ausgabe erfolgen
	 * @param logFileName Name der Ausgabedatei
	 * @return LogWriter das Methodenobjekt selbst
	 * @throws IOException wenn Datei nicht erstellt, oder geoeffnet werden kann.
	 */
	public LogWriter init (verboseFlagSet verboseFlag, String logFileName) throws IOException {
		
		this.logFileName = logFileName;
		this.verboseFlag = verboseFlag;
		
		try {
			fileWriter = new FileWriter(logFileName,true);	
			printWriter = new PrintWriter(fileWriter);
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return this;	
		
	} // init()
	
	/**
	 * Die Methode close() ist eine Ableitung der allgemeinen close()-Methode der Klasse
	 * AutoCloseable in der Klassen-Deklaration. Die Methode schreibt alle noch offenen
	 * Zeichenketten in die Ausgabedatei und schliesst sie daraufhin.
	 * 
	 * @author Thomas Schicklberger
	 * @version 2.0
	 * @throws IOException wenn Datei nicht beschrieben, oder geschlossen werden kann.
	 * 
	 * @see java.lang.AutoCloseable#close()
	 * 
	 */
	@Override public void close () throws IOException {

		try {
			fileWriter.flush();
			if (fileWriter != null) { fileWriter.close(); }
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-5);
		}	
	} // close()
	
    /**
     * Die Methode writeLog() ist die eigentliche Funktion zur Ausgabe von Zeichenketten, in
     * der in der {@link #init(verboseFlagSet, String) init()}-Methode deklarierten Ausgabevariante.
     * 
     * @author Thomas Schicklberger
     * @version 2.0
     * @param writeLogText Textzeile die ausgegeben werden soll
     */
    public void writeLog (String writeLogText) {
    	
    	switch (verboseFlag) {
  
    		case VB_SCREEN: 
    			System.out.println (writeLogText);
    			break;
	
    		case VB_BOTH: 
    			System.out.println (writeLogText);
    			printWriter.write(writeLogText + "\r\n");
    			printWriter.flush();
    			break;

    		case VB_FILE: 
    			printWriter.write(writeLogText + "\r\n");
    			printWriter.flush();
				break;
			
    		case VB_OMIT:
    			// Ausgabe wird komplett unterdrueckt
    			break;
		
    		default:
    			System.err.println("LogWriter.writeLog: unbekanntes VerboseFlag " + verboseFlag);
    	}    	
    } // writeLog()
}
