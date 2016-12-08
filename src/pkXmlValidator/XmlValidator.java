
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
	/** FileWrite für Logdatei */
	public static FileWriter fw = null;
	/** BufferedWriter für logdatei */
	public static BufferedWriter bw = null;
	
	/** 
	 * Hauptmethode (main) zur Validierung von XML-Dateien via XSD-Datei
	 * @author Thomas Schicklberger ^
	 * @version 1.0                                         
	 * @param args Kommandozeile
	 * @throws SAXParseException wenn XML nicht valide, bzw. XSD nicht gefunden wird
	 * @throws SAXException wenn anderer XML-Fehler vorliegt
	 * @throws FileNotFoundException wenn die angegebene XML-Datei nicht gefunden wird
	 * @throws IOException wenn Schreib-/Lesefehler bei Log-, XML-, oder XSD-Datei auftreten
	 * @return void
	*/
	public static void main(String[] args)  throws SAXParseException, SAXException, FileNotFoundException, IOException {
		
		/** XML-Datei aus Uebergabeparameter 2 */
		String myXMLFile = null; 
		String myXSDFile = null; /** XSD-Datei aus Uebergabeparameter 3 */
		String myLOGFile = "XmlValidator." + new SimpleDateFormat("yyyy_MM").format(new Date()) + ".log"; /** LogFile */
		
		// Vordefinierte Ausgabe in Datei (f) via LogWriter 
		char vFlag = 'f'; 
		
		// Anzahl und Richtigkeit der Uebergabeparameter pruefen 
		if (args.length > 2) {
			
			vFlag = args[0].charAt(0); // Ausgabeparameter aus Uebergabeparameter zuweisen 
				
			switch (vFlag) {
				case 's': case 'f': case 'b': break;
				default: 
					System.out.println ("Fehler im ersten Parameter. Verbose Flag-Wert \"" + vFlag + "\" falsch!\r\n");
					System.out.println ("Moegliche Verbose Flags: [s|f|b]. \r\n");
					return;
			}

			// Eingabe-Dateien aus den Uebergabeparametern zuweisen
			myXMLFile = args[1];
			myXSDFile = args[2];
			
		} else {
				System.out.println("Bitte Parameter ueberpruefen! Falsche Anzahl.\r\n"); 
				return;
		} // if-else
		
		try {

			/** Log-Ausgabe initialisieren */
			fw = new FileWriter(myLOGFile,true);
			bw = new BufferedWriter(fw);

			logWrite (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");		

			logWrite (vFlag,bw,"XmlValidator " + prg_version);
			logWrite (vFlag,bw,"(c) 2016 Drei Banken EDV GmbH");
			logWrite (vFlag,bw,"Autor: AELZV/SCHICKLBERGER, Thomas\r\n");
		
			logWrite (vFlag,bw,"Ausgefuehrt am: " + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(new Date()));
			logWrite (vFlag,bw,"Ausgefuehrt in: " + System.getProperty("user.dir") + "\r\n");
			logWrite (vFlag,bw,"LOG-Datei: " + new File (myLOGFile).getAbsolutePath());
			logWrite (vFlag,bw,"XML-Datei: " + myXMLFile);
			logWrite (vFlag,bw,"XSD-Datei: " + myXSDFile + "\r\n");
			logWrite (vFlag,bw,"Dateien prüfen ...");

			if (!new File (myXMLFile).exists()) {
				throw new FileNotFoundException(myXMLFile); 
			} else {
				if (!new File (myXSDFile).exists()) {
					throw new FileNotFoundException(myXSDFile); 
				}
			}

			logWrite (vFlag,bw,"Schema vorbereiten ...");
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			logWrite (vFlag,bw,"Schema laden ...");
			File schemaFile = new File(myXSDFile);
			Schema schema = factory.newSchema(schemaFile);

			logWrite (vFlag,bw,"XML-Validator instanzieren ...");
			Validator validator = schema.newValidator();
			
			//System.out.println("Validator Class: " + validator.

					
			logWrite (vFlag,bw,"XML validieren ...");
			validator.validate(new StreamSource(myXMLFile));
		    
			logWrite (vFlag,bw,"Ergebnis: XML-Datei ist valide!");
			logWrite (vFlag,bw,"Programm beendet.");
			logWrite (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");		

			bw.close();
			System.exit(0);

		} 

		catch (SAXParseException e) {
			logWrite (vFlag,bw,"(e) Ergebnis: XML-Datei ist nicht valide!");
			
			logWrite (vFlag,bw,"(e) Zeile: " + e.getLineNumber());
			logWrite (vFlag,bw,"(e) Spalte: " + e.getColumnNumber());
			
			logWrite (vFlag,bw,"(e) Grund: " + e.getMessage().substring(e.getMessage().indexOf(":") + 2));
			logWrite (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");

			if (bw != null) { bw.close(); }
			System.exit(-1);
			
		}
		
		catch (SAXException e) {
			// XML-Instanz ist nicht valide
			logWrite (vFlag,bw,"(e) SAX Fehler!");
			logWrite (vFlag,bw,"(e) Grund: " + e.getMessage());
			logWrite (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");

			if (bw != null) { bw.close(); }
			System.exit(-1);
		} 
		
		catch (FileNotFoundException e) {
			logWrite (vFlag,bw,"Datei nicht gefunden " + e.getMessage().split(" ")[0]);
			logWrite (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");
		
			if (bw != null) { bw.close(); }
			System.exit(-2);
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			logWrite (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");
			if (bw != null) { bw.close(); }
			System.exit(-3);
		} 
		
		finally {
		
			try {
				if (bw != null) { bw.close(); }
			} 
			
			catch (IOException e) {
				e.printStackTrace();
				System.exit(-4);
			}
		}

	}


	/**
	 * Methode zur Ausgabe einer Textzeile entweder in eine Logdatei, am Schirm, oder beides
	 * @author Thomas Schicklberger
	 * @param pFlag Ausgabeflag: s Schirm, b Beides, f Datei
	 * @param pBW AusgabeObjekt
	 * @param pLine Ausgabezeile
	 * @throws IOException
	 */
	public static void logWrite(char pFlag, BufferedWriter pBW, String pLine) throws IOException {
		
		try {
			switch (pFlag) {
				case 's': 
					System.out.println (pLine);
					break;
			
				case 'b': 
					System.out.println (pLine);
					pBW.write(pLine + "\r\n");
					break;
		
				case 'f': 
					pBW.write(pLine + "\r\n");
					break;
				
				default:
					System.out.println(pLine);
			}
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-5);
		}
	}

} // XmlValidator

