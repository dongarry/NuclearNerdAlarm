package com.nerd.alarm;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* Credits
 *  http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
 * 
 * Nerd Alarm - To access weather info, we download from the google api but we handle the
 *  xml file ourselves, this is where we parse it
 */
public class XmlHandler extends DefaultHandler{
	
	
	// We just open the XML file and get the Condition, Wind and temp (in Celcius)
	private boolean in_outertag = false;
	private boolean in_innertag = false;
	private boolean in_current = false;
	       
	private ParsedXmlSet _xmlDataSet = new ParsedXmlSet();
	 
	public ParsedXmlSet getParsedData() {return this._xmlDataSet;}
	 	
	@Override
	public void startDocument() throws SAXException {this._xmlDataSet = new ParsedXmlSet();}
	
	@Override
	public void endDocument() throws SAXException {}
	 
	// Gets be called on opening tags 	
	@Override
	public void startElement(String namespaceURI, String localName,
	                        String qName, Attributes atts) throws SAXException {
	                
		if (localName.equals("xml_api_reply")) {this.in_outertag = true;}
		else if (localName.equals("weather")) {this.in_innertag = true;}
		else if (localName.equals("current_conditions")) {this.in_current  = true;}
		else if (localName.equals("temp_c")) {
			if (in_current) {
				String attrValue = atts.getValue("data");
		        int _temp = Integer.parseInt(attrValue);
		        _xmlDataSet.setTemp(_temp);
			}
		}else if (localName.equals("wind_condition")) {
			if (in_current) {
				String attrValue = atts.getValue("data");
		        String _wind = attrValue;
		        _xmlDataSet.setWind(_wind);
			}
		}
		else if (localName.equals("condition")) {
			// Extract an Attribute        
			if (in_current) {
				String attrValue = atts.getValue("data");
		        String _condition = attrValue;
		        _xmlDataSet.setCondition(_condition);
			}
			}
	 }
	       
	 //** Gets be called on closing tags 
	@Override
	 public void endElement(String namespaceURI, String localName, String qName)throws SAXException {
	         if (localName.equals("xml_api_reply")) {this.in_outertag = false;}
	         else if (localName.equals("weather")) {this.in_innertag = false;}
	         else if (localName.equals("current_conditions")) {this.in_current = false;}
	         else if (localName.equals("condition")) {}
	        }
}

