import vpt.*;
import vpt.algorithms.frequential.*;

/**
 * Dumps the vpt.algorithms.frequential package: FFT, IFFT, DFT2D, IDFT2D.
 * All four are confirmed non-functional in the Java (see FFT.py's/DFT2D.py's
 * docstrings for the mechanism) -- this driver exists to golden-verify that
 * this port reproduces that exact broken behaviour, not a working transform.
 *
 * Usage: java -cp classes:oracle DumpFrequential > python/tests/goldens/frequential.txt
 */
public class DumpFrequential {

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
        ByteImage g = new ByteImage(4, 4, 1);
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                g.setXYCByte(x, y, 0, (x == 1 && y == 1) ? 100 : 10);

        DoubleImage[] fft = FFT.invoke(g, Boolean.FALSE, Integer.valueOf(1));
        dumpDouble("FFT(g,false,REAL_IMAG)[0]", fft[0]);
        dumpDouble("FFT(g,false,REAL_IMAG)[1]", fft[1]);

        DoubleImage[] fftMag = FFT.invoke(g, Boolean.TRUE, Integer.valueOf(3));
        dumpDouble("FFT(g,true,MAG_PHASE)[0]", fftMag[0]);
        dumpDouble("FFT(g,true,MAG_PHASE)[1]", fftMag[1]);

        dumpDouble("IFFT(fft[0],fft[1])", IFFT.invoke(fft[0], fft[1]));

        DoubleImage[] dft = DFT2D.invoke(g, Boolean.FALSE);
        dumpDouble("DFT2D(g,false)[0]", dft[0]);
        dumpDouble("DFT2D(g,false)[1]", dft[1]);
        dumpDouble("DFT2D(g,false)[2]", dft[2]);

        DoubleImage[] dftShifted = DFT2D.invoke(g, Boolean.TRUE);
        dumpDouble("DFT2D(g,true)[2]", dftShifted[2]);

        dumpDouble("IDFT2D(dft[0],dft[1])", IDFT2D.invoke(dft[0], dft[1]));
    }
}
