/*
 * Copyright Xiao Yi and Philip Cook. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list
 *   of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer listed in this license in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the copyright holders nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package us.fibernet.fiberj;

import java.io.File;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import javax.imageio.ImageIO;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;

/**
 * A utility class for read diffraction pattern image files
 *
 */
@SuppressWarnings("restriction")
public final class PatternReader {

    private static int[][] image;

    private PatternReader() {
    }

    /**
     * Parse the pattern file extension and call accordingly a read method
     * Read pattern files that come with attributes not stored in itself
     */
    public static Pattern readPattern(String[] args) {

        if(args == null || args.length < 1) {
            return null;
        }

        String fname = args[0];
        int[][] data = null;
        Pattern pattern = null;

        if(args.length == 1) {
            if(fname.toLowerCase().endsWith("tif")) {
                data = readTif(fname);
            }
            else if(fname.toLowerCase().endsWith("plr")) {
                data = readPlr(fname);
            }
            else if(fname.toLowerCase().endsWith("smv")) {
                return PatternSmv.readSmvPattern(fname);
            }
            else {
                data = readImage(fname);
            }
        }
        else {
            if(fname.toLowerCase().endsWith("dat")) {
                int w = 0, h = 0;
                try {
                    w = Integer.parseInt(args[1]);
                    h = Integer.parseInt(args[2]);
                    data = readDat(fname, w, h);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("dat image width and height must be specified");
                }
            }
        }


        if(data != null) {
            pattern = new Pattern(data, new File(fname).getName(), false);
        }

        return pattern;
    }

    /**
     * Parse the pattern file extension and call accordingly a read method
     * Read pattern files that come with attributes not stored in itself
     */
    public static int[][] readPatternData(String[] args) {

        if(args == null || args.length < 1) {
            return null;
        }

        String fname = args[0];

        if(args.length == 1) {
            if(fname.toLowerCase().endsWith("tif")) {
                return readTif(fname);
            }
            else if(fname.toLowerCase().endsWith("plr")) {
                return readPlr(fname);
            }
            else if(fname.toLowerCase().endsWith("smv")) {
                return PatternSmv.readSmv(fname);
            }
            else {
                return readImage(fname);
            }
        }
        else {
            if(fname.toLowerCase().endsWith("dat")) {
                int w = 0, h = 0;
                try {
                    w = Integer.parseInt(args[1]);
                    h = Integer.parseInt(args[2]);
                    return readDat(fname, w, h);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("dat image width and height must be specified");
                }
            }
        }

        return null;
    }

