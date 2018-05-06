# Guide for setting up BackupSecurity

## Requirements
+ Need one computer with ubuntu 12.04+ or windows 7+
+ Need one android device with android 5.1 or higher
+ Need minimum quad core arm cpu for android device
+ Both android device and pc should be on same wifi network

## Instructions (for users)
+ Download and install [BackupSecurity.apk](https://github.com/ARUG18/BackupSecurity/raw/master/app/release/app-release.apk) on your android device first
+ Open the app and configure settings as you wish
+ Now run [BackupSecurity.jar](https://raw.githubusercontent.com/ARUG18/BackupSecurity-Desktop/master/BackupSecurity.jar), this should start backup service
+ First backup could take long time

## Instructions (for developers)
+ Get source for android app [here](https://github.com/ARUG18/BackupSecurity)
+ Get source for desktop side program [here](https://github.com/ARUG18/BackupSecurity-Desktop)
+ Compile android app with android studio and install it
+ To compile desktop side program, first compile classes, run >> javac *.java
+ Then to make executable jar, run >> jar cfm BackupSecurity.jar manifest.txt *.class

## Todo list
+ Need to add backup time interval options in desktop side program
+ Need to turn off ftp server on mobile when it's not needed
+ Something more for improving security functionality
