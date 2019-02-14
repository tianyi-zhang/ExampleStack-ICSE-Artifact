public class foo{
    @Override
    public void configure(WebAppContext context) throws Exception
    {
        boolean metadataComplete = context.getMetaData().isMetaDataComplete();
        
        context.addDecorator(new AnnotationDecorator(context));

        //Even if metadata is complete, we still need to scan for ServletContainerInitializers - if there are any
        AnnotationParser parser = null;
        
        if (!metadataComplete)
        {
            //If metadata isn't complete, if this is a servlet 3 webapp or isConfigDiscovered is true, we need to search for annotations
            if ((context.getServletContext().getEffectiveMajorVersion() >= 3) || context.isConfigurationDiscovered())
            {
                _discoverableAnnotationHandlers.add(new WebServletAnnotationHandler(context));
                _discoverableAnnotationHandlers.add(new WebFilterAnnotationHandler(context));
                _discoverableAnnotationHandlers.add(new WebListenerAnnotationHandler(context));
            }
        }

        //Regardless of metadata, if there are any ServletContainerInitializers with @HandlesTypes, then we need to scan all the
        //classes so we can call their onStartup() methods correctly
        createServletContainerInitializerAnnotationHandlers(context, getNonExcludedInitializers(context));

        if (!_discoverableAnnotationHandlers.isEmpty() || (_classInheritanceHandler != null)
            || !_containerInitializerAnnotationHandlers.isEmpty())
        {
            parser = createAnnotationParser();

            parse(context, parser);

            for (DiscoverableAnnotationHandler h : _discoverableAnnotationHandlers)
            {
                context.getMetaData().addDiscoveredAnnotations(
                    ((AbstractDiscoverableAnnotationHandler) h).getAnnotationList());
            }
        }
    }
}