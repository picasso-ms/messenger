package server;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Handler extends DefaultHandler {

    private String thisElement="";
    private  String message;
    private String sender;
    private String receiver;
    private String delete;

    public String getDelete() {
        return delete;
    }

    public String getReceiver() {
        return receiver;
    }



    public String getMessage() {
        return message;
    }


    public String getSender() {
        return sender;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        thisElement = qName;
        sender =  attributes.getValue("sender");
        receiver = attributes.getValue("receiver");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        if(thisElement.equals("message"))
            message = new String(ch,start,length);
        if(thisElement.equals("delete"))
            delete = new String(ch,start,length);

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }
}
