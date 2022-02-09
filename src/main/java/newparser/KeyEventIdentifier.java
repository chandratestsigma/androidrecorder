package newparser;

import newparser.model.KeyBoardTemp;
import newparser.model.Point;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class KeyEventIdentifier {
    static KeyBoardTemp keyBoardTemp = new KeyBoardTemp();
    Map<String, Integer> mxPoints = new HashMap<>();
    Map<String, Integer> myPoints = new HashMap<>();
    Map<String, Integer> mWidthPoints = new HashMap<>();
    Map<String, Integer> mHeightPoints = new HashMap<>();
    Map<String, Integer> mkeyCodePoints = new HashMap<>();
    public static Boolean  isKeyBoardIsOn() throws IOException, InterruptedException {
        String [] adbCommands = new String[]{AdbCommandExecutor.adbPath, "shell" ,"dumpsys", "input_method","|", "grep", "mInputShown"};
        AdbCommandExecutor adbCommandExecutor = new AdbCommandExecutor(adbCommands);
        adbCommandExecutor.start();
        while (adbCommandExecutor.isInProcess){
            //System.out.println("dump is in progress");
            Thread.sleep(100);
        }
        // System.out.println(xmlDump+xmlDump.substring(xmlDump.indexOf("mInputShown=")+"mInputShown=".length(),xmlDump.indexOf("\n")));
        return "true".equals(adbCommandExecutor.outputText.substring(adbCommandExecutor.outputText.indexOf("mInputShown=")+"mInputShown=".length(),adbCommandExecutor.outputText.indexOf("\n")));
    }
    public  Map.Entry<String,Integer> getKeyCode(Point point){
        Map.Entry keyEntry = null;
        for(Map.Entry entry : mkeyCodePoints.entrySet()) {
            keyBoardTemp.setX(mxPoints.get(entry.getKey()));
            keyBoardTemp.setY(myPoints.get(entry.getKey()));
            keyBoardTemp.setWidth(mWidthPoints.get(entry.getKey()));
            keyBoardTemp.setHeight(mHeightPoints.get(entry.getKey()));
            if(isKeyInsideArea(point, keyBoardTemp)){
                return entry;
            }
        }
        return keyEntry;
    }
    public boolean isKeyInsideArea(Point point, KeyBoardTemp temp) {
        return ((point.getX() >= temp.getX() && (point.getX() <= temp.getX() + temp.getWidth()))
                && (point.getY() >= temp.getY() && (point.getY() <= temp.getY() + temp.getHeight())));
    }

}
