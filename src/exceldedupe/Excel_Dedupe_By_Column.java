// Coded by Travis Kehrli
// Last updated 2/7/2018
// The purpose of this program is to take all text files in the given directory
//   and output them into an Excel spreadsheet such that one column is the name
//   of the file and the second column is the contents of the file.
//  It can be run from inside the target folder or a path to the target folder may be given.
//  Of course there are some limitations, such as max rows/columns and max characters per cell.

package exceldedupe;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.*;

public class Excel_Dedupe_By_Column {
	
	public static void main(String[] args) {
		try {
			//String fileName = "read_ex.csv"; // FILE NAME
	        //File file = new File(fileName);
			
			String targetColumn = args[0];
			String targetFile = args[1];
			
			if(args.length > 2) {
				System.out.println("Usage: exceldedupe [field to dedupe by] [path to file]");
			}
			
			Reader reader = Files.newBufferedReader(Paths.get(targetFile));
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();
            csvReader.close();
            
            Map<String, String[]> finalMap = new HashMap<String, String[]>();;
            
            String[] headers = null;
            int targetColumnIndex = -1;
            boolean onHeader = true;
            
            for(String[] record : records) {
            	if(onHeader) {
            		headers = record;
            		for(int i = 0; i < headers.length; i++) {
            			if(headers[i].equals(targetColumn)) {
            				targetColumnIndex = i;
            				break;
            			}
            		}
            		
            		onHeader = false;
            		//System.out.println(Arrays.toString(headers));
            		//System.out.println(targetColumnIndex);
            	} else {
            		if(targetColumnIndex == -1) {
            			System.out.println("Was not able to find the header you specified.");
            			throw new Exception();
            		}
            		
            		if("".equals(record[targetColumnIndex])) {
            			String newID = "Blank" + record[0];
            			finalMap.put(newID, record);
            			
            		} else {
	            		String[] currentRecord = finalMap.get(record[targetColumnIndex]);
	            		
	            		if(currentRecord == null) {
	            			//System.out.println("NULL");
	            			for(int j = 0; j < record.length; j++) {
	            				record[j] = record[j].replace("\n", " | ").replace("\r", " | ");
	            			}
	            			finalMap.put(record[targetColumnIndex], record);
	            		} else {
	            			if(record[targetColumnIndex] != "") {
		            			// There are conflicting records
		            			//System.out.println("CONFLICTING RECORD");
		            			//System.out.println(Arrays.toString(record));
		            			for(int j = 0; j < currentRecord.length; j++) {
		            				if(j != targetColumnIndex) {
		            					if(!"".equals(record[j])) {
		                					currentRecord[j] = currentRecord[j].replace("\n", " | ").replace("\r", " | ") + " | " + record[j].replace("\n", " | ").replace("\r", " | ");
		            					}
		            				}
		            			}
		            			//System.out.println(Arrays.toString(currentRecord));
		            			finalMap.replace(record[targetColumnIndex], currentRecord);
	            			}
	            		}
	            		
	            		//System.out.println(Map.toString(finalMap));
            		}
            	}
            }
            
            Writer writer = Files.newBufferedWriter(Paths.get("./new.csv"));

            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
	        
            csvWriter.writeNext(headers);
            
            List<String[]> Listofvalues = new ArrayList(finalMap.values());
            
            for (String[] array : Listofvalues) {
            	//System.out.println(Arrays.toString(array).replace("\n", "").replace("\r", ""));
            	//System.out.println(Arrays.toString(array));
            	csvWriter.writeNext(array);
            }
            
            csvWriter.close();

		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
}
