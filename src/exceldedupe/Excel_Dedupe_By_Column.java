// Coded by Travis Kehrli
// Last updated 2/7/2018
// The purpose of this program is to take all text files in the given directory
//   and output them into an Excel spreadsheet such that one column is the name
//   of the file and the second column is the contents of the file.
//  It can be run from inside the target folder or a path to the target folder may be given.
//  Of course there are some limitations, such as max rows/columns and max characters per cell.

package exceldedupe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.opencsv.*;

public class Excel_Dedupe_By_Column {
	
	public static void main(String[] args) {
		try {
			//String fileName = "read_ex.csv"; // FILE NAME
	        //File file = new File(fileName);
			
			if(args.length < 2) {
				System.out.println("Usage: java -jar exceldedupe.jar [field to dedupe by] [path to file]");
				System.exit(0);
			}
			
			String targetColumn = args[0];
			String targetFile = args[1];
			
			String fileName = Paths.get(targetFile).getFileName().toString();
			
			FileInputStream input = new FileInputStream(new File(targetFile));
	        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	        decoder.onMalformedInput(CodingErrorAction.IGNORE);
	        InputStreamReader reader = new InputStreamReader(input, decoder);
					
			Reader bufferedReader = new BufferedReader(reader);
            CSVReader csvReader = new CSVReader(bufferedReader);
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
	            			for(int j = 0; j < record.length; j++) {
	            				record[j] = record[j].replace("\n", " | ").replace("\r", " | ");
	            			}
	            			finalMap.put(record[targetColumnIndex], record);
	            		} else {
	            			if(record[targetColumnIndex] != "") {
		            			// There are conflicting records
		            			for(int j = 0; j < currentRecord.length; j++) {
		            				if(j != targetColumnIndex) {
		            					if(!"".equals(record[j])) {
		                					currentRecord[j] = currentRecord[j].replace("\n", " | ").replace("\r", " | ") + " | " + record[j].replace("\n", " | ").replace("\r", " | ");
		            					}
		            				}
		            			}
		            			finalMap.replace(record[targetColumnIndex], currentRecord);
	            			}
	            		}
            		}
            	}
            }
            
            String newFileName = FilenameUtils.removeExtension(fileName) + "_Deduplicated.csv";
            Writer writer = Files.newBufferedWriter(Paths.get(newFileName));

            CSVWriter csvWriter = new CSVWriter(writer,
                    '\t',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
	        
            csvWriter.writeNext(headers);
            
            List<String[]> Listofvalues = new ArrayList(finalMap.values());
            
            for (String[] array : Listofvalues) {
            	csvWriter.writeNext(array);
            }
            
            csvWriter.close();

		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
}
