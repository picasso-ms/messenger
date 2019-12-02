package clientapp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Handler extends DefaultHandler {

    private String thisElement="";
    private  String login;
    private  String message;
    private String delete;

    public String getDelete() {
        return delete;
    }



    public String getLogin() {
        return login;
    }
    private String partner;

    public String getPartner() {
        return partner;
    }



    public String getMessage() {
        return message;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        thisElement = qName;
        partner = attributes.getValue("partner");

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        if(thisElement.equals("login"))
            login = new String(ch,start,length);
        if(thisElement.equals("message"))
            message = new String(ch,start,length);
        if(thisElement.equals("d"))
            delete = new String(ch,start,length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }
}
