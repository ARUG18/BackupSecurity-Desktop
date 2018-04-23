# Guide for setting up BackupSecurity

## Requirements
+ Need one computer with ubuntu 12.04+ or windows 7+
+ Need one android device with android 5.1 or higher
+ Need minimum quad core arm cpu for android device
+ Both android device and pc should be on same wifi network

## Instructions
+ Download and install [BackupSecurity.apk](https://raw.githubusercontent.com/ARUG18/BackupSecurity/master/app.apk) first
+ Open the app and press prepare config button, this will prepare filestosync.txt
+ Now click the start server button to start the ftp server on android
+ Now compile and run [downloader.java](https://raw.githubusercontent.com/ARUG18/BackupSecurity-Desktop/master/downloader.java), this should start finding your mobile ip
+ Downloading will start as soon as mobile ip is found

## Todo
+ Need to implement similar algo to find pc's local ip from mobile
+ Need the mobile app to read acknowledgement.txt from pc and generate next filestosync.txt accordingly
+ Stats like number of files changed today or total size or redundancy detection can be done
+ Need to make some gui for both desktop and android side applications
+ All the security stuff
