public class foo {
public void foo(){
int iColor = Color.parseColor(color);

int red   = (iColor & 0xFF0000) / 0xFFFF;
int green = (iColor & 0xFF00) / 0xFF;
int blue  = iColor & 0xFF;

float[] matrix = { 0, 0, 0, 0, red,
                   0, 0, 0, 0, green,
                   0, 0, 0, 0, blue,
                   0, 0, 0, 1, 0 };

ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
drawable.setColorFilter(colorFilter);
}
}