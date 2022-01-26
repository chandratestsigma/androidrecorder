import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AndroidTest {
    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("/home/chandra/Desktop/platform-tools/adb", "shell", "getevent", "-l", "-r");
        builder.redirectErrorStream(true); // so we can ignore the error stream
        Process process = builder.start();
        InputStream out = process.getInputStream();
        OutputStream in = process.getOutputStream();
        File file =new File("/home/chandra/Desktop/platform-tools/android.txt");
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream("/home/chandra/Desktop/platform-tools/android.txt");
        byte[] buffer = new byte[4000];
        new Thread(()->{
            try{
                XMLParser.main();
            }catch (Exception e){}}).start();
        while (isAlive(process)) {
            int no = out.available();
            if (no > 0) {
                int n = out.read(buffer, 0, Math.min(no, buffer.length));
                fileOutputStream.write(new String(buffer, 0, n).getBytes());
                //System.out.println(new String(buffer, 0, n));
            }
        }

        System.out.println(process.exitValue());
    }

}

