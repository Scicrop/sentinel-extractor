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
   - Downloader:
   ```
   $java -jar se.jar d downloader-file.properties
   ```
   - Supervisor:
   ```
   $java -jar se.jar s supervisor-file.xml
   ```
 
