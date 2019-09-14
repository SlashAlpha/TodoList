package com.learning.duprat.todolist.datamodel;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class DataDoubleStore {

    private static final String TODOITEM = "todoitem";
    private static final String SHORTDESC = "short_descritpion";
    private static final String DETAILS = "details";
    private static final String DEADLINE = "deadline";
    private static final String REMAINING = "REMAINING";
    private static final String COMPLETED = "completed";
    private static final String FILEINFO = "file_info";
    private static final String STORE = "store";
    private static String LIST_FILE = "";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    // *** initialize the contacts list here ***


    public DataDoubleStore(String listFile) {
        this.LIST_FILE = (listFile == null) ? "default.xml" : listFile;
    }

    //
    // *** Add methods to add/delete/access contacts here ***
    public void addContact(ToDoItem item) {
        ToDoData.getInstance().addToDoItem(item);
    }

    public void loadContacts() {
        ToDoData.getInstance().initiateList();
        LocalDate date = LocalDate.now();
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream(LIST_FILE);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            ToDoItem item = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a contact item, we create a new contact
                    if (startElement.getName().getLocalPart().equals(TODOITEM)) {
                        item = new ToDoItem("", "", null, 0, false, 0, "");
                        continue;
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart()
                                .equals(SHORTDESC)) {
                            event = eventReader.nextEvent();
                            item.setShortDescription(event.asCharacters().getData());
                            continue;
                        }
                    }
                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DETAILS)) {
                        event = eventReader.nextEvent();
                        item.setDetails(returnString(event.toString()));
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DEADLINE)) {
                        event = eventReader.nextEvent();
                        String deadLine = event.asCharacters().getData();
                        LocalDate dateline = LocalDate.parse(deadLine, formatter);
                        item.setDeadLine(dateline);
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart()
                            .equals(REMAINING)) {
                        event = eventReader.nextEvent();
                        String str = event.asCharacters().getData();
                        int remaining = Integer.parseInt(str);
                        item.setRemaining(remaining);
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart()
                            .equals(COMPLETED)) {
                        event = eventReader.nextEvent();
                        String str = event.asCharacters().getData();
                        boolean completed = Boolean.parseBoolean(str);
                        item.setCompleted(completed);
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(STORE)) {
                        event = eventReader.nextEvent();
                        item.setStore(returnString(event.asCharacters().getData()));
                        continue;
                    }

                }

                // If we reach the end of a contact element, we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(TODOITEM)) {
                        ToDoData.getInstance().getToDoItems().add(item);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void saveContacts() {

        try {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(LIST_FILE));
            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(end);

            StartElement contactsStartElement = eventFactory.createStartElement("",
                    "", "items");
            eventWriter.add(contactsStartElement);
            eventWriter.add(end);

            for (ToDoItem item : ToDoData.getInstance().getToDoItems()) {
                saveContact(eventWriter, eventFactory, item);
            }

            eventWriter.add(eventFactory.createEndElement("", "", "contacts"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Problem with Contacts file: " + e.getMessage());
            e.printStackTrace();
        } catch (XMLStreamException e) {
            System.out.println("Problem writing contact: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveContact(XMLEventWriter eventWriter, XMLEventFactory eventFactory, ToDoItem item)
            throws FileNotFoundException, XMLStreamException {

        XMLEvent end = eventFactory.createDTD("\n");

        // create contact open tag
        StartElement configStartElement = eventFactory.createStartElement("",
                "", TODOITEM);
        eventWriter.add(configStartElement);
        eventWriter.add(end);
        // Write the different nodes
        createNode(eventWriter, SHORTDESC, dataEmptyCheck(item.getShortDescription()));
        createNode(eventWriter, DETAILS, splitString(item.getDetails()));
        createNode(eventWriter, DEADLINE, dataEmptyCheck(item.getDeadLine().format(formatter)));
        createNode(eventWriter, REMAINING, dataEmptyCheck(Integer.toString(item.getRemaining())));
        createNode(eventWriter, COMPLETED, dataEmptyCheck(Boolean.toString(item.isCompleted())));
        createNode(eventWriter, STORE, splitString(item.getStore()));


        eventWriter.add(eventFactory.createEndElement("", "", TODOITEM));
        eventWriter.add(end);
    }

    private void createNode(XMLEventWriter eventWriter, String name,
                            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }

    public String dataEmptyCheck(String s) {
        if (s.equals("")) {
            return " ";
        } else {
            return s;
        }
    }

    public String splitString(String s) {
        String[] str = s.split("\n");
        String r = "";
        for (String i : str) {
            r += i + "cucarraccaGiraffe";
        }
        return r;
    }

    public String returnString(String s) {
        String[] str = s.split("cucarraccaGiraffe");
        String r = "";
        for (String i : str) {
            r += i + "\n";
        }
        return r;
    }
}
