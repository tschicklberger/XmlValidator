package pkXMLValidator;

//------------------------------------------------------------------------------
//Name:    XMLValidator
//------------------------------------------------------------------------------
//Purpose: Ein XML gegen ein beliebiges XSD validieren 
//Author:  SCHICKLBERGER, Thomas (ST)
//Version: R1V1.0.0-0001
//------------------------------------------------------------------------------
//History:
//20161201.2300 - TS - Ersterstellung
//20161202.1400 - TS - erste funktionierende Version
//20161207.0600 - TS - Einbau Logging, Umbau auf Uebergabeparameter
//
//------------------------------------------------------------------------------
//Aufruf: java -jar XMLValidator.jar <verbose flag> <xml file> <xsd file>
//    
//        verbose flag: [s|f|b] 
//                  s ... Ausgabe auf Schirm
//                  f ... Ausgabe in Logdatei
//                  b ... Ausgabe in Logdatei und Schirm
//
//------------------------------------------------------------------------------
//Aufgaben:
// - Bei verbose flag s wird trotzdem eine Leerdatei erstellt
//------------------------------------------------------------------------------

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLValidator {

	// current release/version info for output
	public static final String prg_version = "R1V1.1.0-0001";

	// Logfile deklarieren
	public static FileWriter fw = null;
	public static BufferedWriter bw = null;
	
	public static void main(String[] args)  throws SAXException, FileNotFoundException, IOException {
		
		String myXMLFile = null; // XML-Datei aus Uebergabeparameter 2
		String myXSDFile = null; // XSD-Datei aus Uebergabeparameter 3
		
		String myLOGFile = "XMLValidator." + new SimpleDateFormat("yyyy_MM").format(new Date()) + ".log";
		
		char vFlag = 'f'; // Standardmaessig alles in Log-File ausgeben
		
		// Anzahl und Richtigkeit der Uebergabeparameter pruefen 
		if (args.length > 2) {
			
			vFlag = args[0].charAt(0);
				
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

			// Log-Ausgabe initialisieren
			fw = new FileWriter(myLOGFile,true);
			bw = new BufferedWriter(fw);

			writeLine (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");		

			writeLine (vFlag,bw,"XMLValidator " + prg_version);
			writeLine (vFlag,bw,"(c) 2016 Drei Banken EDV GmbH");
			writeLine (vFlag,bw,"Autor: AELZV/SCHICKLBERGER, Thomas\r\n");
		
			writeLine (vFlag,bw,"Ausgefuehrt am: " + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(new Date()));
			writeLine (vFlag,bw,"Ausgefuehrt in: " + System.getProperty("user.dir") + "\r\n");
			writeLine (vFlag,bw,"LOG-Datei: " + new File (myLOGFile).getAbsolutePath());
			writeLine (vFlag,bw,"XML-Datei: " + myXMLFile);
			writeLine (vFlag,bw,"XSD-Datei: " + myXSDFile + "\r\n");
			writeLine (vFlag,bw,"Dateien prüfen ...");

			if (!new File (myXMLFile).exists()) {
				throw new FileNotFoundException(myXMLFile); 
			} else {
				if (!new File (myXSDFile).exists()) {
					throw new FileNotFoundException(myXSDFile); 
				}
			}

			writeLine (vFlag,bw,"Schema vorbereiten ...");
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			writeLine (vFlag,bw,"Schema laden ...");
			File schemaFile = new File(myXSDFile);
			Schema schema = factory.newSchema(schemaFile);

			writeLine (vFlag,bw,"XML-Validator instanzieren ...");
			Validator validator = schema.newValidator();
			
			//System.out.println("Validator Class: " + validator.

					
			writeLine (vFlag,bw,"XML validieren ...");
			validator.validate(new StreamSource(myXMLFile));
		    
			writeLine (vFlag,bw,"Ergebnis: XML-Datei ist valide!");
			writeLine (vFlag,bw,"Programm beendet.");
			writeLine (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");		

			bw.close();
			System.exit(0);

		} 

		catch (SAXParseException e) {
			writeLine (vFlag,bw,"(e) Ergebnis: XML-Datei ist nicht valide!");
			
			writeLine (vFlag,bw,"(e) Zeile: " + e.getLineNumber());
			writeLine (vFlag,bw,"(e) Spalte: " + e.getColumnNumber());
			
			writeLine (vFlag,bw,"(e) Grund: " + e.getMessage().substring(e.getMessage().indexOf(":") + 2));
			writeLine (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");

			if (bw != null) { bw.close(); }
			System.exit(-1);
			
		}
		
		catch (SAXException e) {
			// XML-Instanz ist nicht valide
			writeLine (vFlag,bw,"(e) SAX Fehler!");
			writeLine (vFlag,bw,"(e) Grund: " + e.getMessage());
			writeLine (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");

			if (bw != null) { bw.close(); }
			System.exit(-1);
		} 
		
		catch (FileNotFoundException e) {
			writeLine (vFlag,bw,"Datei nicht gefunden " + e.getMessage().split(" ")[0]);
			writeLine (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");
		
			if (bw != null) { bw.close(); }
			System.exit(-2);
		} 
		
		catch (IOException e) {
			e.printStackTrace();
			writeLine (vFlag,bw,"------------------------------------------------------------------------------------------------------------------------");
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

	// ------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------

	public static void writeLine(char pFlag, BufferedWriter pBW, String pLine) throws IOException {
		
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

} // XMLValidator

