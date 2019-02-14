package com.liveperson.schema;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: rans
 * Date: 6/4/13
 * Time: 8:13 AM
 */
public class SchemaZkRepo implements SchemaRepo<String>{
    private static final Logger LOG = LoggerFactory.getLogger(SchemaZkRepo.class);
    public static final String ZK_CONNECT_CONNECT = "zkConnect.connect";

    public static final String ROOT_DEFAULT_PATH = "/migdalor/data-model/avroSchemaRepo";

    public static final String ROOT_PATH_KEY = "repo.root";

    private String rootPath;

    private SchemaCacheManager schemaCacheManager;
    public String zkConnect;


    public SchemaZkRepo(Properties properties) throws RepoException {
        initProperties(properties);
        schemaCacheManager = new SchemaCacheManager(zkConnect);
        writeRoot();
    }

    private void writeRoot(){
        schemaCacheManager.writeRoot(rootPath);
    }

    private void initProperties(Properties properties) {
        zkConnect = properties.getProperty(ZK_CONNECT_CONNECT);
        rootPath = (String)properties.get(ROOT_PATH_KEY);
        if (rootPath == null) {rootPath = ROOT_DEFAULT_PATH;}
    }

    @Override
    public void addSchema(String revision, String schema) {
        schemaCacheManager.addSchema(addRoot(revision), schema);
    }

    @Override
    public void removeSchema(String s)  {
        throw new NotImplementedException();
    }


    @Override
    public String getSchema(String revision) throws RepoException {
        Object sc = schemaCacheManager.getSchema(addRoot(revision));
        if (sc == null) {
            throw new RepoException("Failed to find schema for revision " + revision);
        }
        return sc.toString();
    }

    @Override
    public String getLatestSchemaId() throws RepoException {
        List<String> schemasList = schemaCacheManager.getShemasList(rootPath);
        if (schemasList == null || schemasList.size() == 0){
            throw new RepoException("Schema list is empty");
        }
        mavenVersion latestSchema = new mavenVersion("0");
        for (String schema : schemasList) {
            mavenVersion migdalortVersion = new mavenVersion(schema);
            if (migdalortVersion.compareTo(latestSchema) > 0){
                latestSchema = migdalortVersion;
            }
        }
        return latestSchema.version;
    }

    public SchemaCacheManager getSchemaCacheManager() {
        return schemaCacheManager;
    }

    public void setSchemaCacheManager(SchemaCacheManager schemaCacheManager) {
        this.schemaCacheManager = schemaCacheManager;
    }

    public String addRoot(String revision){
        return rootPath+"/"+revision;
    }



    /**
     *
     * in our company we use the maven version as revision version.
     * therefore
     *
     * been taken from
     * http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
     *
     */
    private class mavenVersion implements Comparable<mavenVersion> {

        private final static String SNAPSHOT = "-SNAPSHOT";

        private final String version;

        private final String [] parts;

        public final String getVersion() {
            return this.version;
        }

        public mavenVersion(String version) {
            if(version == null)
                throw new IllegalArgumentException("Version can not be null");

            version = version.replace("\"","");
            if(!version.matches("[0-9]+(\\.[0-9]+)*("+SNAPSHOT+")*"))
                throw new IllegalArgumentException("Invalid version format, version: "+version);
            this.version = version;

            String replaceSnapShot = version.replace(SNAPSHOT,".-1");
            //replaceSnapShot = version.replace(SNAPSHOT,".-1");
            this.parts = replaceSnapShot.split("\\.");
        }

        @Override public int compareTo(mavenVersion other) {
            if(other == null)
                return 1;
            String[] otherParts = other.parts;
            int length = Math.max(parts.length, otherParts.length);
            for(int i = 0; i < length; i++) {
                int thisPart = i < parts.length ?
                        Integer.parseInt(parts[i]) : 0;
                int thatPart = i < otherParts.length ?
                        Integer.parseInt(otherParts[i]) : 0;
                if(thisPart < thatPart)
                    return -1;
                if(thisPart > thatPart)
                    return 1;
            }
            return 0;
        }
    }
}
