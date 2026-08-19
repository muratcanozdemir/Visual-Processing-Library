package javax.media.jai;

import java.awt.Point;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/**
 * Shim replacing javax.media.jai.RasterFactory (Java Advanced Imaging).
 * JAI's RasterFactory is a thin convenience wrapper over java.awt.image;
 * these delegate to the JDK equivalents with identical semantics.
 */
public final class RasterFactory {

    private RasterFactory() {}

    public static SampleModel createPixelInterleavedSampleModel(
            int dataType, int width, int height, int pixelStride,
            int scanlineStride, int[] bandOffsets) {
        return new PixelInterleavedSampleModel(
                dataType, width, height, pixelStride, scanlineStride, bandOffsets);
    }

    public static SampleModel createPixelInterleavedSampleModel(
            int dataType, int width, int height, int numBands) {
        int[] bandOffsets = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            bandOffsets[i] = i;
        }
        return new PixelInterleavedSampleModel(
                dataType, width, height, numBands, numBands * width, bandOffsets);
    }

    public static SampleModel createBandedSampleModel(
            int dataType, int width, int height, int numBands) {
        return new BandedSampleModel(dataType, width, height, numBands);
    }

    public static WritableRaster createWritableRaster(
            SampleModel sampleModel, DataBuffer dataBuffer, Point location) {
        return Raster.createWritableRaster(sampleModel, dataBuffer, location);
    }

    public static WritableRaster createWritableRaster(
            SampleModel sampleModel, Point location) {
        return Raster.createWritableRaster(sampleModel, location);
    }
}
