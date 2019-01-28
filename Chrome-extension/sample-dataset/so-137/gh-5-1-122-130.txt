package jk_5.nailed.launch;

import net.minecraft.launchwrapper.Launch;

import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public final class ServerMain {

    private static final String MINECRAFT_SERVER_LOCAL = "minecraft_server.1.8.jar";
    private static final String MINECRAFT_SERVER_REMOTE = "https://s3.amazonaws.com/Minecraft.Download/versions/1.8/minecraft_server.1.8.jar";
    private static final String LAUNCHWRAPPER_LOCAL = "launchwrapper-1.11.jar";
    private static final String LAUNCHWRAPPER_REMOTE = "https://libraries.minecraft.net/net/minecraft/launchwrapper/1.11/launchwrapper-1.11.jar";
    private static final String DEOBF_SRG_LOCAL = "deobf.srg.gz";
    private static final String DEOBF_SRG_REMOTE = "https://github.com/nailed/nailed/raw/f48cb982537670e3d661fa86cc1d6de44315a053/config/joined.srg";
    private static final String DEOBF_SRG_HASH = "93f72f87b5505dcbf1ce1c0f5b70f4fa";
    // From http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static void main(String[] args) throws Exception {
        if(!checkMinecraft()){
            return;
        }

        Launch.main(join(args,
                "--tweakClass", "jk_5.nailed.launch.ServerTweaker"
        ));
    }

    private static String[] join(String[] args, String... prefix) {
        String[] result = new String[prefix.length + args.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(args, 0, result, prefix.length, args.length);
        return result;
    }

    private static boolean checkMinecraft() throws Exception {
        Path bin = Paths.get("bin");
        if (Files.notExists(bin)) {
            Files.createDirectories(bin);
        }

        // First check if Minecraft is already downloaded :D
        Path path = bin.resolve(MINECRAFT_SERVER_LOCAL);
        // Download the server first
        if (Files.notExists(path) && !downloadVerified(MINECRAFT_SERVER_REMOTE, path)) {
            return false;
        }

        path = bin.resolve(LAUNCHWRAPPER_LOCAL);
        if (Files.notExists(path) && !downloadVerified(LAUNCHWRAPPER_REMOTE, path)) {
            return false;
        }

        path = bin.resolve(DEOBF_SRG_LOCAL);
        return Files.exists(path) || downloadVerified(DEOBF_SRG_REMOTE, path, DEOBF_SRG_HASH);
    }

    private static boolean downloadVerified(String remote, Path path) throws Exception {
        return downloadVerified(remote, path, null);
    }

    private static boolean downloadVerified(String remote, Path path, String expected) throws Exception {
        String name = path.getFileName().toString();
        boolean gzip = name.endsWith(".gz");
        URL url = new URL(remote);

        System.out.println("Downloading " + name + "... This can take a while.");
        System.out.println(url);
        URLConnection con = url.openConnection();
        if (gzip) {
            con.addRequestProperty("Accept-Encoding", "gzip");
        }

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        try (ReadableByteChannel source = Channels.newChannel(new DigestInputStream(con.getInputStream(), md5));
             FileChannel out = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            out.transferFrom(source, 0, Long.MAX_VALUE);
        }

        if (expected == null) {
            expected = getETag(con);
        }
        if (!expected.isEmpty()) {
            String hash = toHexString(md5.digest());
            if (hash.equals(expected)) {
                System.out.println("Successfully downloaded " + name + " and verified checksum!");
            } else {
                Files.delete(path);
                System.err.println("Failed to download " + name + " (failed checksum verification).");
                System.err.println("Please try again later.");
                return false;
            }
        }

        return true;
    }

    private static String getETag(URLConnection con) {
        String hash = con.getHeaderField("ETag");
        if (hash == null || hash.isEmpty()) {
            return "";
        }

        if (hash.startsWith("\"") && hash.endsWith("\"")) {
            hash = hash.substring(1, hash.length() - 1);
        }

        return hash;
    }

    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
