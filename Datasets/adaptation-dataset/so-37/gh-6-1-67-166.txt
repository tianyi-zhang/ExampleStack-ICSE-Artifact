package com.prezi.logbox.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    private static Pattern baseNamePattern = null;

    public static String baseName(String fileName) throws Exception {
        if ( baseNamePattern == null){
            baseNamePattern = Pattern.compile(".*/([^\\./]+)((\\.)([^\\./]+))?");
        }

        Matcher m = baseNamePattern.matcher(fileName);
        if (!m.matches()){
            throw new Exception(String.format("Can't calculate basename for file: %s, pattern: %s", fileName, baseNamePattern));
        }
        /*
        // delete everything after the last _
        int position = 0;
        int last_userscore = m.group(1).length();
        System.out.println("#####    " + m.group(1));
        while (position != -1) {
            position = m.group(1).indexOf("_", position + 1);
            if (position != -1) {
                last_userscore = position;
            }
            System.out.println(position);
        }
        System.out.println("#####    " +m.group(1).substring(0, last_userscore));

        return m.group(1).substring(0, last_userscore);
        */
        return m.group(1);
    }


    private static Pattern protocolPattern = null;

    public static Protocol protocolFromURI(String URI) throws Exception {
        if ( protocolPattern == null){
            protocolPattern = Pattern.compile("([^:]+)://.*");
        }

        Matcher m = protocolPattern.matcher(URI);
        if (!m.matches()){
            return null;
        }

        String protocolStr = m.group(1);
        if (protocolStr.equals("s3") || protocolStr.equals("s3n")){
            return Protocol.S3;
        }
        else if (protocolStr.equals("hdfs")){
            return Protocol.HDFS;
        }
        else if (protocolStr.equals("file")){
            return Protocol.LOCAL_FILE;
        }
        else {
            throw new Exception(String.format("Unknown protocol: %s", protocolStr));
        }

    }

    /**
     * Converts a standard POSIX Shell globbing pattern into a regular expression
     * pattern. The result can be used with the standard {@link java.util.regex} API to
     * recognize strings which match the glob pattern.
     * <p/>
     * See also, the POSIX Shell language:
     * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01
     * Found on http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
     * @param pattern A glob pattern.
     * @return A regex pattern to recognize the given glob pattern.
     */

    public static final String globToRegex(String pattern) {
        StringBuilder sb = new StringBuilder(pattern.length());
        int inGroup = 0;
        int inClass = 0;
        int firstIndexInClass = -1;
        char[] arr = pattern.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            switch (ch) {
                case '\\':
                    if (++i >= arr.length) {
                        sb.append('\\');
                    } else {
                        char next = arr[i];
                        switch (next) {
                            case ',':
                                // escape not needed
                                break;
                            case 'Q':
                            case 'E':
                                // extra escape needed
                                sb.append('\\');
                            default:
                                sb.append('\\');
                        }
                        sb.append(next);
                    }
                    break;
                case '*':
                    if (inClass == 0)
                        sb.append(".*");
                    else
                        sb.append('*');
                    break;
                case '?':
                    if (inClass == 0)
                        sb.append('.');
                    else
                        sb.append('?');
                    break;
                case '[':
                    inClass++;
                    firstIndexInClass = i+1;
                    sb.append('[');
                    break;
                case ']':
                    inClass--;
                    sb.append(']');
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
                        sb.append('\\');
                    sb.append(ch);
                    break;
                case '!':
                    if (firstIndexInClass == i)
                        sb.append('^');
                    else
                        sb.append('!');
                    break;
                case '{':
                    inGroup++;
                    sb.append('(');
                    break;
                case '}':
                    inGroup--;
                    sb.append(')');
                    break;
                case ',':
                    if (inGroup > 0)
                        sb.append('|');
                    else
                        sb.append(',');
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
}

