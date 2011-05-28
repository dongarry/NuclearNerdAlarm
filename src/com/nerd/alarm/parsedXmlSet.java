package com.nerd.alarm;

/* Credit: 
*  http://www.anddev.org/parsing_xml_from_the_net_-_using_the_saxparser-t353.html
*/

public class ParsedXmlSet {
	 
	        private String wind = null;
	        private String condition = null;
	        private int temp = 0;
	        
	        public String getWind() {return wind;}
	        public String getCondition(){return condition;}
	        public int getTemp() {return temp;}
	        
	        public void setWind(String Wind) {this.wind = Wind;}
	        public void setCondition(String _condition) {this.condition = _condition;}
	   	 	public void setTemp(int Temp) {this.temp= Temp;}
	       
	        public String toString(){
	               return this.condition  + ", " + this.wind + ", " + this.temp + " ";}
	        
	}

