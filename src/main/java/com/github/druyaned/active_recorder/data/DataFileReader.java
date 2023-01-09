package com.github.druyaned.active_recorder.data;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.github.druyaned.active_recorder.active.ActiveCalendar;
import com.github.druyaned.active_recorder.active.ActiveMode;
import com.github.druyaned.active_recorder.active.Activity;
import java.time.Instant;

public class DataFileReader {

    /**
     * Reads provided {@link DataFile data file} and returns
     * {@link ActiveCalendar active calendar} as a result.
     * 
     * @param aFile data file to read; extension {@code .xml}.
     * @return {@link ActiveCalendar} that was read.
     * @throws SAXException in same cases as {@link DocumentBuilder#parse}.
     * @throws IOException in same cases as {@link DocumentBuilder#parse}.
     */
    public static Activity[] read(DataFile aFile) throws SAXException, IOException {

        // prepare to read
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true); // to control DTD validation
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        builder.setErrorHandler(new DefaultHandler());

        // read the file
        Document document = builder.parse(aFile.asFile());

        // prepare to read the document
        Element root = document.getDocumentElement();
        NodeList activeTimesList = root.getChildNodes();
        int n = activeTimesList.getLength();
        
        // read the document
        Activity[] activities = new Activity[n];
        for (int i = 0; i < n; ++i) {
            Node activeTimeNode = activeTimesList.item(i);
            NodeList fields = activeTimeNode.getChildNodes();
            Node startNode = fields.item(0);
            Node stopNode = fields.item(1);
            Node modeNode = fields.item(2);
            Node descrNode = fields.item(3);
            Instant start = Instant.parse(startNode.getTextContent());
            Instant stop = Instant.parse(stopNode.getTextContent());
            ActiveMode mode = ActiveMode.valueOf(modeNode.getTextContent());
            String descr = descrNode.getTextContent();
            activities[i] = new Activity(start, stop, mode, descr);
        }

        return activities;
    }
}
