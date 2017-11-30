import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import org.boehn.kmlframework.kml.Document;
import org.boehn.kmlframework.kml.Feature;
import org.boehn.kmlframework.kml.Folder;
import org.boehn.kmlframework.kml.Geometry;
import org.boehn.kmlframework.kml.Icon;
import org.boehn.kmlframework.kml.IconStyle;
import org.boehn.kmlframework.kml.Kml;
import org.boehn.kmlframework.kml.KmlException;
import org.boehn.kmlframework.kml.Placemark;
import org.boehn.kmlframework.kml.Style;
import org.boehn.kmlframework.kml.StyleSelector;
import org.boehn.kmlframework.kml.TimeStamp;

public class WriteWifiKml {

	private static Document doc = new Document();
	private static Folder folder = new Folder();
	public WriteWifiKml() {

	}
	// get an Wifi arraylist and the the name of the futur file. return a new Klm file
	public void createWifiKml(ArrayList<Wifi> array,String newKmlname) {
		initialisation();
		int i = 0;
		while(i<array.size()) {
			inputData(array.get(i));
			i++;
		}
		createFile(newKmlname);
	}
	// write the style of each placemark on our document (color/sign to display on the map at the wifis location )
	public static void writeStyle (String color){
		Style style = new Style();
		style.setId(color);
		IconStyle iconstyle = new IconStyle();
		style.setIconStyle(iconstyle);
		iconstyle.setIconHref("http://maps.google.com/mapfiles/ms/icons/"+color+"-dot.png");
		doc.addStyleSelector(style);

	}
	// we are initialise our kml file by writing the sign and creating a new folder (Wifi networks)
	public void initialisation() {

		writeStyle("red");
		writeStyle("green");
		writeStyle("yellow");   
		folder.setName("Wifi Networks");

	}
	// get a wifi, check her signal and return the StyleUrl that correspond to.
	public static String getstyleUrl (Wifi wifi){
		String red = "#red", green = "#green", yellow = "#yellow";
		int signal = Integer.parseInt(wifi.getSignal());
		if (signal <= -90 ) return red ;
		else if (signal >= -90 && signal <= -70 ) return yellow ;
		else  return green ;
	}

	// get a wifi, return the good timestamp's format YYYY-MM-DDTHH:MM:SSZ
	public static String timestampformat (String temp){
		String timestampf = "",YYYY= "",MM ="",DD="",HH="",mm="",SS="00";
		temp = temp.replaceAll("/", "index");
		temp = temp.replaceAll(" ", "index");
		temp = temp.replaceAll(":", "index");
		String [] date = temp.split("index");
		YYYY = date[2];
		MM = date[1];
		DD = date[0];
		HH = date[3];
		mm = date[4];
		if (date.length==6)  SS = date[5];
		timestampf = YYYY+"-"+MM+"-"+DD+"T"+HH+":"+mm+":"+SS+"Z";
		
		return timestampf;
	}

	// get a wifi and create here placemark 
	// here inputData create our Placemark with the Wifi data and add him to our folder.
	public void inputData(Object o) {
		Wifi wf = new Wifi();
		wf = (Wifi)o ;

		Placemark placemark = new Placemark(wf.getSsid());
		TimeStamp timestamp = new TimeStamp(timestampformat(wf.getTime())) ;
		//timestamp.setWhen(timestampformat(wf.time));
		placemark.setTimePrimitive(timestamp);
		placemark.setDescription("![CDATA[BSSID:"+wf.getMac()+"\nId: "+wf.getId()+"\nFrequency:"+wf.getFreq()+"\ndate: "+wf.getTime() );
		placemark.setStyleUrl(getstyleUrl(wf));
		double Lon,Lat ;
		Lon = Double.parseDouble(wf.getLon());
		Lat = Double.parseDouble(wf.getLat());
		placemark.setLocation(Lon,Lat);
		folder.addFeature(placemark);
	}

	// createFile create a kml file by adding the folder to the doc and the doc to the new Kml file.
	public void createFile(String Filename)  {
		Kml kml = new Kml();	
		doc.addFeature(folder); 
		kml.setFeature(doc);
		try {
			kml.createKml(Filename);
		} catch (KmlException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}