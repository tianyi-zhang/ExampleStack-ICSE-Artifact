package com.os3.expatmdm;

import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Sysinfo {
    /**
     * Gathers system information
     * @return A HashMap containing key-value configuration settings
     */
    public static HashMap<String, String> Gather() {
        HashMap<String, String> info = new HashMap<>();

        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            info.put(entry.getKey().toString(), entry.getValue().toString());
        }

        info.put("android.sdk", Build.VERSION.SDK_INT+"");
        info.put("android.codename", Build.VERSION.CODENAME);
        info.put("android.incremental", Build.VERSION.INCREMENTAL);
        info.put("android.release", Build.VERSION.RELEASE);

        info.put("build.board", Build.BOARD);
        info.put("build.bootloader", Build.BOOTLOADER);
        info.put("build.brand", Build.BRAND);
        info.put("build.cpu_abi", Build.CPU_ABI);
        info.put("build.cpu_abi2", Build.CPU_ABI2);
        info.put("build.device", Build.DEVICE);
        info.put("build.display", Build.DISPLAY);
        info.put("build.fingerprint", Build.FINGERPRINT);
        //info.put("build.radio", Build.getRadioVersion());
        info.put("build.hardware", Build.HARDWARE);
        info.put("build.host", Build.HOST);
        info.put("build.id", Build.ID);
        info.put("build.manufacturer", Build.MANUFACTURER);
        info.put("build.model", Build.MODEL);
        info.put("build.product", Build.PRODUCT);
        info.put("build.serial", Build.SERIAL);
        info.put("build.user", Build.USER);
        info.put("build.tags", Build.TAGS);
        info.put("build.type", Build.TYPE);
        info.put("build.unknown", Build.UNKNOWN);
        info.put("build.time", Build.TIME+"");

        info.put("kernel.version", getKernelVersion());
        info.put("kernel.verity", getVerityAvailable()+"");
        info.put("kernel.vermagic", getVermagic()+"");

        String runtime = getCurrentRuntimeValue();
        info.put("runtime.real", runtime == null ? System.getProperty("java.vm.name") : runtime);
        info.put("memory.mappings", TextUtils.join(",", getMemoryMappings().toArray()));

        info.put("methods.kallsyms", getKallsymsAvailable() ? "1" : "0");
        info.put("methods.kcore", getKcoreAvailable() ? "1": "0");
        info.put("methods.devmem", getDevMemAvailable() ? "1" : "0");
        info.put("methods.devkmem", getDevKmemAvailable() ? "1" : "0");
        info.put("methods.modules", getModulesAvailable() ? "1" : "0");
        info.put("methods.devptmx", getDevPtmxAvailable() ? "1" : "0");

        File loc= new File(Expat.Ctx.getFilesDir(), "ksymsprint");
        try {
            copy(new File("/data/local/tmp/kallsymsprint"), loc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Executer("chmod 770 " + loc.getAbsolutePath());
        Executer(loc.getAbsolutePath());
        return info;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static String getKernelVersion() {
        String procVersionStr = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }
        } catch (IOException e) {
        }

        return procVersionStr;
    }

    public static String Executer(String command) {
        StringBuffer output = new StringBuffer();
        StringBuffer output2 = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            line = "";
            while ((line = reader.readLine())!= null) {
                output2.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        String response2 = output2.toString();
        return response;
    }


    // Since system properties java.vm.name and java.specification.name are not reliable right now
    // Use reflection to get the real runtime
    // http://stackoverflow.com/questions/19830342/how-can-i-detect-the-android-runtime-dalvik-or-art

    private static final String SELECT_RUNTIME_PROPERTY = "persist.sys.dalvik.vm.lib";
    private static final String LIB_DALVIK = "libdvm.so";
    private static final String LIB_ART = "libart.so";
    private static final String LIB_ART_D = "libartd.so";

    private static String getCurrentRuntimeValue() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            try {
                Method get = systemProperties.getMethod("get",
                        String.class, String.class);
                if (get == null) {
                    return null;
                }
                try {
                    final String value = (String)get.invoke(
                            systemProperties, SELECT_RUNTIME_PROPERTY,
                        /* Assuming default is */"Dalvik");
                    if (LIB_DALVIK.equals(value)) {
                        return "Dalvik";
                    } else if (LIB_ART.equals(value)) {
                        return "ART";
                    } else if (LIB_ART_D.equals(value)) {
                        return "ART debug build";
                    }

                    return value;
                } catch (IllegalAccessException e) {
                    return null;
                } catch (IllegalArgumentException e) {
                    return null;
                } catch (InvocationTargetException e) {
                    return null;
                }
            } catch (NoSuchMethodException e) {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Set<String> getMemoryMappings() {
        Set<String> libs = new HashSet<>();
        try {
            String mapsFile = "/proc/" + android.os.Process.myPid() + "/maps";
            BufferedReader reader = new BufferedReader(new FileReader(mapsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".so") || line.endsWith(".jar") || line.endsWith(".odex") || line.endsWith(".dex") || line.endsWith(".dat")) {
                    int n = line.lastIndexOf(" ");
                    libs.add(line.substring(n + 1));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return libs;
    }

    private static boolean getKallsymsAvailable() {
        File f = new File("/proc/kallsyms");
        return f.exists();
    }

    private static boolean getModulesAvailable() {
        File f = new File("/proc/modules");
        return f.exists();
    }

    private static boolean getDevMemAvailable() {
        File f = new File("/dev/mem");
        return f.exists();
    }

    private static boolean getDevKmemAvailable() {
        File f = new File("/dev/kmem");
        return f.exists();
    }

    private static boolean getDevPtmxAvailable() {
        File f = new File("/dev/ptmx");
        return f.exists();
    }

    private static boolean getKcoreAvailable() {
        File f = new File("/proc/kcore");
        return f.exists();
    }

    // There may be a better way to check if verity is in use
    // veritysetup binary should be available on the device
    private static int getVerityAvailable() {
        int found = 0;

        try { // avoid permission errors
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec("which veritysetup");
                found = (proc.exitValue() == 0) ? 1 : 0;
                if (found > 0) return found;
            } catch (IOException e) {
                e.printStackTrace();
            }

            FilenameFilter fstabFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("fstab") || name.startsWith("fstab") || name.startsWith("vold");
                }
            };

            List<File> find1 = Arrays.asList(new File("/").listFiles(fstabFilter));
            List<File> find2 = Arrays.asList(new File("/etc").listFiles(fstabFilter));
            List<File> finds = new ArrayList<>(find1);
            finds.addAll(find2);

            for (File f : finds) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int n = line.trim().replace('\t', ' ').lastIndexOf(" ");
                        // http://nelenkov.blogspot.nl/2014/05/using-kitkat-verified-boot.html
                        if (line.substring(n + 1).contains("verify"))
                            return 2;
                    }
                } catch (FileNotFoundException e) {
                } catch (IOException e) {}
            }
        } catch (Exception e) {}

        return found;
    }

    // Find kernel modules and read their embedded vermagic
    // Surely this can be found elsewhere?
    private static String getVermagic() {
        try {
            FilenameFilter koFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ko");
                }
            };

            File[] kos = new File("/system/lib/modules/").listFiles(koFilter);
            for (File ko : kos) {
                StreamSearcher searcher = new StreamSearcher(new byte[]{'v','e','r','m','a','g','i','c','='});
                // bottleneck... though buffering the input stream already cuts down execution time
                BufferedInputStream fis = new BufferedInputStream(new FileInputStream(ko));
                boolean found = searcher.search(fis);
                if (found) {
                    StringBuilder builder = new StringBuilder();
                    int c;
                    while ((c = fis.read()) > 31 && c < 127) {
                        builder.append((char) c);
                    }

                    String magic = builder.toString().trim();
                    if (magic.length() > 4) // arbitrary number
                        return magic;
                }
            }
        } catch(Exception e){}
        return "";
    }

    // ToDo: method that detects kernel module signing
}
