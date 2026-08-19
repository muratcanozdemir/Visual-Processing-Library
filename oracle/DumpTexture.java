import vpt.*;
import vpt.algorithms.texture.*;
import vpt.algorithms.mm.gray.connected.attribute.Area;

/**
 * Dumps the vpt.algorithms.texture package -- top-level 23/23 files
 * complete. The six dataset-harness subpackages (alot, curet, kthtips,
 * kthtips2, kthtips2a, outex) and CircularFourierPowerSpectra are excluded
 * -- see python/README.md.
 *
 * SpatialLineGranulometry's valuation=MOMENT (4) is NOT exercised here:
 * it is a confirmed bug (see SpatialLineGranulometry.py's docstring) that
 * always throws GlobalException, so Java's own invoke() catches it and
 * returns null -- dumpVector(name, null) would NPE this driver. Verified
 * in Python only, via a direct call asserting the result is None.
 *
 * Usage: java -cp classes:oracle DumpTexture > python/tests/goldens/texture.txt
 */
public class DumpTexture {

    static void dumpValue(String name, double v) {
        System.out.println(name + "\t" + Double.toHexString(v));
    }

    static void dumpVector(String name, double[] v) {
        StringBuilder sb = new StringBuilder();
        for (double d : v) {
            if (sb.length() > 0) sb.append(',');
            sb.append(Double.toHexString(d));
        }
        System.out.println(name + "\t" + v.length + "\t" + sb);
    }

    static void dumpDouble(String name, Image img) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < img.getYDim(); y++)
            for (int x = 0; x < img.getXDim(); x++)
                for (int c = 0; c < img.getCDim(); c++) {
                    if (sb.length() > 0) sb.append(',');
                    sb.append(Double.toHexString(img.getXYCDouble(x, y, c)));
                }
        System.out.println(name + "\t" + img.getXDim() + "\t" + img.getYDim()
            + "\t" + img.getCDim() + "\t" + sb);
    }

    public static void main(String[] args) {
        // A 12x12 grayscale gradient, no degenerate flat regions.
        ByteImage gray = new ByteImage(12, 12, 1);
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                gray.setXYCByte(x, y, 0, (x * 7 + y * 3) % 256);
            }
        }

        // A 12x12 RGB gradient for MultiTextonHistogram (needs cdim==3).
        ByteImage rgb = new ByteImage(12, 12, 3);
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                rgb.setXYCByte(x, y, 0, (x * 7 + y * 3) % 256);
                rgb.setXYCByte(x, y, 1, (x * 3 + y * 11) % 256);
                rgb.setXYCByte(x, y, 2, (x * 13 + y) % 256);
            }
        }

        dumpVector("AreaGranulometry(gray)", AreaGranulometry.invoke(gray, 3, 2, 0));

        Area[] criteria = new Area[] { new Area(5), new Area(10) };
        dumpVector("AttributeProfile(gray)", AttributeProfile.invoke(gray, criteria, 0));

        dumpVector("CCBasedAreaProfile(gray)", CCBasedAreaProfile.invoke(gray, 8));

        dumpVector("CircularGranulometry(gray)", CircularGranulometry.invoke(gray, 3, 2, 5));

        dumpVector("ExtendedMorphologicalCovariance(gray)",
            ExtendedMorphologicalCovariance.invoke(gray, 3));

        dumpVector("HorizontalMorphologicalCovariance(gray,VOLUME)",
            HorizontalMorphologicalCovariance.invoke(gray, 3, 0));
        dumpVector("HorizontalMorphologicalCovariance(gray,LOCATIONS)",
            HorizontalMorphologicalCovariance.invoke(gray, 3, 4));

        dumpVector("LineGranulometry(gray)", LineGranulometry.invoke(gray, 3, 0));

        dumpVector("MorphologicalCovariance(gray)", MorphologicalCovariance.invoke(gray, 3));

        dumpVector("MorphologicalCovarianceFFT(gray)",
            MorphologicalCovarianceFFT.invoke(gray, 2));

        dumpVector("MultiCircleFFTCovariance(gray)",
            MultiCircleFFTCovariance.invoke(gray, 2, 2, 3));
        dumpVector("MultiCircleFFTGranulometry(gray)",
            MultiCircleFFTGranulometry.invoke(gray, 2, 2, 3));

        dumpVector("MultiResolutionHistogram(gray)", MultiResolutionHistogram.invoke(gray));

        dumpVector("MultiTextonHistogram(rgb)", MultiTextonHistogram.invoke(rgb));

        dumpVector("OrderedAngularVolumesHistogram(gray)",
            OrderedAngularVolumesHistogram.invoke(gray, 4));

        dumpVector("OrientationHistogram(gray)", OrientationHistogram.invoke(gray, 8, 3));

        dumpDouble("OrientationMap(gray)", OrientationMap.invoke(gray, 3));

        dumpVector("RotationInvariantGaborFeatures(gray)",
            RotationInvariantGaborFeatures.invoke(gray, 2));

        dumpVector("RotationInvariantMorphologicalCovariance(gray,VOLUME)",
            RotationInvariantMorphologicalCovariance.invoke(gray, 2, 2, 0));
        dumpVector("RotationInvariantMorphologicalCovariance(gray,LOCATIONS)",
            RotationInvariantMorphologicalCovariance.invoke(gray, 2, 2, 4));

        dumpValue("SmoothnessFactor(gray)", SmoothnessFactor.invoke(gray, 3));

        dumpVector("SpatialCircularGranulometry(gray)",
            SpatialCircularGranulometry.invoke(gray, 2, 2, 2, 0));
        dumpVector("SpatialCircularGranulometryFFT(gray)",
            SpatialCircularGranulometryFFT.invoke(gray, 2, 2, 2, 0));

        dumpVector("SpatialLineGranulometry(gray,VOLUME)",
            SpatialLineGranulometry.invoke(gray, 2, 0, 0, 0));

        dumpVector("SpatialMorphologicalCovariance(gray)",
            SpatialMorphologicalCovariance.invoke(gray, 2, 2, 0));
        dumpVector("SpatialMorphologicalCovarianceFFT(gray)",
            SpatialMorphologicalCovarianceFFT.invoke(gray, 2, 2, 0));
    }
}
