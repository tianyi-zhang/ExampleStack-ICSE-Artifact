package net.gettrillium.trillium.api;

import net.gettrillium.trillium.api.Configuration.Broadcast;
import net.gettrillium.trillium.api.messageutils.Message;
import net.gettrillium.trillium.api.messageutils.Mood;
import net.gettrillium.trillium.api.messageutils.Pallete;
import net.gettrillium.trillium.runnables.TpsRunnable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern COMMANDBLOCK_REGEX = Pattern.compile("@p", Pattern.LITERAL);
    private static final Pattern COMMA_SPACE = Pattern.compile(", ");

    public static void printCurrentMemory(CommandSender sender) {
        int free = (int) Runtime.getRuntime().freeMemory() / 1000000;
        int max = (int) Runtime.getRuntime().maxMemory() / 1000000;
        int used = max - free;
        int i = (int) ((100L * (long) used) / (long) max);

        new Message(Mood.NEUTRAL, "Max Memory", max + "MB").to(sender);
        new Message(Mood.NEUTRAL, "Used Memory", used + "MB").to(sender);
        new Message(Mood.NEUTRAL, "Free Memory", free + "MB").to(sender);
        new Message(Mood.NEUTRAL, "Used Memory", asciibar(i)).to(sender);
        new Message(Mood.NEUTRAL, "TPS", String.valueOf(TpsRunnable.getTPS())).to(sender);
        new Message(Mood.NEUTRAL, "Lag Rate", asciibar((int) Math.round((1.0D - (TpsRunnable.getTPS() / 20.0D))))).to(sender);
    }

    public static String asciibar(int percent) {
        StringBuilder bar = new StringBuilder();
        bar.append(ChatColor.GRAY);
        bar.append('[');

        for (int i = 0; i < 25; i++) {
            if (i < (percent / 4)) {
                bar.append(ChatColor.AQUA);
                bar.append('#');
            } else {
                bar.append(ChatColor.DARK_GRAY);
                bar.append('-');
            }
        }

        bar.append(ChatColor.GRAY);
        bar.append("]  ");
        bar.append(ChatColor.AQUA);
        bar.append(percent);
        bar.append('%');
        return bar.toString();
    }

    // TODO - unit test this
    public static List<String> centerText(String input) {
        String desaturated = ChatColor.stripColor(input);
        String[] s = stringSplitter(desaturated, 40);
        List<String> centered = new ArrayList<>(s.length);
        for (String slices : s) {
            centered.add(StringUtils.center(slices, 60));
        }
        return centered;
    }

    public static void clearChat(Player p) {
        for (int i = 0; i < 200; i++) {
            p.sendMessage(" ");
        }
    }

    // http://stackoverflow.com/a/12297231/4327834
    // #efficiency
    public static String[] stringSplitter(String s, int interval) {
        if ((s == null) || (s.isEmpty())) {
            return new String[]{};
        }

        if (interval == 0) {
            return new String[]{s};
        }

        int arrayLength = (int) Math.ceil((((double) s.length() / (double) interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }

        result[lastIndex] = s.substring(j);

        return result;
    }

    // TODO - unit test this
    // http://stackoverflow.com/a/3083197/4327834
    public static int endOfStringInt(String input) {
        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(input);
        if (m.find()) {
            return Integer.parseInt(m.group());
        } else {
            return 0;
        }
    }

    /**
     * Compares two version numbers
     *
     * @param version1 first version
     * @param version2 another version
     * @return true if version1 is newer than version2
     */
    public static boolean compareVersion(String version1, String version2) {
        if ((version1 == null) || (version2 == null)) {
            return false;
        }

        String[] a = version1.split("\\.");
        String[] b = version2.split("\\.");

        // for the parts of the version number that both share,
        for (int i = 0; (i < a.length) && (i < b.length); ++i) {
            if (Integer.parseInt(a[i]) > Integer.parseInt(b[i])) {
                return true;
            }
        }

        // return true if the version we're comparing to is longer
        return (a.length > b.length);
    }

    public static int timeToTickConverter(String time) {
        int seconds = 0;

        if (time.contains("s")) {
            seconds = endOfStringInt(time.split("s")[0]);
        }
        if (time.contains("m")) {
            seconds += endOfStringInt(time.split("m")[0]) * 60;
        }
        if (time.contains("h")) {
            seconds += endOfStringInt(time.split("h")[0]) * 3600;
        }
        if (time.contains("d")) {
            seconds += endOfStringInt(time.split("d")[0]) * 86400;
        }

        return seconds * 20;
    }

    public static String arrayToString(String[] array) {
        if ((array == null) || (array.length == 0)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String s : array) {
            sb.append(';');
            sb.append(s);
        }

        return sb.substring(1);
    }

    public static String arrayToReadableString(String[] array) {
        if ((array == null) || (array.length == 0)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (String s : array) {
            sb.append(", ");
            sb.append(s);
        }

        return sb.substring(2);
    }

    public static String[] arrayFromString(String fromString) {
        if (fromString == null) {
            return null;
        }

        return fromString.split(";");
    }

    public static String timeToString(int ticks) {
        int millis = (ticks / 20) * 1000;

        return String.format("%02d:%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays((long) millis),
                TimeUnit.MILLISECONDS.toHours((long) millis) % TimeUnit.DAYS.toHours(1L),
                TimeUnit.MILLISECONDS.toMinutes((long) millis) % TimeUnit.HOURS.toMinutes(1L),
                TimeUnit.MILLISECONDS.toSeconds((long) millis) % TimeUnit.MINUTES.toSeconds(1L));
    }

    public static void broadcastImportantMessage() {
        List<String> list = TrilliumAPI.getInstance().getConfig().getStringList(Broadcast.IMPORTANT_BROADCAST_BROADCAST);
        for (String s : list) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public static void clearInventory(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
    }

    public static List<String> convertFileToBookPages(File book) {
        List<String> pages = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        try {
            for (Object o : FileUtils.readLines(book)) {
                String line = o.toString();
                line = ChatColor.translateAlternateColorCodes('&', line);

                if (line.equals("<-NEXT->")) {
                    pages.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append(line);
                    sb.append('\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pages;
    }

    // TODO - unit test this
    public static String commandBlockify(String command, Player p) {
        if (command == null) {
            return null;
        } else if (p == null) {
            return command;
        }

        return COMMANDBLOCK_REGEX.matcher(command).replaceAll(p.getName());
    }

    // http://stackoverflow.com/a/22947052
    public static <T> List<List<T>> getPages(Collection<T> c, int pageSize) {
        if (c == null) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<>(c);

        if ((pageSize <= 0) || (pageSize > list.size())) {
            pageSize = list.size();
        }

        int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);

        List<List<T>> pages = new ArrayList<>(numPages);

        for (int pageNum = 0; pageNum < numPages; ) {
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        }

        return pages;
    }

    public static String aestheticSlash() {
        return Pallete.MAJOR.getColor() + "" + ChatColor.STRIKETHROUGH + "--------------" + ChatColor.RESET;
    }

    public static String getColorNameFromID(int id) {
        String color = "white";
        switch (id) {
            case 0:
                color = "white";
                break;
            case 1:
                color = "orange";
                break;
            case 2:
                color = "magenta";
                break;
            case 3:
                color = "light_blue";
                break;
            case 4:
                color = "yellow";
                break;
            case 5:
                color = "lime";
                break;
            case 6:
                color = "pink";
                break;
            case 7:
                color = "gray";
                break;
            case 8:
                color = "light_gray";
                break;
            case 9:
                color = "cyan";
                break;
            case 10:
                color = "purple";
                break;
            case 11:
                color = "blue";
                break;
            case 12:
                color = "brown";
                break;
            case 13:
                color = "green";
                break;
            case 14:
                color = "red";
                break;
            case 15:
                color = "black";
                break;
        }
        return color;
    }

}
