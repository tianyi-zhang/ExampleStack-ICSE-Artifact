public class foo {
@Override
protected boolean setFrame(int l, int t, int r, int b)
{

    Matrix matrix = getImageMatrix(); 
    float scaleFactor, scaleFactorWidth, scaleFactorHeight;
    scaleFactorWidth = (float)width/(float)getDrawable().getIntrinsicWidth();
    scaleFactorHeight = (float)height/(float)getDrawable().getIntrinsicHeight();    

    if(scaleFactorHeight > scaleFactorWidth) {
        scaleFactor = scaleFactorHeight;
    } else {
        scaleFactor = scaleFactorWidth;
    }

    matrix.setScale(scaleFactor, scaleFactor, 0, 0);
    setImageMatrix(matrix);

    return super.setFrame(l, t, r, b);
}
}