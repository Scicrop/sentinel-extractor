# sentinel-extractor
Copernicus Sentinel Satellites data extractor - *"No matter what happens, keep downloading."*

The main idea behind this project is to provide a robust CLI application to collect images (as a background process) from Sentinel-1 and Sentinel-2 sattelites. In other words, a distributed multithreading download manager for Sentinel  data products.

This project explores the resources offered at The Sentinels Scientific Data Hub (https://scihub.copernicus.eu) by In-Orbit Commissioning Review (IOCR) from European Space Agency (ESA).

The source-code is written in Java language. The main logic consists in Web Services consuption through Open Search and Open Data API, both implemented with specific libraries: Apache Abdera for Open Search and Apache Olingo for Open Data. The resulting software is an alternative for downloading Sentinel's data using wget, cUrl or dhusget script.

Main advantages of Sentinel-Extractor software is are: 

 - Command-Line Interface for dowloading through:
  - Open Search queires;
  - Product UUID;
  - Interrupted downloads;
 - Silent-mode for download based on configurarion file;
 - Supervisor that can initiate and restart remote downloader instances:
  - Downloader instances communicate with supervisor through UDP sockets;
