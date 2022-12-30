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
import com.github.druyaned.active_recorder.active.ActiveMode;
import com.github.druyaned.active_recorder.active.Activity;
import com.github.druyaned.active_recorder.time.DateTime;

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
        throws FileNotFoundException, TransformerException
    {
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
        Element root = document.createElement("activeTimes");
        document.appendChild(root);
        for (int i = 0; i < calendar.getActivitiesSize(); ++i) {
            
            // active time item
            Element item = document.createElement("item");
            root.appendChild(item);

            // declare
            Activity a = calendar.getActivity(i);
            DateTime start = a.getStart();
            DateTime stop = a.getStop();
            ActiveMode mode = a.getMode();
            String descr = a.getDescription();

            // start field
            Element startElem = document.createElement("start");
            Element yearStartElem = document.createElement("year");
            yearStartElem.setTextContent(Integer.toString(start.getYear()));
            Element monthStartElem = document.createElement("month");
            monthStartElem.setTextContent(Integer.toString(start.getMonth()));
            Element dayStartElem = document.createElement("day");
            dayStartElem.setTextContent(Integer.toString(start.getDay()));
            Element hourStartElem = document.createElement("hour");
            hourStartElem.setTextContent(Integer.toString(start.hour));
            Element minuteStartElem = document.createElement("minute");
            minuteStartElem.setTextContent(Integer.toString(start.minute));
            Element secondStartElem = document.createElement("second");
            secondStartElem.setTextContent(Integer.toString(start.second));
            startElem.appendChild(yearStartElem);
            startElem.appendChild(monthStartElem);
            startElem.appendChild(dayStartElem);
            startElem.appendChild(hourStartElem);
            startElem.appendChild(minuteStartElem);
            startElem.appendChild(secondStartElem);
            item.appendChild(startElem);

            // stop field
            Element stopElem = document.createElement("stop");
            Element yearStopElem = document.createElement("year");
            yearStopElem.setTextContent(Integer.toString(stop.getYear()));
            Element monthStopElem = document.createElement("month");
            monthStopElem.setTextContent(Integer.toString(stop.getMonth()));
            Element dayStopElem = document.createElement("day");
            dayStopElem.setTextContent(Integer.toString(stop.getDay()));
            Element hourStopElem = document.createElement("hour");
            hourStopElem.setTextContent(Integer.toString(stop.hour));
            Element minuteStopElem = document.createElement("minute");
            minuteStopElem.setTextContent(Integer.toString(stop.minute));
            Element secondStopElem = document.createElement("second");
            secondStopElem.setTextContent(Integer.toString(stop.second));
            stopElem.appendChild(yearStopElem);
            stopElem.appendChild(monthStopElem);
            stopElem.appendChild(dayStopElem);
            stopElem.appendChild(hourStopElem);
            stopElem.appendChild(minuteStopElem);
            stopElem.appendChild(secondStopElem);
            item.appendChild(stopElem);

            // mode field
            Element modeElem = document.createElement("mode");
            modeElem.setTextContent(mode.toString());
            item.appendChild(modeElem);

            // description field
            Element descrElem = document.createElement("descr");
            descrElem.setTextContent(descr);
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
