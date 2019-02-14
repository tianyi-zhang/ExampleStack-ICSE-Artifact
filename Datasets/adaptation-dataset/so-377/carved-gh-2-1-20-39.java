public class foo{
  public static List<Class> searchAnnotatedClasses(String basePackage, Class<?> annotation)
      throws IOException, ClassNotFoundException {
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
        resourcePatternResolver);

    List<Class> candidates = new ArrayList<>();
    String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
        resolveBasePackage(basePackage) + "/" + "**/*.class";
    Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
    for (Resource resource : resources) {
      if (resource.isReadable()) {
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
        if (isCandidate(metadataReader, annotation)) {
          candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
        }
      }
    }
    return candidates;
  }
}