public class foo {
private Size getOptimalPreviewSize(List<Size> sizes, int width, int height)
{           
    Size optimalSize = null;                                

    final double ASPECT_TOLERANCE = 0.1;
    double targetRatio = (double) height / width;

    // Try to find a size match which suits the whole screen minus the menu on the left.
    for (Size size : sizes)
    {
        if (size.height != width) continue;
        double ratio = (double) size.width / size.height;
        if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE)
        {
            optimalSize = size;
        }               
    }

    // If we cannot find the one that matches the aspect ratio, ignore the requirement.
    if (optimalSize == null)
    {
        // TODO : Backup in case we don't get a size.
    }

    return optimalSize;
}
}