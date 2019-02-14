public class foo{
    public static String capitalizeEveryWord(String source, List<Delimiter> delimiters, Locale locale) {
        char[] chars; 

        if (delimiters == null || delimiters.size() == 0)
            delimiters = getDefaultDelimiters();                

        // If Locale specified, i18n toLowerCase is executed, to handle specific behaviors (eg. Turkish dotted and dotless 'i')
        if (locale!=null)
            chars = source.toLowerCase(locale).toCharArray();
        else 
            chars = source.toLowerCase().toCharArray();

        // First charachter ALWAYS capitalized, if it is a Letter.
        if (chars.length>0 && Character.isLetter(chars[0]) && !isSurrogate(chars[0])){
            chars[0] = Character.toUpperCase(chars[0]);
        }

        for (int i = 0; i < chars.length; i++) {
            if (!isSurrogate(chars[i]) && !Character.isLetter(chars[i])) {
                // Current char is not a Letter; gonna check if it is a delimitrer.
                for (Delimiter delimiter : delimiters){
                    if (delimiter.getDelimiter()==chars[i]){
                        // Delimiter found, applying rules...                       
                        if (delimiter.capitalizeBefore() && i>0 
                            && Character.isLetter(chars[i-1]) && !isSurrogate(chars[i-1]))
                        {   // previous character is a Letter and I have to capitalize it
                            chars[i-1] = Character.toUpperCase(chars[i-1]);
                        }
                        if (delimiter.capitalizeAfter() && i<chars.length-1 
                            && Character.isLetter(chars[i+1]) && !isSurrogate(chars[i+1]))
                        {   // next character is a Letter and I have to capitalize it
                            chars[i+1] = Character.toUpperCase(chars[i+1]);
                        }
                        break;
                    }
                } 
            }
        }
        return String.valueOf(chars);
    }
}