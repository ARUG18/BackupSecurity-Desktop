import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Scanner;

class downloader {
    //this is for byte array when downloading
    private static final int BUFFER_SIZE = 2048;
    //just dummy ftpFilePathPrefix value, main will update it later
    private static String ftpFilePathPrefix = "ftp://192.168.0.1:12345/";
    //this is the path which when appended to above url will give us filestosync url on mobile device
    private static String ftpSyncFilePathSuffix = "BackupSecurity/filestosync.txt";
    //this is the directory in which files will be synced
    private static String savePathPrefix = "downloads/";
    //this is the local path which when appended in front of above url will give filestosync path
    private static String savePathSuffix = "filestosync.txt";
    //this is an error count which is incrimented whenever a file fails to download
    private static int errcount = 0;

    public static void main(String[] args) {
        //find ip of mobile device and store it
        String mob_ip = find_mobile_ip();
        ftpFilePathPrefix = "ftp://" + mob_ip + ":12345/";
        System.out.println("ip of mobile is " + mob_ip);
        //download the filestosync from mobile device
        download_file(ftpSyncFilePathSuffix);
        //now parse each line of filestosync and download them all
        long start = System.currentTimeMillis();
        try {
            //read all content from filestosync.txt
            String content = new Scanner(new File(savePathPrefix + ftpSyncFilePathSuffix)).useDelimiter("\\Z").next();
            //store each line separately at each index of arry
            String[] ar = content.split("\n");
            for (String sti : ar) {
                //trim out the timestamp and separator part
                String str = sti.substring(1, sti.indexOf("#$#$") - 1);
                //replace the " " with "%20" before downloading files
                str = str.replace(" ", "%20");
                //System.out.println(str);
                //lets give another attempt if any download fails
                if (!download_file(str))
                    download_file(str);
            }
            long end = System.currentTimeMillis();
            System.out.println("====================================================");
            System.out.println("Sync complete, errors = " + errcount + " time = " + ((end - start) / 60000) + " minutes");
            System.out.println("====================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized boolean download_file(String st) {
        int count;
        try {
            String ust = ftpFilePathPrefix + st;
            //System.out.println("Downloading from " + ust);
            //open the connection
            URL url = new URL(ust);
            URLConnection conection = url.openConnection();
            conection.connect();
            //replace the "%20" with " " before writing data to file
            savePathSuffix = st.replace("%20", " ");
            //open input stream on the url
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            System.out.println("Trying to get " + savePathPrefix + savePathSuffix);
            File yourFile = new File(savePathPrefix + savePathSuffix);
            //create directories if required
            yourFile.getParentFile().mkdirs();
            //output stream
            OutputStream output = new FileOutputStream(savePathPrefix + savePathSuffix);

            byte data[] = new byte[BUFFER_SIZE];

            long total = 0;

            //append data buffer to output stream till available
            while ((count = input.read(data)) != -1) {
                total += count;
                //writing data to file
                output.write(data, 0, count);
            }

            //flushing output
            output.flush();

            //closing streams
            output.close();
            input.close();
            System.out.println("Done !");
            //return true so that we know it's success
            return true;

        } catch (Exception e) {
            System.out.print("Failed !");
            //it's a failure
            errcount++;
            //print the reason of failure
            e.printStackTrace();
            //return false so that we know a failure
            return false;
        }
    }

    //this method tries to find local ip of this computer, need to fix the 192 prefix hardcode later
    private static String find_own_ip() {
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements(); ) {
                NetworkInterface e = n.nextElement();

                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements(); ) {
                    InetAddress addr = a.nextElement();
                    if (addr.toString().contains(":") || (!addr.toString().contains("192"))) {
                        continue;
                    }
                    return addr.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //this method tries to find local ip of mobile device by recursively checking urls with 4sec timeout
    private static String find_mobile_ip() {
        String own_ip = find_own_ip();
        //since we know first 3 blocks of pc's ipv4 will be same as mobile's ipv4
        String prefix = own_ip.substring(1, own_ip.lastIndexOf('.') + 1);
        boolean down = false;
        int i = 0;
        //as soon as we find that download of filestosync is successful, we know that's our mobile device's ip.
        while ((!down) && (i <= 255)) {
            i++;
            down = check_url("ftp://" + prefix + i + ":12345/BackupSecurity/filestosync.txt");
        }
        return prefix + i;
    }

    /*this method tries to download file from any url into temp/temp.txt, returns true if success,
      this method is based on download_file(String) above, no need to dig into this if you already
      understood download_file(String), we are using this method just for checking urls in a loop. */
    private static synchronized boolean check_url(String st) {
        int count;
        try {
            System.out.println("Trying from " + st);
            URL url = new URL(st);
            URLConnection conection = url.openConnection();
            conection.setConnectTimeout(4000);
            conection.connect();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            File yourFile = new File("temp/" + "temp.txt");
            yourFile.getParentFile().mkdirs();
            OutputStream output = new FileOutputStream(yourFile);
            byte data[] = new byte[BUFFER_SIZE];

            while ((count = input.read(data)) != -1) {
                // writing data to file
                output.write(data, 0, count);
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}
