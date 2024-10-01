# OzBlue Installation Process

Welcome to OzBlue! This README will guide you through setting up and running our app.

## Table of Contents
- Prerequisites
- Installation
- Setting Up SQL Server
- Database Setup
- Running the App

## Prerequisites
Before you begin, ensure you have met the following requirements:

- You have installed Android Studio (version 4.0 or higher).
- You have installed the Android SDK and set up the necessary environment variables.
- You have a device or emulator running at least Android 5.0 (Lollipop).
- You have access to a SQL Server instance and the necessary permissions to create databases and users.

## Installation

1. Download the ZIP File:

   - Download the ZIP file of the project from the provided link or repository.

2. Extract the ZIP File:

   - Extract the contents of the ZIP file to a directory of your choice.

3. Open the Project in Android Studio:

   - Launch Android Studio.
   - Click on Open an existing Android Studio project.
   - Navigate to the directory where you extracted the ZIP file and select the project folder.

## Setting Up SQL Server

1. On Windows with SSMS

   - Download SQL Server:
     
     - Go to the SQL Server downloads page.
     - Download the SQL Server Developer or SQL Server Express edition.
       
   - Install SQL Server:
     
      - Run the installer and follow the prompts.
      - Choose the installation type (typically "New SQL Server stand-alone installation").
      - Follow the configuration steps, including setting the authentication mode and adding SQL Server administrators.
        
   - Install SQL Server Management Studio (SSMS):
     
      - Download SSMS from the SSMS download page.
      - Run the installer and follow the prompts.
        
   - Connect to SQL Server:
     
      - Open SSMS.
      - In the "Connect to Server" window, enter your server name and authentication details, then click Connect.

2. On Mac with Azure Data Studio
   
   - Download Azure Data Studio:

      - Go to the Azure Data Studio download page.
   
   - Install Azure Data Studio:
     
      - Open the downloaded file and drag Azure Data Studio to your Applications folder.
   
   - Connect to SQL Server:
     
     - Launch Azure Data Studio.
      - Click on New Connection.
      - Enter your server name and authentication details, then click Connect.

## Database Setup

To set up the database for the app, follow these steps:

1. Create the Database and Tables

   - Open your SQL Server management tool
     
     - SQL Server Management Studio (if using PC)
       
     - Azure Data Studio (if using macOS)
       
   - Execute the create.sql file to create the necessary tables in the database named BlueBookDB.

2. Set User Permissions
   - Execute the users.sql file to grant permissions to the user that will access the database for the app.

## Running the App

1. Sync the Project with Gradle Files:

   - Once the project is opened, Android Studio will prompt you to sync the project with Gradle files. Click Sync Now and wait for the process to complete.

2. Set Up an Emulator or Connect a Device:

   - If you are using an emulator, make sure it is running.
   - If you are using a physical device, enable USB debugging on your device and connect it to your computer via USB.

3. Run the App:

   - Click on the Run button (green play icon) in the toolbar.
   - Select the device or emulator you want to run the app on.
   - Wait for the build process to complete and the app to launch on your selected device.
