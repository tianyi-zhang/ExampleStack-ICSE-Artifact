/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.core.manpack.managers;

import com.google.common.collect.Maps;
import com.google.gson.*;
import de.sanandrew.core.manpack.mod.ConfigurationManager;
import de.sanandrew.core.manpack.mod.ModCntManPack;
import de.sanandrew.core.manpack.util.MutableString;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class SAPUpdateManager
{
    public static final List<Triplet<SAPUpdateManager, MutableBoolean, MutableString>> UPD_MANAGERS = new ArrayList<>();
    public static final Map<Integer, Boolean> IS_IN_RENDER_QUEUE = Maps.newHashMap();

    private boolean checkedForUpdate = false;
    private Version version;
    private String modName;
    private URL updURL;
    private String modInfoURL;
    private File modPackedJar;
    private UpdateFile updInfo = new UpdateFile();
    private final int mgrId;

    public UpdateDownloader downloader;

    public static synchronized void setChecked(int mgrId) {
        UPD_MANAGERS.get(mgrId).getValue1().setTrue();
    }

    public static synchronized void setHasUpdate(int mgrId, String version) {
        setChecked(mgrId);
        UPD_MANAGERS.get(mgrId).getValue2().set(version);
    }

    public static void setInRenderQueue(int mgrId) {
        IS_IN_RENDER_QUEUE.put(mgrId, true);
    }

    private SAPUpdateManager(String modName, Version version, String updateURL, String modURL, File modJar) {
        this.modName = modName;
        this.version = version.clone();
        this.modInfoURL = modURL;
        this.modPackedJar = modJar;

        try {
            this.updURL = new URL(updateURL);
            this.updURL.toURI();                // check validity
        } catch( MalformedURLException | NullPointerException | URISyntaxException e ) {
            this.updURL = null;
            ModCntManPack.UPD_LOG.log(Level.WARN, "The URL to the mod version file is invalid!");
            e.printStackTrace();
        }

        this.mgrId = UPD_MANAGERS.size();
    }

    public static SAPUpdateManager createUpdateManager(String modName, Version version, String updateURL, String modURL, File modJar) {
        SAPUpdateManager updMgr = new SAPUpdateManager(modName, version, updateURL, modURL, modJar);

        if( updMgr.updURL != null ) {
            UPD_MANAGERS.add(Triplet.with(updMgr, new MutableBoolean(false), new MutableString("")));
            IS_IN_RENDER_QUEUE.put(updMgr.mgrId, false);
        }

        return updMgr;
    }

    private void check() {
        Runnable threadProcessor = new Runnable()
        {
            @Override
            public void run() {
                try {
                    ModCntManPack.UPD_LOG.printf(Level.INFO, "Checking for %s update", SAPUpdateManager.this.getModName());
                    if( SAPUpdateManager.this.getUpdateURL() == null ) {
                        throw new MalformedURLException("[NULL]");
                    }

                    Gson gson = new GsonBuilder().registerTypeAdapter(UpdateFile.class, new AnnotatedDeserializer<UpdateFile>()).create();

                    try( BufferedReader in = new BufferedReader(new InputStreamReader(SAPUpdateManager.this.getUpdateURL().openStream())) ) {
                        SAPUpdateManager.this.updInfo = gson.fromJson(in, UpdateFile.class);
                    } catch( IOException | JsonSyntaxException ex ) {
                        ModCntManPack.UPD_LOG.printf(Level.WARN, "Check for Update failed!", ex);
                    }

                    if( SAPUpdateManager.this.updInfo.version == null || SAPUpdateManager.this.updInfo.version.length() < 1 ) {
                        SAPUpdateManager.setChecked(SAPUpdateManager.this.getId());
                        return;
                    }

                    Version webVersion = SAPUpdateManager.this.updInfo.getVersionInst();
                    SAPUpdateManager.this.updInfo.version = webVersion.toString();              // reformat the version number to the format major.minor.revision

                    Version currVersion = SAPUpdateManager.this.getVersion();
                    String currModName = SAPUpdateManager.this.getModName();
                    if( webVersion.versionType != EnumVersionType.RELEASE ) {
                        if( ConfigurationManager.subscribeToUnstable || currVersion.versionType != EnumVersionType.RELEASE ) {
                            if( webVersion.versionType.ordinal() > currVersion.versionType.ordinal() ) {
                                switch( webVersion.versionType ) {
                                    case BETA:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "A beta for %s is available: %s", currModName, webVersion);
                                        break;
                                    case RELEASECANDIDATE:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "A release candidate for %s is available: %s", currModName, webVersion);
                                        break;
                                    default:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "No new update for %s is available.", currModName);
                                        SAPUpdateManager.setChecked(SAPUpdateManager.this.getId());
                                        return;
                                }

                                SAPUpdateManager.setHasUpdate(SAPUpdateManager.this.mgrId, webVersion.toString());
                                return;
                            } else if( webVersion.preVersionNr > currVersion.preVersionNr ) {
                                switch( webVersion.versionType ) {
                                    case ALPHA:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "A new alpha for %s is out: %s", currModName, webVersion);
                                        break;
                                    case BETA:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "A new beta for %s is out: %s", currModName, webVersion);
                                        break;
                                    case RELEASECANDIDATE:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "A new release candidate for %s is out: %s", currModName, webVersion);
                                        break;
                                    default:
                                        ModCntManPack.UPD_LOG.printf(Level.INFO, "No new update for %s is available.", currModName);
                                        SAPUpdateManager.setChecked(SAPUpdateManager.this.getId());
                                        return;
                                }

                                SAPUpdateManager.setHasUpdate(SAPUpdateManager.this.mgrId, webVersion.toString());
                                return;
                            }
                        }
                    } else {
                        if( webVersion.major > currVersion.major ) {
                            ModCntManPack.UPD_LOG.printf(Level.INFO, "New major update for %s is out: %s", currModName, webVersion);
                            SAPUpdateManager.setHasUpdate(SAPUpdateManager.this.mgrId, webVersion.toString());
                            return;
                        } else if( webVersion.major == currVersion.major ) {
                            if( webVersion.minor > currVersion.minor ) {
                                ModCntManPack.UPD_LOG.printf(Level.INFO, "New minor update for %s is out: %s", currModName, webVersion);
                                SAPUpdateManager.setHasUpdate(SAPUpdateManager.this.mgrId, webVersion.toString());
                                return;
                            } else if( webVersion.minor == currVersion.minor ) {
                                if( webVersion.revision > currVersion.revision ) {
                                    ModCntManPack.UPD_LOG.printf(Level.INFO, "New bugfix update for %s is out: %s", currModName, webVersion);
                                    SAPUpdateManager.setHasUpdate(SAPUpdateManager.this.mgrId, webVersion.toString());
                                    return;
                                } else if( webVersion.revision == currVersion.revision && currVersion.versionType != EnumVersionType.RELEASE ) {
                                    ModCntManPack.UPD_LOG.printf(Level.INFO, "A stable release for %s is available: %s", currModName, webVersion);
                                    SAPUpdateManager.setHasUpdate(SAPUpdateManager.this.mgrId, webVersion.toString());
                                    return;
                                }
                            }
                        }
                    }

                    ModCntManPack.UPD_LOG.printf(Level.INFO, "No new update for %s is available.", currModName);
                } catch( IOException ioex ) {
                    ModCntManPack.UPD_LOG.printf(Level.WARN, String.format("Update Check for %s failed!", SAPUpdateManager.this.modName), ioex);
                }

                SAPUpdateManager.setChecked(SAPUpdateManager.this.getId());
            }
        };

        new Thread(threadProcessor, "SAPUpdateThread").start();
    }

    public void checkForUpdate() {
        if( !this.checkedForUpdate ) {
            this.check();
            this.checkedForUpdate = true;
        }
    }

    public Version getVersion() {
        return this.version;
    }

    public String getModName() {
        return this.modName;
    }

    public URL getUpdateURL() {
        return this.updURL;
    }

    public String getModInfoURL() {
        return this.modInfoURL;
    }

    public File getModJar() {
        return this.modPackedJar;
    }

    public boolean isModJarValid() {
        return this.modPackedJar != null && this.modPackedJar.isFile() && this.modPackedJar.getName().endsWith(".jar");
    }

    public int getId() {
        return this.mgrId;
    }

    public UpdateFile getUpdateInfo() {
        return this.updInfo;
    }

    public EnumUpdateSeverity getVersionDiffSeverity() {
        if( this.updInfo.severityOverride != null && this.updInfo.severityOverride.length() > 0 ) {
            return EnumUpdateSeverity.valueOf(this.updInfo.severityOverride);
        }

        Version updVersion = new Version(this.updInfo.version);

        if( updVersion.major > this.version.major ) {
            return EnumUpdateSeverity.SEVERE;
        } else if( updVersion.major == this.version.major ) {
            if( updVersion.minor >= this.version.minor + 4 ) {
                return EnumUpdateSeverity.SEVERE;
            } else if( updVersion.minor > this.version.minor ) {
                return EnumUpdateSeverity.MAJOR;
            } else if( updVersion.minor == this.version.minor ) {
                if( updVersion.revision >= this.version.revision + 8 ) {
                    return EnumUpdateSeverity.SEVERE;
                } else if( updVersion.revision >= this.version.revision + 4 ) {
                    return EnumUpdateSeverity.MAJOR;
                } else if( updVersion.revision > this.version.revision ) {
                    return EnumUpdateSeverity.MINOR;
                }
            }
        }

        return EnumUpdateSeverity.UNKNOWN;
    }

    public void runUpdate() {
        URL dl = this.updInfo.getDownload();
        if( dl != null ) {
            this.downloader = new UpdateDownloader(dl, this.modPackedJar);
        }
    }

    public static class UpdateFile
    {
        @JsonRequired
        public String version;
        public String downloadUrl;
        public String description;
        public String severityOverride;
        public String[] changelog;

        public UpdateFile() { }

        public URL getDownload() {
            try {
                URL dl = new URL(this.downloadUrl);
                dl.toURI();
                return dl;
            } catch( MalformedURLException | URISyntaxException e ) {
                return null;
            }
        }

        public Version getVersionInst() {
            return new Version(version);
        }
    }

    public enum EnumUpdateSeverity
    {
        MINOR(EnumChatFormatting.GREEN),
        MAJOR(EnumChatFormatting.YELLOW),
        SEVERE(EnumChatFormatting.RED),
        UNKNOWN(EnumChatFormatting.WHITE);

        public final EnumChatFormatting format;

        EnumUpdateSeverity(EnumChatFormatting formatting) {
            this.format = formatting;
        }
    }

    public static final class Version
            implements Cloneable
    {
        public final int revision;
        public final int minor;
        public final int major;
        public final EnumVersionType versionType;
        public final int preVersionNr;

        private static final Pattern[] VERSION_PATTERNS = new Pattern[] {
                // {1.7.10-}1.0.0{-beta{.2}}
                Pattern.compile("(\\d+.\\d+[\\.|_]\\d+-)?(?<major>\\d+)\\.(?<minor>\\d+)[\\.|_](?<revision>\\d+)(-(?<prType>alpha|beta|rc)(\\.(?<prNr>\\d+))?)?")
        };

        public Version(int majorNr, int minorNr, int revisionNr) {
            this.major = majorNr;
            this.minor = minorNr;
            this.revision = revisionNr;
            this.versionType = EnumVersionType.RELEASE;
            this.preVersionNr = 0;
        }

        public Version(int majorNr, int minorNr, int revisionNr, EnumVersionType type, int preVersionNr) {
            this.major = majorNr;
            this.minor = minorNr;
            this.revision = revisionNr;
            this.versionType = type;
            this.preVersionNr = preVersionNr;
        }

        public Version(String version) {
            if( version != null ) {
                Matcher matcher;
                for( Pattern verPattern : VERSION_PATTERNS ) {
                    matcher = verPattern.matcher(version);
                    if( matcher.find() ) {
                        this.major = Integer.valueOf(matcher.group("major"));
                        this.minor = Integer.valueOf(matcher.group("minor"));
                        this.revision = Integer.valueOf(matcher.group("revision"));
                        if( matcher.group("prType") != null ) {
                            String prType = matcher.group("prType");
                            switch( prType ) {
                                case "alpha":
                                    this.versionType = EnumVersionType.ALPHA;
                                    break;
                                case "beta":
                                    this.versionType = EnumVersionType.BETA;
                                    break;
                                case "rc":
                                    this.versionType = EnumVersionType.RELEASECANDIDATE;
                                    break;
                                default:
                                    this.versionType = EnumVersionType.UNKNOWN;
                            }

                            this.preVersionNr = matcher.group("prNr") != null ? Integer.valueOf(matcher.group("prNr")) : 1;
                        } else {
                            this.versionType = EnumVersionType.RELEASE;
                            this.preVersionNr = 0;
                        }
                        return;
                    }
                }
            }

            this.major = -1;
            this.minor = -1;
            this.revision = -1;
            this.versionType = EnumVersionType.UNKNOWN;
            this.preVersionNr = -1;
//	        FMLLog.log(ModCntManPack.UPD_LOG, Level.WARN,
//	                   "Version Number for the mod %s could not be compiled! The version number %s does not have the required formatting!",
//	                   this.modName, version);
        }

        @Override
        public String toString() {
            if( this.versionType == EnumVersionType.RELEASE ) {
                return String.format("%d.%d.%d", this.major, this.minor, this.revision);
            } else {
                return String.format("%d.%d.%d-%s.%d", this.major, this.minor, this.revision, this.versionType, this.preVersionNr);
            }
        }

        @Override
        public Version clone() {
            return new Version(this.major, this.minor, this.revision, this.versionType, this.preVersionNr);
        }
    }

    public enum EnumVersionType
    {
        ALPHA("alpha"),
        BETA("beta"),
        RELEASECANDIDATE("rc"),
        RELEASE,
        UNKNOWN;

        private final String versionStr;

        EnumVersionType() {
            this.versionStr = this.name();
        }

        EnumVersionType(String verStr) {
            this.versionStr = verStr;
        }

        @Override
        public String toString() {
            return this.versionStr;
        }
    }


    /**
     * code from http://stackoverflow.com/a/14245807
     */

    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.FIELD )
    @interface JsonRequired
    {
    }

    static class AnnotatedDeserializer<T>
            implements JsonDeserializer<T>
    {
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            T pojo = new Gson().fromJson(je, type);

            Field[] fields = pojo.getClass().getDeclaredFields();
            for( Field f : fields ) {
                if( f.getAnnotation(JsonRequired.class) != null ) {
                    try {
                        f.setAccessible(true);
                        if( f.get(pojo) == null ) {
                            throw new JsonParseException("Missing field in JSON: " + f.getName());
                        }
                    } catch( IllegalArgumentException | IllegalAccessException ex ) {
                        ModCntManPack.UPD_LOG.log(Level.WARN, ex, null);
                    }
                }
            }

            return pojo;
        }
    }
}