    /*
     * Read a raw data image and extract image data into a 2D array
     */
    private static int[][] readDat(String fname, int width, int height) {

        EndianCorrectInputStream input = null;
        image = null;

        try {

            image = new int[height][width];
            input = new EndianCorrectInputStream(new FileInputStream(fname), false);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    image[i][j] = (int) (input.readShortCorrect());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return image;
    }

    /*
     * Read a TIFF image and extract image data into a 2D array
     */
    private static int[][] readTif(String fname) {

        image = null;

        try {
            SeekableStream s = new FileSeekableStream(fname);
            TIFFDecodeParam param = null;
            ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
            RenderedImage op = dec.decodeAsRenderedImage(0);
            Raster raster = op.getData();
            image = new int[op.getHeight()][op.getWidth()];
            // System.out.println(op.getHeight() + " " + op.getWidth());

            int[] pixelColor = new int[4];
            for (int x = 0; x < op.getWidth(); x++) {
                for (int y = 0; y < op.getHeight(); y++) {
                    raster.getPixel(x, y, pixelColor);
                    image[y][x] = pixelColor[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    /*
     * Read PNG, JPG, GIF, etc into a 2D array
     */
    private static int[][] readImage(String fname) {

        image = null;

        try {

            BufferedImage img = ImageIO.read(new File(fname));
            int height = img.getHeight();
            int width = img.getWidth();
            //int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);

            // TODO: extract pixel values according to our color model
            byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

            image = new int[height][width];
            int p = 0;
            for(int h=0; h<height; h++) {
                for(int w=0; w<width; w++) {
                    image[h][w] = pixels[p++];
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    /*
    public static void getPixels(String url) {

        BufferedImage image = ImageIO.read(new URL(url));

        int w = image.getWidth();
        int h = image.getHeight();

        int[] dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w);

        Color c = new Color(dataBuffInt[100]);

        System.out.println(c.getRed());   // = (dataBuffInt[100] >> 16) & 0xFF
        System.out.println(c.getGreen()); // = (dataBuffInt[100] >> 8)  & 0xFF
        System.out.println(c.getBlue());  // = (dataBuffInt[100] >> 0)  & 0xFF
        System.out.println(c.getAlpha()); // = (dataBuffInt[100] >> 24) & 0xFF
    }
    */

    // ref: http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
    //
    private static int[][] getPixels(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
           final int pixelLength = 4;
           for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
              int argb = 0;
              argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
              argb += ((int) pixels[pixel + 1] & 0xff); // blue
              argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
              argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
              result[row][col] = argb;
              col++;
              if (col == width) {
                 col = 0;
                 row++;
              }
           }
        } else {
           final int pixelLength = 3;
           for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
              int argb = 0;
              argb += -16777216; // 255 alpha
              argb += ((int) pixels[pixel] & 0xff); // blue
              argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
              argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
              result[row][col] = argb;
              col++;
              if (col == width) {
                 col = 0;
                 row++;
              }
           }
        }

        return result;
     }

    /*
     * Read a PLR image and extract image data into a 2D array
     */
    public static int[][] readPlr(String fname) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            PatternPlr dp = new PatternPlr();
            dp.parseData(br);
            br.close();
            return dp.getImageData();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     */
    static int[][] doubleToInt(double[][] array) {
        int[][] output = new int[array.length][array[0].length];
        for (int r = 0; r < array.length; r++) {
            for (int c = 0; c < array[0].length; c++) {
                output[r][c] = (int) array[r][c];
            }
        }
        return output;
    }

} // class PatternReader



/**
 * Input stream provided with NiftiDataset class. Better to use tools.EndianNeutralInputStream,
 * because that gives an endian-correct result from the standard <code>DataInput</code> methods,
 * and will also read unsigned types.
 *
 * @author Philip Cook (imported this code)
 * @version $Id$
 */

class EndianCorrectInputStream extends DataInputStream {

    private boolean bigendian = true;

    public EndianCorrectInputStream(String filename, boolean be) throws FileNotFoundException {
        super(new FileInputStream(filename));
        bigendian = be;
    }

    public EndianCorrectInputStream(InputStream is, boolean be) {
        super(is);
        bigendian = be;
    }

    public short readShortCorrect() throws IOException {
        short val;

        val = readShort();
        if(bigendian) {
            return (val);
        } else {
            int byte0 = (int) val & 0xff;
            int byte1 = ((int) val >> 8) & 0xff;
            // swap the byte order
            return (short) ((byte0 << 8) | (byte1));
        }
    }

    public short flipShort(short val) {

        int byte0 = (int) val & 0xff;
        int byte1 = ((int) val >> 8) & 0xff;
        // swap the byte order
        return (short) ((byte0 << 8) | (byte1));
    }

    public int readIntCorrect() throws IOException {
        int val;

        val = readInt();
        if(bigendian) {
            return (val);
        }

        else {
            int byte0 = val & 0xff;
            int byte1 = (val >> 8) & 0xff;
            int byte2 = (val >> 16) & 0xff;
            int byte3 = (val >> 24) & 0xff;
            // swap the byte order
            return (byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3;
        }
    }

    // flipInt will flip the byte order of an int
    public int flipInt(int val) {

        int byte0 = val & 0xff;
        int byte1 = (val >> 8) & 0xff;
        int byte2 = (val >> 16) & 0xff;
        int byte3 = (val >> 24) & 0xff;
        // swap the byte order
        return (byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3;
    }

    public long readLongCorrect() throws IOException {
        long val;

        val = readLong();
        if(bigendian) {
            return (val);
        }

        else {
            return (flipLong(val));
        }
    }

    // flipLong will flip the byte order of a long
    public long flipLong(long val) {

        long byte0 = val & 0xff;
        long byte1 = (val >> 8) & 0xff;
        long byte2 = (val >> 16) & 0xff;
        long byte3 = (val >> 24) & 0xff;
        long byte4 = (val >> 32) & 0xff;
        long byte5 = (val >> 40) & 0xff;
        long byte6 = (val >> 48) & 0xff;
        long byte7 = (val >> 56) & 0xff;
        // swap the byte order
        return (long) ((byte0 << 56) | (byte1 << 48) | (byte2 << 40)
                | (byte3 << 32) | (byte4 << 24) | (byte5 << 16) | (byte6 << 8) | byte7);
    }

    public float readFloatCorrect() throws IOException {
        float val;

        if(bigendian) {
            val = readFloat();
        }

        else {
            int x = readUnsignedByte();
            x |= ((int) readUnsignedByte()) << 8;
            x |= ((int) readUnsignedByte()) << 16;
            x |= ((int) readUnsignedByte()) << 24;
            val = (float) Float.intBitsToFloat(x);
        }
        return val;
    }

    // flipFloat will flip the byte order of a float
    public float flipFloat(float val) throws IOException {

        int x = Float.floatToIntBits(val);
        int y = flipInt(x);
        return Float.intBitsToFloat(y);
    }

    public double readDoubleCorrect() throws IOException {
        double val;
        if(bigendian) {
            val = readDouble();
        } else {
            long x = readUnsignedByte();
            x |= ((long) readUnsignedByte()) << 8;
            x |= ((long) readUnsignedByte()) << 16;
            x |= ((long) readUnsignedByte()) << 24;
            x |= ((long) readUnsignedByte()) << 32;
            x |= ((long) readUnsignedByte()) << 40;
            x |= ((long) readUnsignedByte()) << 48;
            x |= ((long) readUnsignedByte()) << 56;
            val = Double.longBitsToDouble(x);
        }
        return val;
    }

    // flipDouble will flip the byte order of a double
    public double flipDouble(double val) {

        long x = Double.doubleToLongBits(val);
        long y = flipLong(x);
        return Double.longBitsToDouble(y);
    }

}

