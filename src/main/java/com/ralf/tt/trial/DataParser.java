package com.ralf.tt.trial;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Date;

public class DataParser {
    /**
     * Class to parse data from xml file. It is called out on initial start-up
     * Then it runs every HH:15, delay of 60 minutes, to insert data to DB
     */
    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:file:./data/WeatherData";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "password";
    // Parameters to access H2 database
    public static void DataParser() {
        try {
            /** Creating a constructor of file class and parsing an XML file
            * Defines a factory API that enables applications to obtain a parser that produces*
             * DOM object trees from XML documents.
             */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Creating an object of builder to parse the  xml file.
            DocumentBuilder db = dbf.newDocumentBuilder();
            String weatherPhenomenon = "";
            URL url = new URL("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
            Document doc = db.parse(new InputSource(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1)));
            // Here nodeList contains all the nodes with wmoCode stations
            NodeList nodeList = doc.getElementsByTagName("station");
            // Iterate through all the nodes in NodeList using for loop.
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tElement = (Element) node;
                    String wmoCode = (tElement.getElementsByTagName("wmocode").item(0).getTextContent());
                    if (wmoCode.equals("26038") || wmoCode.equals("26242") || wmoCode.equals("41803")) { // If wmoCode is either Tallinn, Tartu or Pärnu
                        String name = (tElement.getElementsByTagName("name").item(0).getTextContent()); // name parser
                        double airTemperature = Double.parseDouble(tElement.getElementsByTagName("airtemperature").item(0).getTextContent()); // airTemp
                        double windSpeed = Double.parseDouble(tElement.getElementsByTagName("windspeed").item(0).getTextContent()); //windSpeed
                        Timestamp ts = new Timestamp(System.currentTimeMillis()); // timestamp of parsing to sql timestamp
                        try {
                            weatherPhenomenon = (tElement.getElementsByTagName("phenomenon").item(0).getTextContent());
                            // if phenomenon is empty, declare as null
                        } catch (Exception ignored){weatherPhenomenon = null;}

                        insertWeatherData(name, wmoCode, airTemperature, windSpeed, weatherPhenomenon, ts);
                    }
                }
            }
        }
        // This exception block catches all the exceptions raised.
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void insertWeatherData(String name, String wmoCode, double airTemperature, double windSpeed, String weatherPhenomenon, Date ts) throws SQLException {
        /**
         * Method to insert data to weather_data database
         */
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            String sql = "INSERT INTO WEATHER_DATA  " +
                    "VALUES ('" + name + "', '" + wmoCode + "', " + airTemperature + ", " + windSpeed + ", '" + weatherPhenomenon + "', '" + ts + "')";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt!=null)
                    conn.close();
            } catch (SQLException ignored) {
            } // do nothing
            try {
                if (conn!=null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } //end finally try
        } //end try
    }

    public static void main(String[] args) {
        DataParser();
    }

}
