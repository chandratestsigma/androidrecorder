import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XMLParser {
    static int minArea = 1600*720;
    static  Node selctedNaode;
    static int width = 720;
    static int height=1600;
    static String adbPath = "/home/chandra/Desktop/platform-tools";
    static  String deviceName = "192.168.0.193:34519";

    public static void main() throws Exception {
        parseXML();
    }
    public static void parseXML() throws Exception {
        BufferedReader br =new BufferedReader(new FileReader("/home/chandra/Desktop/platform-tools/android.txt"));
        String xmlData ="";
        while(true) {
            try {
                String px = "";
                String py = "";
                xmlData = getDump();
                GetKeyBoardLayout keyBoardLayout = new GetKeyBoardLayout();
                keyBoardLayout.getMappingPoints();
                Boolean keboardIsOn = getKeyBoardOn().equals("true");
                System.out.println("select next element");
                String line = readLine(br,"ABS_MT_POSITION_X", false);
               // System.out.println(line);
                if(line.indexOf("ABS_MT_POSITION_X") > 0) {
                    px =  line.substring(line.indexOf("ABS_MT_POSITION_X")+"ABS_MT_POSITION_X".length()+4, line.indexOf("ABS_MT_POSITION_X")+"ABS_MT_POSITION_X".length()+12);
                    line = readLine(br,"ABS_MT_POSITION_Y", true);
                   // System.out.println(line);
                    if(line.indexOf("ABS_MT_POSITION_Y") > 0) {
                        py =  line.substring(line.indexOf("ABS_MT_POSITION_Y")+"ABS_MT_POSITION_Y".length()+4, line.indexOf("ABS_MT_POSITION_Y")+"ABS_MT_POSITION_Y".length()+12);
                    }
                }

                if(px.length()>0 && py.length()>0){

                    if(keboardIsOn){
                        Map.Entry<String,Integer>  entry = keyBoardLayout.tapOnKeyWithPoint(px,py);
                        if(entry!=null){
                            System.out.println(entry.getKey());
                        }else {
                            try {
                                processData(px, py, xmlData.substring(xmlData.indexOf("<hierarchy"), xmlData.indexOf("/hierarchy")+11));
                            }catch (Exception e){
                            }

                        }

                    }else{
                        try {
                            processData(px, py, xmlData.substring(xmlData.indexOf("<hierarchy"), xmlData.indexOf("/hierarchy")+11));
                        }catch (Exception e){
                        }
                    }
                }
                Thread.sleep(200);
            }catch (Exception e){
                //System.err.println(e.getMessage());
                e.printStackTrace();
            }


        }
    }
    private static String readLine(BufferedReader br, String str, Boolean immedeate){
        String line = null;
        Boolean isCompleted = false;
        try {

           do{

                try {
                    String l =  br.readLine();
                     line = l != null ? line+l : "";
                     if(line.length()>0)
                    Integer.parseInt(line.substring(line.length()-2));
                    isCompleted = true;
                }catch (Exception e){
                    isCompleted = true;
                }

            } while(StringUtils.isEmpty(line)  || !isCompleted || (line.indexOf(str) < 0 && !immedeate));
        }catch (Exception e){

        }
       return line;
    }
    public static void processData(String x, String y, String xmlText) throws Exception {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse( new ByteArrayInputStream(xmlText.getBytes()));
            doc.getDocumentElement().normalize();
           // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("hierarchy");
            Long px = Long.parseLong(hexToBinary(x), 2);
            Long py = Long.parseLong(hexToBinary(y), 2);
            long []p = new long[]{px,py};
            Node nNode = nList.item(0);
            selctedNaode=nNode;
           // System.out.println("\nCurrent Element :" + nNode.getNodeName());
            Node node = getLocations(nNode.getFirstChild(), p);
           // for(int i=0; i< selctedNaode.getAttributes().getLength();i++)
               // System.out.println(selctedNaode.getAttributes().item(i));

            String bounds= selctedNaode.getAttributes().getNamedItem("bounds").getNodeValue();
            String arr[] = bounds.split("\\]\\[");

            int left = Integer.parseInt(arr[0].split(",")[0].substring(1));
            int bottom = Integer.parseInt(arr[0].split(",")[1]);
            int right = Integer.parseInt(arr[1].split(",")[0]);
            int top = Integer.parseInt(arr[1].split(",")[1].substring(0,arr[1].split(",")[1].length()-1));
            System.out.println(generateXpath(doc,selctedNaode));
           // executeClick((left)+"", (bottom)+"");

    }

    public static  Node getLocations( Node node, long[] p){
        NodeList childNodes = node.getChildNodes();
        for (int temp = 0; temp <childNodes.getLength(); temp++) {
            Node nNode = childNodes.item(temp);
            verifyPointinsideNde(nNode, p);
            verifySiblings(nNode, p);
            getLocations( nNode, p);
        }
        return selctedNaode;
    }
    private static void verifySiblings(Node nNode, long p[]){

        Node sibling = nNode.getNextSibling();
        while (sibling !=null) {
            verifyPointinsideNde(sibling, p);
            getLocations( sibling, p);
            sibling = sibling.getNextSibling();

        }
    }
    private static Boolean verifyPointinsideNde(Node nNode, long p[]){
        String bounds= nNode.getAttributes().getNamedItem("bounds").getNodeValue();
        String arr[] = bounds.split("\\]\\[");

        int left = Integer.parseInt(arr[0].split(",")[0].substring(1));
        int bottom = Integer.parseInt(arr[0].split(",")[1]);
        int right = Integer.parseInt(arr[1].split(",")[0]);
        int top = Integer.parseInt(arr[1].split(",")[1].substring(0,arr[1].split(",")[1].length()-1));

        if((p[0] >= left) && (p[0] < right) && (p[1] <= top) && (p[1] > bottom)){
            //System.out.println("bounds======>"+bounds);
            int area = (width-(right-left)) * (height-(top-bottom));
            boolean hasMinorChilds=false;
            for( int i=0 ; i< nNode.getChildNodes().getLength();i++){
                boolean hasMinor = verifyPointinsideNde(nNode.getChildNodes().item(i), p);
                if(hasMinor){
                    hasMinorChilds = true;
                    break;
                }
            }
            if((area<minArea) && (area>0) && !nNode.hasChildNodes() && !hasMinorChilds){
                selctedNaode = nNode;
                minArea =area;
                return true;
            }
        }
        return false;
    }
    static String hexToBinary(String hex)
    {
        String binary = "";
        hex = hex.toUpperCase();
        HashMap<Character, String> hashMap
                = new HashMap<Character, String>();
        hashMap.put('0', "0000");
        hashMap.put('1', "0001");
        hashMap.put('2', "0010");
        hashMap.put('3', "0011");
        hashMap.put('4', "0100");
        hashMap.put('5', "0101");
        hashMap.put('6', "0110");
        hashMap.put('7', "0111");
        hashMap.put('8', "1000");
        hashMap.put('9', "1001");
        hashMap.put('A', "1010");
        hashMap.put('B', "1011");
        hashMap.put('C', "1100");
        hashMap.put('D', "1101");
        hashMap.put('E', "1110");
        hashMap.put('F', "1111");

        int i;
        char ch;
        for (i = 0; i < hex.length(); i++) {
            ch = hex.charAt(i);
            if (hashMap.containsKey(ch))
                binary += hashMap.get(ch);
            else {
                binary = "Invalid Hexadecimal String";
                return binary;
            }
        }
        return binary;
    }

    public static String getDump() throws Exception{
        ProcessBuilder builder = new ProcessBuilder(adbPath+"/adb", "exec-out" ,"uiautomator", "dump", "/dev/tty");
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        String xmlDump = "";
        byte[] buffer = new byte[4000];
        while(isAlive(process)){
            int no = out.available();
            while (no > 0) {

                int n = out.read(buffer, 0, Math.min(no, buffer.length));

                xmlDump = xmlDump+new String(buffer, 0, n);
                no =no-Math.min(no, buffer.length);
            }
        }
        process.exitValue();
        return xmlDump;
    }

    public static String launchApp() throws Exception{
        ProcessBuilder builder = new ProcessBuilder(adbPath+"/adb", "shell", "monkey", "-p", "com.twitter.android.lite", "-c", "android.intent.category.LAUNCHER 1");
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        String xmlDump = "";
        byte[] buffer = new byte[4000];
        while(isAlive(process)){
            int no = out.available();
            while (no > 0) {

                int n = out.read(buffer, 0, Math.min(no, buffer.length));

                xmlDump = xmlDump+new String(buffer, 0, n);
                no =no-Math.min(no, buffer.length);
            }
        }
        process.exitValue();
        return xmlDump;
    }

    public static String executeClick(String x, String y) throws Exception{
        ProcessBuilder builder = new ProcessBuilder(adbPath+"/adb", "shell" ,"input", "tap", x, y);
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        String xmlDump = "";
        byte[] buffer = new byte[10000];
        while(isAlive(process)){
            int no = out.available();
            if (no > 0) {
                int n = out.read(buffer, 0, Math.min(no, buffer.length));
                xmlDump = xmlDump+new String(buffer, 0, n);
            }
        }

        return xmlDump;
    }
    public static String pressKey(Integer x, Integer y) throws Exception{
        ProcessBuilder builder = new ProcessBuilder(adbPath+"/adb", "shell" ,"input", "tap", x+"", y+"");
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        String xmlDump = "";
        byte[] buffer = new byte[10000];
        while(isAlive(process)){
            int no = out.available();
            if (no > 0) {
                int n = out.read(buffer, 0, Math.min(no, buffer.length));
                xmlDump = xmlDump+new String(buffer, 0, n);
            }
        }
        System.out.println("pressed key---------->"+x+"  "+y);
        return xmlDump;
    }
    public static String pressKey(String code) throws Exception{
        ProcessBuilder builder = new ProcessBuilder(adbPath+"/adb", "shell" ,"input", "text",code);
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        String xmlDump = "";
        byte[] buffer = new byte[10000];
        while(isAlive(process)){
            int no = out.available();
            if (no > 0) {
                int n = out.read(buffer, 0, Math.min(no, buffer.length));
                xmlDump = xmlDump+new String(buffer, 0, n);
            }
        }
        System.out.println("pressed key---------->"+code);
        return xmlDump;
    }
    public static String getKeyBoardOn() throws Exception{
        ProcessBuilder builder = new ProcessBuilder(adbPath+"/adb", "shell" ,"dumpsys", "input_method","|", "grep", "mInputShown");
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        String xmlDump = "";
        byte[] buffer = new byte[10000];
        while(isAlive(process)){
            int no = out.available();
            if (no > 0) {
                int n = out.read(buffer, 0, Math.min(no, buffer.length));
                xmlDump = xmlDump+new String(buffer, 0, n);
            }
        }
       // System.out.println(xmlDump+xmlDump.substring(xmlDump.indexOf("mInputShown=")+"mInputShown=".length(),xmlDump.indexOf("\n")));
        return xmlDump.substring(xmlDump.indexOf("mInputShown=")+"mInputShown=".length(),xmlDump.indexOf("\n"));
    }
    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    private static boolean validateXpath(Document doc, String xpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(xpath, doc.getDocumentElement(), XPathConstants.NODESET);
        return nodes.getLength() == 1;
    }

    private static String generateXpath(Document doc, Node node) {
        List<String> attributes = List.of("class", "resource-id", "text",  "checked", "enabled", "package", "password");
        String xpath ="//node";
        for(int i=0; i<attributes.size();i++){
            try {
                xpath = xpath + "[@"+node.getAttributes().getNamedItem(attributes.get(i))+"]";
                if(validateXpath(doc, xpath)){
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return xpath;
    }
}