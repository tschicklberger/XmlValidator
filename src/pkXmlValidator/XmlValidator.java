package pkXmlValidator; 
// Validator Kapsel
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
// new branch 
/** 
 * Klasse zur Validierung von XML-Dateien gegen XSD-Dateien                  
 */
public class XmlValidator {

	/** aktuelle Versionsdatei fuer Ausgabe */
	public static final String PRG_VERSION = "R1V1.2.0-0001";
	/** Ausgabedateiname fuer logWriter-Methode */
	public static final String XMLVLOGFILE = "XmlValidator." + new SimpleDateFormat("yyyy_MM").format(new Date()) + ".log";
		
	/** Vordefinierte Ausgabe in Datei (VB_BOTH) via logWriter-Methode */
	private static LogWriter.verboseFlagSet verboseFlag = LogWriter.verboseFlagSet.VB_BOTH;
	/** XML-Datei aus Uebergabeparameter 2 */
	private static String validatorXmlFile = null; 
	/** XSD-Datei aus Uebergabeparameter 3 */
	private static String validatorXsdFile = null; 
	
	private static LogWriter lw = null;
	
	/** 
	 * Hauptmethode (main) zur Validierung von XML-Dateien via XSD-Datei
	 * @author Thomas Schicklberger
	 * @version 1.0                                         
	 * @param args Kommandozeile: <Verbose-Flag [f|s|b|o]> <XML-Datei> <XSD-Datei>
	 * @throws SAXParseException wenn XML nicht valide, bzw. XSD nicht gefunden wird
	 * @throws SAXException wenn anderer XML-Fehler vorliegt
	 * @throws FileNotFoundException wenn die angegebene XML-Datei nicht gefunden wird
	 * @throws IOException wenn Schreib-/Lesefehler bei Log-, XML-, oder XSD-Datei auftreten
	 * @return void
	*/
	public static void main(String[] args)  throws SAXParseException, SAXException, FileNotFoundException, IOException, Exception {
		
		// Uebergabeparameter aus Kommandozeile oder UC4 pruefen
		if (!checkParams(args)) {
			lw.close();
			throw new Exception ("invalid arguments");
		}
		
		// LogWriter initialisieren
		LogWriter lw = new LogWriter();
		lw.init(verboseFlag,XMLVLOGFILE);
		
		lw.writeLog(lw.SDASH120);		

		lw.writeLog ("XmlValidator " + PRG_VERSION);
		lw.writeLog ("(c) 2016 Drei Banken EDV GmbH");
		lw.writeLog ("Autor: AELZV/SCHICKLBERGER, Thomas\r\n");
	
		lw.writeLog ("Ausgefuehrt am: " + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(new Date()));
		lw.writeLog ("Ausgefuehrt in: " + System.getProperty("user.dir") + "\r\n");
		lw.writeLog ("LOG-Datei: " + XMLVLOGFILE);

		lw.writeLog ("XML-Datei: " + validatorXmlFile);
		lw.writeLog ("XSD-Datei: " + validatorXsdFile + "\r\n");
		
		try {
			
			lw.writeLog ("Dateien prüfen ...");

			if (!new File (validatorXmlFile).exists()) {
				throw new FileNotFoundException(validatorXmlFile); 
			} else {
				if (!new File (validatorXsdFile).exists()) {
					throw new FileNotFoundException(validatorXsdFile); 
				}
			}

			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			lw.writeLog ("Schema laden ...");
			File schemaFile = new File(validatorXsdFile);
			Schema schema = factory.newSchema(schemaFile);

			lw.writeLog ("XML-Validator instanzieren ...");
			Validator validator = schema.newValidator();
		
			lw.writeLog ("XML validieren ...");
			validator.validate(new StreamSource(validatorXmlFile));
			
			lw.writeLog ("Ergebnis: XML-Datei ist valide!");
			lw.writeLog ("Programm beendet.");
			lw.writeLog (lw.SDASH120);		

			lw.close();
			System.exit(0);

		} 

		catch (SAXParseException e) {
			lw.writeLog ("(e) Ergebnis: XML-Datei ist nicht valide!");
			
			lw.writeLog ("(e) Zeile: " + e.getLineNumber());
			lw.writeLog ("(e) Spalte: " + e.getColumnNumber());
			
			lw.writeLog ("(e) Grund: " + e.getMessage().substring(e.getMessage().indexOf(":") + 2));
			lw.writeLog ("------------------------------------------------------------------------------------------------------------------------");

			lw.close();
			System.exit(-1);
			
		}
		
		catch (SAXException e) {
			// XML-Instanz ist nicht valide
			lw.writeLog ("(e) SAX Fehler!");
			lw.writeLog ("(e) Grund: " + e.getMessage());
			lw.writeLog ("------------------------------------------------------------------------------------------------------------------------");

			lw.close();
			System.exit(-1);
		} 
		
		catch (FileNotFoundException e) {
			lw.writeLog ("Datei nicht gefunden " + e.getMessage().split(" ")[0]);
			lw.writeLog ("------------------------------------------------------------------------------------------------------------------------");
		
			lw.close();
			System.exit(-2);
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			lw.writeLog ("------------------------------------------------------------------------------------------------------------------------");
			lw.close();
			System.exit(-3);
		} 
		
		catch (Exception e) {
			e.printStackTrace();
			lw.writeLog ("------------------------------------------------------------------------------------------------------------------------");
			lw.close();
			System.exit(-4);
		}
		
		finally {
		
			try {
				lw.close();
			} 
			
			catch (IOException e) {
				e.printStackTrace();
				System.exit(-5);
			}
		}
	}

	/**
	 * Methode zur Ueberpruefung der Uebergabeparameter aus der Kommandozeile
	 * @author Thomas Schicklberger
	 * @version 1.0
	 * @param params Uebergabeparameter aus der Hauptklasse (Kommandozeile)
	 * @return true wenn alle in Ordnung, false, wenn die Parameter nicht passen
	 * @throws IOException
	 */
	static boolean checkParams (String[] params) throws IOException {
		
		if (params.length > 2) {
			
			switch (LogWriter.verboseFlagSet.valueOf(params[0])) {
				case VB_FILE: case VB_SCREEN: case VB_BOTH: case VB_OMIT: break;
				default: 
					System.out.println ("Fehler im ersten Parameter. Verbose Flag-Wert \"" + verboseFlag + "\" falsch!\r\n");
					System.out.println ("Moegliche Verbose Flags: [VB_FILE|VB_SCREEN|VB_BOTH|VB_OMIT]. \r\n");
					return false;
			}

			verboseFlag = LogWriter.verboseFlagSet.valueOf(params[0]);
			validatorXmlFile = params[1];
			validatorXsdFile = params[2];
			
		} else {
				System.out.println("Bitte Parameter ueberpruefen! Falsche Anzahl.\r\n"); 
				return false;
		} // if-else
		
		return true;
	}

} // XmlValidator

