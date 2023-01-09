package com.github.druyaned.active_recorder.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.helpers.DefaultHandler;
import com.github.druyaned.active_recorder.active.ActiveCalendar;
import com.github.druyaned.active_recorder.active.Activity;

public class DataFileWriter {

    /**
     * Writes provided {@link ActiveCalendar active calendar} in the {@link DataFile data file}.
     * 
     * @param calendar to be written in the {@code aFile}.
     * @param aFile data file to write active calendar; extension {@code .xml}.
     * @param configFile to write correctly; extension {@code .dtd}.
     * @throws FileNotFoundException {@link FileOutputStream#FileOutputStream(File)}.
     * @throws TransformerException in same cases as {@link Transformer#transform(Source, Result)}.
     */
    public static void write(ActiveCalendar calendar, DataFile aFile, ConfigFile configFile)
        throws FileNotFoundException, TransformerException {
        
        // prepare to write
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        builder.setErrorHandler(new DefaultHandler());
        Document document = builder.newDocument();
        
        // write in the document
        Element root = document.createElement("activities");
        document.appendChild(root);
        for (int i = 0; i < calendar.getActivitiesSize(); ++i) {
            Element item = document.createElement("item");
            root.appendChild(item);
            Activity a = calendar.getActivity(i);
            Element startElem = document.createElement("start");
            startElem.setTextContent(a.getStart().toString());
            item.appendChild(startElem);
            Element stopElem = document.createElement("stop");
            stopElem.setTextContent(a.getStop().toString());
            item.appendChild(stopElem);
            Element modeElem = document.createElement("mode");
            modeElem.setTextContent(a.getMode().toString());
            item.appendChild(modeElem);
            Element descrElem = document.createElement("descr");
            descrElem.setTextContent(a.getDescr());
            item.appendChild(descrElem);
        }

        // prepare to write in the file
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, configFile.getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        String indentAmountKeyName = "{http://xml.apache.org/xslt}indent-amount";
        transformer.setOutputProperty(indentAmountKeyName, "2");

        // write in the file
        StreamResult fileStream = new StreamResult(new FileOutputStream(aFile.asFile()));
        transformer.transform(new DOMSource(document), fileStream);
    }
}
