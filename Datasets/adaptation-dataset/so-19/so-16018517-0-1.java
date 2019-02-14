public class foo {
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    int size;
    if(widthMode == MeasureSpec.EXACTLY && widthSize > 0){
        size = widthSize;
    }
    else if(heightMode == MeasureSpec.EXACTLY && heightSize > 0){
        size = heightSize;
    }
    else{
        size = widthSize < heightSize ? widthSize : heightSize;
    }

    int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
    super.onMeasure(finalMeasureSpec, finalMeasureSpec);
}
}