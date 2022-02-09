package newparser;

import newparser.model.Point;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class AndroidRecorder extends Thread {
    static AdbCommandExecutor adbCommandExecutor;
    Boolean skipRecording = false;
    public static void main(String[] args) throws IOException, InterruptedException {
        String[] adbCommands = {AdbCommandExecutor.adbPath, "shell", "getevent", "-l", "-r"};
        adbCommandExecutor = new AdbCommandExecutor(AdbCommandExecutor.filePath, adbCommands);
        adbCommandExecutor.start();
        System.out.println("initializing activity dump");
        new KeyBoardParser().initKeyboardTemp();
        System.out.println("initializing dimentions");
        new ActivityDumpParser().initDimentionsTemp();
        System.out.println("dimentions initailzed");
        Thread.sleep(100);
        new AndroidRecorder().start();
    }

    @Override
    public void run() {
        while (adbCommandExecutor.isInProcess) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(AdbCommandExecutor.filePath));
                record(br);
                if(skipRecording){
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private void sendData(String text) throws IOException {
        System.out.println(text);
        AdbCommandExecutor adbCommandExecutor = new AdbCommandExecutor(new String[]{AdbCommandExecutor.adbPath, "shell", "input" ,"text", text}, true);
        adbCommandExecutor.start();
       // while (adbCommandExecutor.isInProcess);
    }
    private void sendKeyEvent(String keyCode) throws IOException {
        System.out.println(keyCode);
        AdbCommandExecutor adbCommandExecutor = new AdbCommandExecutor(new String[]{AdbCommandExecutor.adbPath, "shell", "input" ,"keyevent", keyCode}, true);
        adbCommandExecutor.start();
        //while (adbCommandExecutor.isInProcess);
    }
    public void record(BufferedReader br) throws Exception {
        Boolean isKeyLayoutIsChanged = true;
        String xmlData = "";
        skipRecording = false;
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                if(skipRecording){
                    adbCommandExecutor.process.destroyForcibly();
                    System.out.println("type start recorder again");
                    String text = sc.nextLine();
                   if(text.equals("start")){
                     AndroidRecorder.main(new String[]{});
                     return;
                   }
                }
                System.out.println("getting activity dump");
                xmlData = new ActivityDumpGenarator().getDump();
                System.out.println("select next element or press e to enter data s to skip recording");
                Point point = getPoint(br);
                //System.out.println("got point x="+point.getX()+", y="+point.getY());
                ActivityDumpParser activityDumpParser = new ActivityDumpParser();
                Document document= activityDumpParser.parseDump(xmlData);

                if (point.getX() > 0 && point.getY() > 0) {
                    if (KeyEventIdentifier.keyBoardTemp.getKeyboardY()<=point.getY()) {
                        Boolean keyboardIsOn = KeyEventIdentifier.isKeyBoardIsOn();
                       // Map.Entry<String, Integer> entry = keyEventIdentifier.getKeyCode(point);
                       if(keyboardIsOn){
                           sendKeyEvent(67+"");
                           System.out.println("enter text to send data");

                           String text = sc.nextLine();
                           if(StringUtils.isEmpty(text)){
                               sendKeyEvent(67+"");
                           } if(text.equals("s")) {
                               skipRecording = true;
                           }else {
                               sendData(text);
                           }
                       }else{
                           new ElementDetector().getMinNodeInsidePoint(
                                   activityDumpParser.getTopNode(document), point, ActivityDumpParser.temp);
                           new XPATHGenarator().generateXpath(document, ActivityDumpParser.temp.getSelectedNode());
                       }


                    } else {
                        new ElementDetector().getMinNodeInsidePoint(
                                activityDumpParser.getTopNode(document), point, ActivityDumpParser.temp);
                        new XPATHGenarator().generateXpath(document, ActivityDumpParser.temp.getSelectedNode());
                    }
                }
                Thread.sleep(200);
            } catch (Exception e) {
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
    private String getOnlyDigits(BufferedReader br){
        String line = "";
        do {

            try {
                line = br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (line==null || line.indexOf(ZEROS) < 0);
        return line.substring(line.indexOf(ZEROS));

    }
    String XSTRING = "ABS_MT_POSITION_X";
    String YSTRING = "ABS_MT_POSITION_Y";
    String ZEROS = "0000";
    private Point getPoint(BufferedReader br) {
        String line = readLine(br, XSTRING, false);
        String px = "";
        String py = "";
        if (line.indexOf(XSTRING) > 0) {
            if (line.substring(12).indexOf(ZEROS) < 0) {
                px =  getOnlyDigits(br);
            }else{
                px = line.substring(line.indexOf(XSTRING) + XSTRING.length() + 4, line.indexOf(XSTRING) + XSTRING.length() + 12);
            }
            px = px.trim();
            line = readLine(br, YSTRING, true);
            // System.out.println(line);
            if (line.substring(12).indexOf(ZEROS) < 0) {
               py =  getOnlyDigits(br);
            }else{
                py = line.substring(line.indexOf(YSTRING) + YSTRING.length() + 4, line.indexOf(YSTRING) + YSTRING.length() + 12);
            }
            py = py.trim();
        }
        System.out.println("point x:"+px+" point y"+py);
        return new Point(((int) Long.parseLong(HexUtil.hexToBinary(px), 2)),
                ((int) Long.parseLong(HexUtil.hexToBinary(py), 2)));
    }
}
