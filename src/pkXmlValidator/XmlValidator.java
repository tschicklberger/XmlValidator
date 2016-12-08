
package pkXmlValidator; 

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** 
 * Klasse zur Validierung von XML-Dateien gegen XSD-Dateien                  
 */
public class XmlValidator {

	/** aktuelle Versionsdatei fuer Ausgabe */
	public static final String prg_version = "R1V1.1.0-0001";
	/** Ausgabedateiname fuer logWriter-Methode */
	public static final String myLOGFile = "XmlValidator." + new SimpleDateFormat("yyyy_MM").format(new Date()) + ".log";
	
	/** FileWriter fuer logWriter-Methode */
	public static FileWriter fw = null;
	/** BufferedWriter fuer logWriter-Methode */
	public static BufferedWriter bw = null;
	
	/** Vordefinierte Ausgabe in Datei (f) via logWriter-Methode */
	public static char vFlag = 'f';
	/** XML-Datei aus Uebergabeparameter 2 */
	static String myXMLFile = null; 
	/** XSD-Datei aus Uebergabeparameter 3 */
	static String myXSDFile = null; 
	
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
		
		
		logWrite ("------------------------------------------------------------------------------------------------------------------------");		

		logWrite ("XmlValidator " + prg_version);
		logWrite ("(c) 2016 Drei Banken EDV GmbH");
		logWrite ("Autor: AELZV/SCHICKLBERGER, Thomas\r\n");
	
		logWrite ("Ausgefuehrt am: " + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(new Date()));
		logWrite ("Ausgefuehrt in: " + System.getProperty("user.dir") + "\r\n");
		logWrite ("LOG-Datei: " + new File (myLOGFile).getAbsolutePath());
		
		logWrite ("Parameter pruefen ...");
		// Uebergabeparameter aus Kommandozeile oder UC4 pruefen
		if (!checkParams(args)) {
			throw new Exception ("invalid arguments");
		}		
		
		logWrite ("XML-Datei: " + myXMLFile);
		logWrite ("XSD-Datei: " + myXSDFile + "\r\n");
		
		try {
			
			logWrite ("Dateien prüfen ...");

			if (!new File (myXMLFile).exists()) {
				throw new FileNotFoundException(myXMLFile); 
			} else {
				if (!new File (myXSDFile).exists()) {
					throw new FileNotFoundException(myXSDFile); 
				}
			}

			logWrite ("Schema vorbereiten ...");
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			logWrite ("Schema laden ...");
			File schemaFile = new File(myXSDFile);
			Schema schema = factory.newSchema(schemaFile);

			logWrite ("XML-Validator instanzieren ...");
			Validator validator = schema.newValidator();
			
			//System.out.println("Validator Class: " + validator.

					
			logWrite ("XML validieren ...");
			validator.validate(new StreamSource(myXMLFile));
		    
			logWrite ("Ergebnis: XML-Datei ist valide!");
			logWrite ("Programm beendet.");
			logWrite ("------------------------------------------------------------------------------------------------------------------------");		

			bw.close();
			System.exit(0);

		} 

		catch (SAXParseException e) {
			logWrite ("(e) Ergebnis: XML-Datei ist nicht valide!");
			
			logWrite ("(e) Zeile: " + e.getLineNumber());
			logWrite ("(e) Spalte: " + e.getColumnNumber());
			
			logWrite ("(e) Grund: " + e.getMessage().substring(e.getMessage().indexOf(":") + 2));
			logWrite ("------------------------------------------------------------------------------------------------------------------------");

			if (bw != null) { bw.close(); }
			System.exit(-1);
			
		}
		
		catch (SAXException e) {
			// XML-Instanz ist nicht valide
			logWrite ("(e) SAX Fehler!");
			logWrite ("(e) Grund: " + e.getMessage());
			logWrite ("------------------------------------------------------------------------------------------------------------------------");

			if (bw != null) { bw.close(); }
			System.exit(-1);
		} 
		
		catch (FileNotFoundException e) {
			logWrite ("Datei nicht gefunden " + e.getMessage().split(" ")[0]);
			logWrite ("------------------------------------------------------------------------------------------------------------------------");
		
			if (bw != null) { bw.close(); }
			System.exit(-2);
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			logWrite ("------------------------------------------------------------------------------------------------------------------------");
			if (bw != null) { bw.close(); }
			System.exit(-3);
		} 
		
		catch (Exception e) {
			e.printStackTrace();
			logWrite ("------------------------------------------------------------------------------------------------------------------------");
			if (bw != null) { bw.close(); }
			System.exit(-4);
		}
		
		finally {
		
			try {
				if (bw != null) { bw.close(); }
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
	public static boolean checkParams (String[] params) throws IOException {
		

		if (params.length > 2) {
			
			vFlag = params[0].charAt(0); // Ausgabeparameter aus Uebergabeparameter zuweisen 
				
			switch (vFlag) {
				case 's': case 'f': case 'b': case 'o': break;
				default: 
					System.out.println ("Fehler im ersten Parameter. Verbose Flag-Wert \"" + vFlag + "\" falsch!\r\n");
					System.out.println ("Moegliche Verbose Flags: [s|f|b]. \r\n");
					return false;
			}

			// Eingabe-Dateien aus den Uebergabeparametern zuweisen
			myXMLFile = params[1];
			myXSDFile = params[2];
			
		} else {
				System.out.println("Bitte Parameter ueberpruefen! Falsche Anzahl.\r\n"); 
				return false;
		} // if-else
		
		return true;
	}

	/**
	 * Methode zur Ausgabe einer Textzeile entweder in eine Logdatei, am Schirm, beides, oder unterdrueckt.
	 * Dazu muss vFlag mit entweder 'f', 's', 'b', oder 'o' belegt werden
	 * @author Thomas Schicklberger
	 * @version 2.1
	 * @param pLine Ausgabezeile
	 * @throws IOException
	 */
	public static void logWrite(String pLine) throws IOException {
			
		fw = new FileWriter(myLOGFile,true);
		bw = new BufferedWriter(fw);

		try {
			switch (vFlag) {
				case 's': 
					System.out.println (pLine);
					break;
			
				case 'b': 
					System.out.println (pLine);
					bw.write(pLine + "\r\n");
					break;
		
				case 'f': 
					bw.write(pLine + "\r\n");
					break;
				case 'o': // Ausgabe unterdruecken
					break;
				
				default:
					System.out.println(pLine);
			}
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-6);
		}
	}

} // XmlValidator

