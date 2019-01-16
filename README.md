# CSV Deduplication Java Script
A Java script that takes a CSV file and merges rows based on a given column name.

This was initially written to handle duplicate entries in excel documents of contacts.

## Instructions

Despite any references you see to excel, it operates on CSV files.

Input CSV files must have a header row followed by data rows. 

Syntax is as follows:  
java -jar exceldedupe.jar FIELD_NAME FILE_NAME

FIELD_NAME = Name of the column that should be used for the deduplication.
FILE_NAME = Name and/or path to the file.

Example usage:  
java -jar excededupe.jar Name contacts.csv

Output will be placed in the same directory as the .jar file. The output file will be called "new.csv".
