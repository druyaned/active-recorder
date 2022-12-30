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
import com.github.druyaned.active_recorder.active.ActiveTime;
import com.github.druyaned.active_recorder.time.DateTime;

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
    public static ActiveCalendar read(DataFile aFile) throws SAXException, IOException {

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
        ActiveTime[] activeTimes = new ActiveTime[n];
        for (int i = 0; i < n; ++i) {

            // declare
            Node activeTimeNode = activeTimesList.item(i);
            NodeList fields = activeTimeNode.getChildNodes();
            Node startNode = fields.item(0);
            Node stopNode = fields.item(1);
            Node modeNode = fields.item(2);
            Node descrNode = fields.item(3);

            // start field
            NodeList startList = startNode.getChildNodes();
            int year1 = Integer.parseInt(startList.item(0).getTextContent());
            int month1 = Integer.parseInt(startList.item(1).getTextContent());
            int day1 = Integer.parseInt(startList.item(2).getTextContent());
            int h1 = Integer.parseInt(startList.item(3).getTextContent());
            int m1 = Integer.parseInt(startList.item(4).getTextContent());
            int s1 = Integer.parseInt(startList.item(5).getTextContent());
            DateTime start = DateTime.of(year1, month1, day1, h1, m1, s1);

            // stop field
            NodeList stopList = stopNode.getChildNodes();
            int year2 = Integer.parseInt(stopList.item(0).getTextContent());
            int month2 = Integer.parseInt(stopList.item(1).getTextContent());
            int day2 = Integer.parseInt(stopList.item(2).getTextContent());
            int h2 = Integer.parseInt(stopList.item(3).getTextContent());
            int m2 = Integer.parseInt(stopList.item(4).getTextContent());
            int s2 = Integer.parseInt(stopList.item(5).getTextContent());
            DateTime stop = DateTime.of(year2, month2, day2, h2, m2, s2);

            // mode field
            ActiveMode mode = ActiveMode.valueOf(modeNode.getTextContent());

            // description field
            String descr = descrNode.getTextContent();

            // activeTime item
            activeTimes[i] = new ActiveTime(start, stop, mode, descr);
        }

        return new ActiveCalendar(activeTimes);
    }
}
