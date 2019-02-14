public class foo{
	public static List<String> splitConsideringQuotes(String input, char delimiter) {
		// based on http://stackoverflow.com/questions/1757065/splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
		List<String> result = new ArrayList<String>();
		int start = 0;
		boolean inQuotes = false;
		
		for (int current = 0; current < input.length(); current++) {
			
		    if (input.charAt(current) == '\"') {
		    	inQuotes = !inQuotes; // toggle state
		    }
		    
		    boolean atLastChar = (current == input.length() - 1);
		    
		    	
		    if (input.charAt(current) == delimiter && !inQuotes) {
		        result.add(input.substring(start, current).replace("\"", ""));
		        start = current + 1;
		    } else if (atLastChar) {
		    	result.add(input.substring(start).replace("\"", ""));
		    }
		}
		return result;
	}
}