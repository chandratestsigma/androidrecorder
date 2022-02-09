package newparser;

import java.io.*;

public class AdbCommandExecutor extends Thread{
    static String adbPath = "/home/chandra/Desktop/platform-tools/adb";
    static public String filePath = "/home/chandra/Desktop/platform-tools/android.txt";
    Process process = null;
    Boolean sync = false;
    Boolean wirteToFile = false;
    String outputText = "";
    Boolean isInProcess = false;
    Boolean skipReadingOuput = false;
    private void startProces(String commands[]) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        this.isInProcess = true;
        process = builder.start();
    }
    public AdbCommandExecutor(String filePath, String commands[] ) throws IOException {
        this.filePath = filePath;
        this.wirteToFile = true;
        this.isInProcess = true;
        startProces(commands);
    }
    public AdbCommandExecutor(String commands[]) throws IOException {
        this.sync = true;
        startProces(commands);
    }

    public AdbCommandExecutor(String commands[], Boolean skipReadingOuput) throws IOException {
        this.sync = true;
        this.skipReadingOuput = skipReadingOuput;
        startProces(commands);
    }

    @Override
    public void run() {
        InputStream out = process.getInputStream();
        byte[] buffer = new byte[4000];
        if(wirteToFile){
            File file =new File(filePath);
            if(file.exists()){
                file.delete();
            }
        }

        try {
            FileOutputStream fileOutputStream = wirteToFile ? new FileOutputStream(filePath) : null;
             do{
                int no = 0;
                if(this.skipReadingOuput){
                    continue;
                }
                no = out.available();
                if (no > 0) {
                    int n = out.read(buffer, 0, Math.min(no, buffer.length));
                    String data = new String(buffer, 0, n);
                    if(this.wirteToFile)
                        fileOutputStream.write(data.getBytes());
                    else
                        outputText = outputText+data;
                }
            }while (ProcessUtil.isAlive(process));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isInProcess = false;
    }

}
