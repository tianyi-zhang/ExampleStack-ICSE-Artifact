public class foo{
    private void parse(final WebAppContext context, AnnotationParser parser) throws Exception
    {
        Collection<Resource> _resources = collectResources(new LinkedHashSet<Resource>(), context.getClassLoader());

        for (Resource _resource : _resources)
        {
            if (_resource == null)
            {
                return;
            }

            parser.clearHandlers();
            
            for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
            {
                if (h instanceof AbstractDiscoverableAnnotationHandler)
                {
                    ((AbstractDiscoverableAnnotationHandler) h).setResource(null); //
                }
            }
            
            parser.registerHandlers(_discoverableAnnotationHandlers);
            parser.registerHandler(_classInheritanceHandler);
            parser.registerHandlers(_containerInitializerAnnotationHandlers);

            parser.parse(_resource, new ClassNameResolver()
            {
                @Override
                public boolean isExcluded(String name)
                {
                    if (context.isSystemClass(name))
                    {
                        return true;
                    }
                    if (context.isServerClass(name))
                    {
                        return false;
                    }
                    
                    return false;
                }

                @Override
                public boolean shouldOverride(String name)
                {
                    //looking at webapp classpath, found already-parsed class of same name - did it come from system or duplicate in webapp?
                    if (context.isParentLoaderPriority())
                    {
                        return false;
                    }
                    
                    return true;
                }
            });
        }
    }
}