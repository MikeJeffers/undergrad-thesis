package thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ThesisFingerIDExporter {

	static Vector<String[]> lines = new Vector<String[]>();

	static String[] rawlines = null;
	static PrintWriter out = null;
	public ThesisFingerIDExporter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			 rawlines = readLines(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String s : rawlines){
			lines.add(s.split(","));
			//System.out.println(s);
		}
		
		
		
		try {
			FileWriter outFile = new FileWriter(args[1]);
			out = new PrintWriter(outFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String[] s : lines){
			out.print(s[1]+","+s[7].charAt(1));
			if(Integer.parseInt(s[3])==1 )
				out.println(",DOWN");
			else
				out.println(",UP");
			out.print(s[2]+","+s[8].charAt(1));
			if(Integer.parseInt(s[4])==1 )
				out.println(",DOWN");
			else
				out.println(",UP");
			System.out.println(s);
		}
		
		out.flush();
		out.close();
		

	}

	 public static String[] readLines(String filename) throws IOException {
	        FileReader fileReader = new FileReader(filename);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        List<String> lines = new ArrayList<String>();
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            lines.add(line);
	        }
	        bufferedReader.close();
	        return lines.toArray(new String[lines.size()]);
	    }

}
