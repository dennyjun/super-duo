# Introduction
Super-duo takes two functional applications, Alexandria and Football Scores (base code provided by Udacity), and takes it to a production-ready state by finding and handling error cases, adding accessibility features, allowing for localization, adding a widget and adding a library.

# Alexandria
This is a book list and barcode scanner application.

# Football Scores
This application tracks current and future football (soccer) matches.

# Configuration
An API key for Football Scores is NOT required but HIGHLY RECOMMENDED as the number of requests the application makes will be set to 50 requests a day in comparison to 50 requests a minute with the API key. You can get a free API key from: http://api.football-data.org/register. Replace the following variable in the strings.xml file with the API key:
     
       <string name="api_key"></string>
