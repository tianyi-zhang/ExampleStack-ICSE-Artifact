public class foo {
private Bitmap decodeFile(File f){
    Bitmap b = null;

        //Decode image size
    BitmapFactory.Options o = new BitmapFactory.Options();
    o.inJustDecodeBounds = true;

    FileInputStream fis = new FileInputStream(f);
    BitmapFactory.decodeStream(fis, null, o);
    fis.close();

    int scale = 1;
    if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
        scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / 
           (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
    }

    //Decode with inSampleSize
    BitmapFactory.Options o2 = new BitmapFactory.Options();
    o2.inSampleSize = scale;
    fis = new FileInputStream(f);
    b = BitmapFactory.decodeStream(fis, null, o2);
    fis.close();

    return b;
}
}