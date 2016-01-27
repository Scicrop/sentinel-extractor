# sentinel-extractor
Copernicus Sentinel Satellites data extractor - *"No matter what happens, keep downloading."*

The main idea behind this project is to provide a robust CLI application to collect images (as a background process) from Sentinel-1 and Sentinel-2 sattelites. In other words, a distributed multithreading download manager for Sentinel  data products.

This project explores the resources offered at The Sentinels Scientific Data Hub (https://scihub.copernicus.eu) by In-Orbit Commissioning Review (IOCR) from European Space Agency (ESA).

The source-code is written in Java language. The main logic consists in Web Services consuption through Open Search and Open Data API, both implemented with specific libraries: Apache Abdera for Open Search and Apache Olingo for Open Data. The resulting software is an alternative for downloading Sentinel's data using wget, cUrl or dhusget script.

Main advantages of Sentinel-Extractor software is are: 

 - Command-Line Interface for dowloading through:
  - Open Search queries;
  - Product UUID;
  - Interrupted downloads;
 - Silent-mode for download based on configurarion file (Downloader);
 - Supervisor that can initiate and restart remote downloader instances:
  - Downloader instances communicate with supervisor through UDP sockets;

## How to use ##

First of all, this software must be executed with Oracle Java JRE or OpenJDK JRE version >= 1.7

 - Interactive mode:
   - Use this mode to monitor de entire process of downloads of a given Open Search query.
  ```
  $java -jar se.jar
  ```
   - In this mode the user will be asked to inform:
     - User (based on registration at https://scihub.copernicus.eu/dhus);
     - Password (based on registration at https://scihub.copernicus.eu/dhus);
     - Sentinel satellite (Integer: 1 or 2);
     - Output folder (The place where the download will be stored. For Windows, use this pattern: C:/dir/subdir/);
     - How much time (milliseconds) the downloader should wait for before kill a stalled connection (>= 60000 for <= 50 Mbps connections);
     - How much tries will try the download (Integer: Default is 5);
     - How to download:
       - (1) Open Search Query: 
         - Example: https://scihub.copernicus.eu/dhus/search?q=footprint:%22Intersects(POLYGON((-4.53%2029.85,26.75%2029.85,26.75%2046.80,-4.53%2046.80,-4.53%2029.85)))%22&$filter=substringof(20151221T164815_,Name)
        - (2) Open Data Query (by UUID):
          - Example: 18f7993d-eae1-4f7f-9d81-d7cf19c18378 
        - (3) Resume interrupted downloads
      
 - No-interactive mode:
   - Use this mode to monitor de entire process of downloads of a given Open Search query with pre-defined configurations written in files. You can run this mode with ALL threads and ONE supervisor checking each thread, OR run only one thread. These are the files that you have to write in disk:
     - downloader-file.properties: this file has the attributes of each downloader thread. For each thread it will be one file.
       - Naming Example: downloader-file-br.properties; downloader-file-usa.properties; downloader-file-ru.properties.
       - Properties content example:
>         user=guest
>         password=guest_pass
>         outputfolder=/tmp/
>         sentinel=1 
>         clienturl=https://scihub.copernicus.eu/dhus/search?q=( footprint:"Intersects(POLYGON((-74.24323771090575 -34.81331346157173,-31.2668365052604 -34.81331346157173,-31.2668365052604 5.647318588641241,-74.24323771090575 5.647318588641241,-74.24323771090575 -34.81331346157173)))" ) AND ( beginPosition:[2016-01-25T00:00:00.000Z TO 2016-01-26T23:59:59.999Z] AND endPosition:[2016-01-25T00:00:00.000Z TO 2016-01-26T23:59:59.999Z] ) AND (platformname:Sentinel-1 AND producttype:SLC) 
>         socketport=9001
>         verbose=false
>         log=true
>         logfolder=/tmp/
>         threadcheckersleep=60000
>         downloadtrieslimit=100
         
         - user: based on registration at https://scihub.copernicus.eu/dhus;
         - password: based on registration at https://scihub.copernicus.eu/dhus;
         - sentinel: Sentinel satellite (Integer: 1 or 2);
         - outputfoolder: The place where the download will be stored. For Windows, use this pattern: C:/dir/subdir/;
         - clienturl: the URL...........................
         - socketport: any socketport for udp connections. This port has to be the same in supervisor-file.xml (available below).
         - verbose: true if your thread will display messages about the detailed funcionality, false if not. (default: false)
         - log: true if your thread will display important logs about the downloading, false if not. (default: true)
         - logfolder: the folder where the same log above will be write in a file.
         - threadcheckersleep: how much time (milliseconds) the downloader should wait for before kill a stalled connection (>= 60000 for <= 50 Mbps connections)
         - downloadtrieslimit: the limit of tries to download the files in clienturl.
     - supervisor-file.xml: descript in XML format all threads that the supervisor will check. Below there are an example of this file:
>     <?xml version="1.0"?>
>       <supervisor jarpath="/opt/sentinel-extractor/se.jar" udp_server_port="9001">
>	         <thread prop="/home/user/sentinel-configuration/downloader-file-br.properties"/>
>	         <thread prop="/home/user/sentinel-configuration/downloader-file-usa.properties"/>
>	         <thread prop="/home/user/sentinel-configuration/downloader-file-ru.properties"/>
>       </supervisor>

         - /supervisor/@jarpath: the location of your sentinel-extractor jar
         - /supervisor/@udp_server_port: the number port where the sentinel will receive messages of threads.
         - /supervisor/thread/@prop: the path of each configuration file. Each configuration file will run ONE thread. If there is 5 configurations, the supervisor will run 5 threads.
    - After you have those configuration files in disk, you can choice:
     - Supervisor Mode: ALL threads and ONE supervisor checking each thread
     
     ```$java -jar se.jar s supervisor-file.xml```
     - Downloader Mode: Run only one thread

     ```$java -jar se.jar d downloader-file.properties```
 
