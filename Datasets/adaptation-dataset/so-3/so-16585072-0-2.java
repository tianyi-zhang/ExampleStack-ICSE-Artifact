public class foo {
public static void lockScreenOrientation(Activity activity)
{   
    WindowManager windowManager =  (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);   
    Configuration configuration = activity.getResources().getConfiguration();   
    int rotation = windowManager.getDefaultDisplay().getRotation(); 

    // Search for the natural position of the device    
    if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&  
       (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||  
       configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&   
       (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))   
    {   
        // Natural position is Landscape    
        switch (rotation)   
        {   
            case Surface.ROTATION_0:    
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);    
                break;      
            case Surface.ROTATION_90:   
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); 
            break;      
            case Surface.ROTATION_180: 
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); 
                break;          
            case Surface.ROTATION_270: 
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
                break;
        }
    }
    else
    {
        // Natural position is Portrait
        switch (rotation) 
        {
            case Surface.ROTATION_0: 
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
            break;   
            case Surface.ROTATION_90: 
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
            break;   
            case Surface.ROTATION_180: 
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); 
                break;          
            case Surface.ROTATION_270: 
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); 
                break;
        }
    }
}
}