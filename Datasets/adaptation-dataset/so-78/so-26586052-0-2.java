public class foo {
    public static String toDuration(long duration) {

        StringBuffer res = new StringBuffer();
        for(int i=0;i< Lists.times.size(); i++) {
            Long current = Lists.times.get(i);
            long temp = duration/current;
            if(temp>0) {
                res.append(temp).append(" ").append( Lists.timesString.get(i) ).append(temp > 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if("".equals(res.toString()))
            return "0 second ago";
        else
            return res.toString();
    }
}