package newparser;

import newparser.model.KeyBoardTemp;

public class KeyBoardParser {

    public void parseKeyDump(KeyEventIdentifier identifier, KeyBoardTemp temp) {
        String arrayKeyBaoardLines[] = getKeyboardDumpText("[KeyScrap dump start]", "[KeyScrap dump end]").split("\":|\",");
        for (int i = 1; i < arrayKeyBaoardLines.length; i = i + 2) {
            String value = arrayKeyBaoardLines[i];
            String strValues[] = value.split("\\),");
            String key = strValues[0].substring(6 + 3);
            String keyCode = (strValues[1].length() > 6) ? strValues[1].substring(6 + 1) : strValues[1].substring(6);
            String mx = strValues[3].substring(3 + 1);
            Integer my = Integer.parseInt(strValues[4].substring(3 + 1)) + temp.getKeyboardY() + temp.getEdge();
            String width = strValues[5].substring(7 + 1);
            String height = strValues[6].substring(8 + 1);
            identifier.mxPoints.put(key, Integer.parseInt(mx) + temp.getEdge());
            identifier.myPoints.put(key, my);
            identifier.mWidthPoints.put(key, Integer.parseInt(width));
            identifier.mHeightPoints.put(key, Integer.parseInt(height));
            identifier.mkeyCodePoints.put(key, Integer.parseInt(keyCode));
        }
    }

    public String getKeyboardDumpText(String startText, String endText) {
        String dumpString = null;
        String mappingString = null;

        do {
            try {
                String[] adbCommands = {AdbCommandExecutor.adbPath, "shell", "dumpsys", "input_method"};
                AdbCommandExecutor adbCommandExecutor = new AdbCommandExecutor(adbCommands);
                adbCommandExecutor.start();
                while (adbCommandExecutor.isInProcess) ;
                dumpString = adbCommandExecutor.outputText;
               // System.out.println(dumpString);
                if (dumpString != null) {
                    if (dumpString.indexOf(startText) > -1 && dumpString.indexOf(endText) > -1)
                        mappingString = dumpString.substring(dumpString.indexOf(startText), dumpString.indexOf(endText));

                }
                Thread.sleep(1000);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            //System.out.println("dump is in progress");
        } while (dumpString.indexOf(startText) == -1 || dumpString.indexOf(endText) == -1);


        return mappingString;
    }

    public void initKeyboardTemp(){
        String text =  getKeyboardDumpText("[BoardScrap Start]", "[BoardScrap dump end]");
        KeyEventIdentifier.keyBoardTemp.setKeyboardY(Integer.parseInt(text.substring(text.indexOf("PosY")).split(":|,")[1].trim()));
        KeyEventIdentifier.keyBoardTemp.setEdge(Integer.parseInt(text.substring(text.indexOf("VerticalGap")).split(":|,")[1].trim()));
        System.out.println(KeyEventIdentifier.keyBoardTemp.getKeyboardY()+" "+KeyEventIdentifier.keyBoardTemp.getEdge());
    }

}
