public class foo{
    /*
     * Thanks to stack overflow for this code snippet
     * http://stackoverflow.com/questions/959731/how-to-replace-a-set-of-tokens-in-a-java-string
     */
    private static String replaceTokens(String template, Map<String, String> replacements) {
    	Pattern pattern = Pattern.compile("_(.+?)_");
    	Matcher matcher = pattern.matcher(template);
    	
    	StringBuffer buffer = new StringBuffer();
    	while(matcher.find()) {
    		String replacement = replacements.get(matcher.group(1));
    		
    		if(replacement != null) {
    			matcher.appendReplacement(buffer, "");
    			buffer.append(replacement);
    		}
    	}
    	matcher.appendTail(buffer);
    	return buffer.toString();
    }
}