import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import javax.xml.xpath.XPathExpressionException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetKeyBoardLayout {
     Map<String, Integer> mxPoints = new HashMap<>();
     Map<String, Integer> myPoints = new HashMap<>();
     Map<String, Integer> mWidthPoints = new HashMap<>();
     Map<String, Integer> mHeightPoints = new HashMap<>();
     Map<String, Integer> mkeyCodePoints = new HashMap<>();
     Integer keyBoardStartX = 0;
     Integer keyBoardStarty = 0;
     Integer keyBoardEndx = 0;
     Integer keyBoardEndy = 0;
     int keyBoardwidth = 1600;
     int keyboardY = 965;
     int getKeyBoardHeight=500;
     int height = 1600;
     int width = 720;
     int minArea = 750*1080;

    static Node selctedNaode;
    public Map.Entry<String,Integer>  tapOnKeyWithPoint(String px, String py) throws Exception {
        try {
            return samsungReadKeyboard( (int)((Integer.parseInt(hexToBinary(px), 2))),
                    (int) (Integer.parseInt(hexToBinary(py), 2)));
        }catch (Exception e){

        }
       return null;
    }

    private Map.Entry<String,Integer>  samsungReadKeyboard(int x, int y) throws Exception {
        Map.Entry entry = getKeyCode(x,y);
        return entry;
    }
    public  Map.Entry<String,Integer> getKeyCode(int x, int y){
        Map.Entry keyEntry = null;
        for(Map.Entry entry : mkeyCodePoints.entrySet()) {
            if(new Keyboard(mxPoints.get(entry.getKey()), myPoints.get(entry.getKey()),
                    mWidthPoints.get(entry.getKey()), mHeightPoints.get(entry.getKey())).isInside(x, y)){
                return entry;
            }
        };
        return keyEntry;
    }
    public void getMappingPoints() throws Exception {
      String dumpString =  null;
        String mappingString = null;
       do {

               dumpString = getKeyBoardMappingString();
               if (dumpString != null) {
                   if(dumpString.indexOf("[KeyScrap dump start]") >-1 && dumpString.indexOf("[KeyScrap dump end]") > -1)
                       mappingString = dumpString.substring(dumpString.indexOf("[KeyScrap dump start]"), dumpString.indexOf("[KeyScrap dump end]"));

               }
            Thread.sleep(200);
              // System.out.println("checking keyboard dump");
       } while(mappingString == null && dumpString.indexOf("[KeyScrap dump start]") ==-1 && dumpString.indexOf("[KeyScrap dump end]") == -1);
      String keyRect = dumpString.substring(dumpString.indexOf("ValidZoneList"));
      keyRect = keyRect.substring(keyRect.indexOf("[Rect(")+6,keyRect.indexOf(")]"));
        keyBoardStartX = Integer.parseInt(keyRect.split("-")[0].split(",")[0].trim());
        keyBoardStarty = Integer.parseInt(keyRect.split("-")[0].split(",")[1].trim());
        keyBoardEndx = Integer.parseInt(keyRect.split("-")[1].split(",")[0].trim());
        keyBoardEndy = Integer.parseInt(keyRect.split("-")[1].split(",")[1].trim());
       // System.out.println(keyBoardStartX+"  "+keyBoardStarty+"  "+keyBoardEndx+"  "+keyBoardEndy);
        String arrayKeyBaoardLines[] = mappingString.split("\":|\",");
        for (int i=1; i<arrayKeyBaoardLines.length;i=i+2) {
            String value = arrayKeyBaoardLines[i];
            String strValues[] = value.split("\\),");
            String key = strValues[0].substring(6+3);
          //  System.out.println(strValues[1]);
            String keyCode = (strValues[1].length()>6) ? strValues[1].substring(6+1) : strValues[1].substring(6);
            String mx = strValues[3].substring(3+1);
            Integer my = Integer.parseInt(strValues[4].substring(3+1))+965+14;
            String width = strValues[5].substring(7+1);
            String height = strValues[6].substring(8+1);
            mxPoints.put(key, Integer.parseInt(mx)+14);
            myPoints.put(key, my);
            mWidthPoints.put(key, Integer.parseInt(width));
            mHeightPoints.put(key, Integer.parseInt(height));
            mkeyCodePoints.put(key, Integer.parseInt(keyCode));
            //System.out.println(key+"  "+keyCode+"  "+mx+"  "+my+"  "+width+"  "+height);
        }
    }

    public String getKeyBoardMappingString() throws Exception{
        ProcessBuilder builder = new ProcessBuilder("/home/chandra/Downloads/platform-tools_r31.0.3-linux/platform-tools/adb", "shell" ,"dumpsys", "input_method");
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
 //return new String(new FileInputStream("/home/chandra/Desktop/platform-tools/keycode_mapping.txt").readAllBytes(), "UTF-8");
    }

    public String getReadMiXml() throws Exception {
        String dumpString =   getKeyBoardMappingString();
        String mappingString = dumpString.substring(dumpString.indexOf("[ViewDumper"), dumpString.indexOf("[/ViewDumper"));
        String keyRect = mappingString.substring(mappingString.indexOf("View Hierarchy:")+"View Hierarchy:".length());
        System.out.println(keyRect);
        return  keyRect;
    }

    public void processData(String x, String y){
        try {
            System.out.println("x="+x);
            System.out.println("y="+y);
            Document doc = Jsoup.parse(getReadMiXml().replaceAll("\\\\n", ""));

            Long px = Long.parseLong(hexToBinary(x), 2);
            Long py = Long.parseLong(hexToBinary(y), 2);

            System.out.println("x="+px);
            System.out.println("y="+py);
            width =  Integer.parseInt(doc.getElementsByTag("DecorView").attr("w"));
            height = Integer.parseInt(doc.getElementsByTag("DecorView").attr("h"));
            System.out.println(width+"   "+height+"----------------------------"+px+"    "+py);
                        long []p = new long[]{px,py};

            Elements node = doc.getElementsByTag("SoftKeyView");
            Elements node1 = doc.getElementsByTag("AppCompatTextView");
           // Elements node2 = doc.getElementsByTag("SoftKeyView");

            minArea = width*height;
            getLocations(node, p);
            getLocations(node1, p);
          //  getLocations(node2, p);
            for(int i=0; i< selctedNaode.attributes().size();i++)
                System.out.println(selctedNaode.attributes().asList().get(i));

            String bounds= selctedNaode.attributes().get("l");
            String arr[] = bounds.split("\\]\\[");

            int left = Integer.parseInt(selctedNaode.attributes().get("l"));
            int top =  Integer.parseInt(selctedNaode.attributes().get("t"));
            int bottom =  top-Integer.parseInt(selctedNaode.attributes().get("h"));
            int right = left - Integer.parseInt(selctedNaode.attributes().get("w"));

            System.out.println(generateXpath(doc,selctedNaode));
            executeClick((left)+"", (bottom)+"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  Node getLocations( Elements nodes, long[] p){
        int width = 1080;
        int height=2340;

        for (int temp = 0 ; temp <nodes.size(); temp++) {
            Node nNode = nodes.get(temp);
            verifyPointinsideNde(nNode, p);
        }
        return selctedNaode;
    }
    private  void verifySiblings(Node nNode, long p[]){

        Node sibling = nNode.nextSibling();
        while (sibling !=null) {
            verifyPointinsideNde(sibling, p);
            sibling = sibling.nextSibling();

        }
    }
    private  Boolean verifyPointinsideNde(Node nNode, long p[]){
        try {
            int left = Integer.parseInt(nNode.attributes().get("l"));
            int top =  Integer.parseInt(nNode.attributes().get("t"));
            int  bottom= Integer.parseInt(nNode.attributes().get("t"))-Integer.parseInt(nNode.attributes().get("h"));
            int right = left + Integer.parseInt(nNode.attributes().get("w"));
            System.out.println(" -------"+p[0]+"   "+left+"   "+right+"----------------------------"+bottom+"    "+top+"  "+p[1]);
            if((p[0] >= left) && (p[0] < right) && (p[1] <= top) && (p[1] > bottom)){
                    selctedNaode = nNode;
                    return true;
            }
        }catch(Exception e){
           // System.out.println(nNode.toString());
          e.printStackTrace();
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
        ProcessBuilder builder = new ProcessBuilder("/home/chandra/Downloads/platform-tools_r31.0.3-linux/platform-tools/adb", "exec-out" ,"uiautomator", "dump", "/dev/tty");
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
        ProcessBuilder builder = new ProcessBuilder("/home/chandra/Downloads/platform-tools_r31.0.3-linux/platform-tools/adb", "shell" ,"input", "tap", x, y);
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

    private static boolean validateXpath(Document doc, String xpath) throws XPathExpressionException {
       return Xsoup.compile(xpath).evaluate(doc).getElements().size() ==1;
    }

    private static String generateXpath(Document doc, Node node) {
        List<String> attributes = List.of( "id","l", "t", "w", "h");
        String xpath ="//"+node.nodeName();
        for(int i=0; i<node.attributes().asList().size();i++){
            try {

                xpath = xpath + "[@"+node.attributes().asList().get(i).getKey()+"="+node.attributes().asList().get(i).getValue()+"]";
                if(validateXpath(doc, xpath)){
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return xpath;
    }
    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }
}
