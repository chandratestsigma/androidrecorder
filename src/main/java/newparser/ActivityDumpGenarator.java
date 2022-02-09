package newparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ActivityDumpGenarator {

    public String getDump() throws IOException, InterruptedException {
        String[] commands  = new String[]{AdbCommandExecutor.adbPath, "exec-out" ,"uiautomator", "dump", "/dev/tty"};
        AdbCommandExecutor adbCommandExecutor  = new AdbCommandExecutor(commands);
        adbCommandExecutor.start();
        while(adbCommandExecutor.isInProcess){
           // System.out.println("dump is in progress");
            Thread.sleep(50);
        }
        return adbCommandExecutor.outputText.substring(adbCommandExecutor.outputText.indexOf("<hierarchy"),
                adbCommandExecutor.outputText.indexOf("/hierarchy")+11);
    }

    public String getDump(String activity) throws ImplementationNotFoundException {
        throw new ImplementationNotFoundException("Implementation not provided");
    }
    public String windowSizeDump() throws IOException, InterruptedException {
        String[] commands  = new String[]{AdbCommandExecutor.adbPath, "shell" ,"wm", "size"};
        AdbCommandExecutor adbCommandExecutor  = new AdbCommandExecutor(commands);
        adbCommandExecutor.start();
        while(adbCommandExecutor.isInProcess){
           // System.out.println("dump is in progress");
            Thread.sleep(100);
        }

        return adbCommandExecutor.outputText;
    }
}
