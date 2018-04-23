import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

class downloader {
    //this is for byte array when downloading
    private static final int BUFFER_SIZE = 2048;
    //just dummy ftpFilePathPrefix value, main will update it later
    private static String ftpFilePathPrefix = "ftp://192.168.0.1:12345/";
    //this is the path which when appended to above url will give us filestosync url on mobile device
    private static String ftpSyncFilePathSuffix = "BackupSecurity/filestosync.txt";
    //this is the directory in which configs will be kept
    private static String configPathPrefix = "configs/";
    //this is the local path which when appended in front of above url will give syncedfiles path
    private static String syncedFilesPathSuffix = "syncedfiles.txt";
    //this is the directory in which files will be synced
    private static String savePathPrefix = "downloads/";
    //this is the local path which when appended in front of above url will give filestosync path
    private static String savePathSuffix = "filestosync.txt";
    //this is an error count which is incrimented whenever a file fails to download
    private static int errcount = 0;
    //record types
    private static int TYPE_FILESTOSYNC = 0;
    private static int TYPE_SYNCEDFILES = 1;

    private static ArrayList<fileRecord> list_new, list_old;

    public static void main(String[] args) {
        //find ip of mobile device and store it
        String mob_ip = find_mobile_ip();
        ftpFilePathPrefix = "ftp://" + mob_ip + ":12345/";
        System.out.print("\n==== ip of mobile is " + mob_ip+" ====");
        //download the filestosync from mobile device
        download_file(ftpSyncFilePathSuffix);
        //now parse each line of filestosync and download them all
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        try {
            //read all content from filestosync.txt and syncedfiles.txt
            File syncedfiles = new File(configPathPrefix + syncedFilesPathSuffix);
            String content_new = null, content_old = null;
            content_new = new Scanner(new File(savePathPrefix + ftpSyncFilePathSuffix)).useDelimiter("\\Z").next();
            if (syncedfiles.exists()) {
                content_old = new Scanner(syncedfiles).useDelimiter("\\Z").next();
            }
            //store each line separately at each index of arry
            String[] ar_new = null, ar_old = null;
            ar_new = content_new.split("\n");
            list_new = new ArrayList<>();
            System.out.print("\n==== parsing filestosync.txt now ====");
            for (String str : ar_new) {
                //lets parse all records and make list of fileRecords out of them
                fileRecord r = new fileRecord();
                r.path = str.substring(1, str.indexOf("#$#$") - 1);
                r.raw = str;
                r.rectype = TYPE_FILESTOSYNC;
                String date_time_stamp = str.substring(str.indexOf("#$#$") + 5, str.length());
                r.year = getInfoFromTimeDateStamp(date_time_stamp, 0);
                r.month = getInfoFromTimeDateStamp(date_time_stamp, 1);
                r.day = getInfoFromTimeDateStamp(date_time_stamp, 2);
                r.hr = getInfoFromTimeDateStamp(date_time_stamp, 3);
                r.min = getInfoFromTimeDateStamp(date_time_stamp, 4);
                /*System.out.print("\nstr = " + str + "\npath = " + r.path + "\nrectype = " + r.rectype + " year = " + r.year +
                        " month = " + r.month + " day = " + r.day + " hr = " + r.hr + " min = " + r.min);*/
                list_new.add(r);
            }
            if (content_old != null) {
                ar_old = content_old.split("\n");
                System.out.print("\n==== parsing syncedfiles.txt now ====");
                list_old = new ArrayList<>();
                for (String str : ar_old) {
                    //lets parse all records and make list of fileRecords out of them
                    fileRecord r = new fileRecord();
                    r.path = str.substring(1, str.indexOf("#$#$") - 1);
                    r.rectype = TYPE_SYNCEDFILES;
                    String date_time_stamp = str.substring(str.indexOf("#$#$") + 5, str.length());
                    r.year = getInfoFromTimeDateStamp(date_time_stamp, 0);
                    r.month = getInfoFromTimeDateStamp(date_time_stamp, 1);
                    r.day = getInfoFromTimeDateStamp(date_time_stamp, 2);
                    r.hr = getInfoFromTimeDateStamp(date_time_stamp, 3);
                    r.min = getInfoFromTimeDateStamp(date_time_stamp, 4);
                    r.success = str.substring(str.length() - 4, str.length()).equals("done");
                    /*System.out.print("\nstr = " + str + "\npath = " + r.path + "\nrectype = " + r.rectype + " year = " + r.year +
                            " month = " + r.month + " day = " + r.day + " hr = " + r.hr + " min = " + r.min + " success = " + r.success);*/
                    list_old.add(r);
                }
            }
            //lets compare old and new records and download accordingly
            for (fileRecord r : list_new) {
                //trim out the timestamp and separator part, just get path
                String path = r.path;
                String rawstring = r.raw;
                boolean need_to_download = checkNeedToDownload(r);
                boolean downloaded = false;
                if (need_to_download) {
                    //replace the " " with "%20" before downloading files
                    path = path.replace(" ", "%20");
                    //Try to download the file
                    downloaded = download_file(path);
                    //Try to download the file again if failed
                    if (!downloaded) {
                        downloaded = download_file(path);
                    }
                    //Giving up now, count this as an error
                    if (!downloaded) {
                        errcount++;
                        System.out.print("\nFailed to downlaod " + r.path);
                    }
                } else {
                    System.out.print("\nNo need to download " + r.path);
                }

                if ((!need_to_download) || (downloaded)) {
                    rawstring += " $#$# done\n";
                } else {
                    rawstring += " $#$# failed\n";
                }
                //write new ar[i] into configs/syncedfiles.txt
                sb.append(rawstring);
            }
            long end = System.currentTimeMillis();
            generate_synced_list(syncedFilesPathSuffix, sb);
            System.out.print("\n====================================================");
            System.out.print("\nSync complete, errors = " + errcount + " time = " + ((end - start) / 60000) + " minutes");
            System.out.print("\n====================================================\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkNeedToDownload(fileRecord r) {
        if (list_old == null || list_old.size() < 1) {
            return true;
        }
        for (fileRecord fr : list_old) {
            if (fr.path.equals(r.path)) {
                //return false only if all date and time params match
                return (fr.min != r.min) || (fr.hr != r.hr) || (fr.day != r.day) ||
                        (fr.month != r.month) || (fr.year != r.year);
            }
        }
        return true;
    }

    //this function parses 16 char time stamps like 2018-04-22 13:31
    private static int getInfoFromTimeDateStamp(String dts, int i) {
        switch (i) {
            case 0: //year
                return Integer.parseInt(dts.substring(0, 4));
            case 1: //month
                return Integer.parseInt(dts.substring(5, 7));
            case 2: //day
                return Integer.parseInt(dts.substring(8, 10));
            case 3: //hr
                return Integer.parseInt(dts.substring(11, 13));
            case 4: //min
                return Integer.parseInt(dts.substring(14, 16));
        }
        return -1;
    }

    private static void generate_synced_list(String s, StringBuilder sb) {
        try {
            File root = new File(configPathPrefix + s);
            root.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(root);
            writer.append(sb.toString());
            writer.flush();
            writer.close();
            System.out.print("\nsyncedfiles.txt has been generated !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized boolean download_file(String st) {
        int count;
        try {
            String ust = ftpFilePathPrefix + st;
            //System.out.print("\nDownloading from " + ust);
            //open the connection
            URL url = new URL(ust);
            URLConnection conection = url.openConnection();
            conection.connect();
            //replace the "%20" with " " before writing data to file
            savePathSuffix = st.replace("%20", " ");
            //open input stream on the url
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            System.out.print("\nTrying to get " + savePathPrefix + savePathSuffix);
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
            System.out.print("\nDone !");
            //return true so that we know it's success
            return true;

        } catch (Exception e) {
            System.out.print("\nFailed !");
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
            System.out.print("\nTrying from " + st);
            URL url = new URL(st);
            URLConnection conection = url.openConnection();
            conection.setConnectTimeout(2000);
            conection.connect();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            File yourFile = new File("configs/" + "temp.txt");
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

class fileRecord
{
    String path = null;
    String raw = null;
    boolean success = false;
    int rectype = -1;
    int year = -1;
    int month = -1;
    int day = -1;
    int hr = -1;
    int min = -1;
}
