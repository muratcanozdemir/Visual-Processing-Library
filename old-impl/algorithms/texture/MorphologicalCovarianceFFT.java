/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.frequential.FFT;
/*   9:    */ import vpt.algorithms.mm.gray.GErosion;
/*  10:    */ import vpt.util.Tools;
/*  11:    */ import vpt.util.se.FlatSE;
/*  12:    */ 
/*  13:    */ public class MorphologicalCovarianceFFT
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16: 27 */   public double[] output = null;
/*  17: 28 */   public Image input = null;
/*  18: 29 */   public Integer len = null;
/*  19:    */   
/*  20:    */   public MorphologicalCovarianceFFT()
/*  21:    */   {
/*  22: 32 */     this.inputFields = "input,len";
/*  23: 33 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 37 */     int cdim = this.input.getCDim();
/*  30: 38 */     int size = this.len.intValue() * 4 * cdim;
/*  31: 39 */     this.output = new double[size];
/*  32:    */     
/*  33: 41 */     double[] originalVolumes = new double[cdim];
/*  34: 43 */     for (int c = 0; c < cdim; c++)
/*  35:    */     {
/*  36: 44 */       DoubleImage[] fft = FFT.invoke(this.input.getChannel(c), Boolean.valueOf(false), Integer.valueOf(2));
/*  37: 45 */       originalVolumes[c] = Tools.volume(fft[0], 0);
/*  38:    */     }
/*  39: 48 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  40:    */     {
/*  41: 49 */       int side = i * 2 + 1;
/*  42:    */       
/*  43:    */ 
/*  44: 52 */       FlatSE se = new FlatSE(1, side, new Point(0, i));
/*  45: 53 */       se.setXYBoolean(0, 0, true);
/*  46: 54 */       se.setXYBoolean(0, side - 1, true);
/*  47:    */       
/*  48: 56 */       Image tmp = GErosion.invoke(this.input, se);
/*  49:    */       
/*  50: 58 */       DoubleImage[] fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  51: 59 */       double fvol = Tools.volume(fft[0], 0);
/*  52: 61 */       for (int c = 0; c < cdim; c++) {
/*  53: 62 */         this.output[(c * 4 * this.len.intValue() + i - 1)] = (fvol / originalVolumes[c]);
/*  54:    */       }
/*  55: 65 */       se = new FlatSE(side, side, new Point(i, i));
/*  56: 66 */       se.setXYBoolean(0, 0, true);
/*  57: 67 */       se.setXYBoolean(side - 1, side - 1, true);
/*  58:    */       
/*  59: 69 */       tmp = GErosion.invoke(this.input, se);
/*  60:    */       
/*  61: 71 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  62: 72 */       fvol = Tools.volume(fft[0], 0);
/*  63: 74 */       for (int c = 0; c < cdim; c++) {
/*  64: 75 */         this.output[(c * 4 * this.len.intValue() + this.len.intValue() + i - 1)] = (fvol / originalVolumes[c]);
/*  65:    */       }
/*  66: 78 */       se = new FlatSE(side, 1, new Point(i, 0));
/*  67: 79 */       se.setXYBoolean(0, 0, true);
/*  68: 80 */       se.setXYBoolean(side - 1, 0, true);
/*  69:    */       
/*  70: 82 */       tmp = GErosion.invoke(this.input, se);
/*  71:    */       
/*  72: 84 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  73: 85 */       fvol = Tools.volume(fft[0], 0);
/*  74: 87 */       for (int c = 0; c < cdim; c++) {
/*  75: 88 */         this.output[(c * 4 * this.len.intValue() + 2 * this.len.intValue() + i - 1)] = (fvol / originalVolumes[c]);
/*  76:    */       }
/*  77: 91 */       se = new FlatSE(side, side, new Point(i, i));
/*  78: 92 */       se.setXYBoolean(side - 1, 0, true);
/*  79: 93 */       se.setXYBoolean(0, side - 1, true);
/*  80:    */       
/*  81: 95 */       tmp = GErosion.invoke(this.input, se);
/*  82:    */       
/*  83: 97 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  84: 98 */       fvol = Tools.volume(fft[0], 0);
/*  85:100 */       for (int c = 0; c < cdim; c++) {
/*  86:101 */         this.output[(c * 4 * this.len.intValue() + 3 * this.len.intValue() + i - 1)] = (fvol / originalVolumes[c]);
/*  87:    */       }
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static double[] invoke(Image image, Integer len)
/*  92:    */   {
/*  93:    */     try
/*  94:    */     {
/*  95:107 */       return (double[])new MorphologicalCovarianceFFT().preprocess(new Object[] { image, len });
/*  96:    */     }
/*  97:    */     catch (GlobalException e)
/*  98:    */     {
/*  99:109 */       e.printStackTrace();
/* 100:    */     }
/* 101:110 */     return null;
/* 102:    */   }
/* 103:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.MorphologicalCovarianceFFT
 * JD-Core Version:    0.7.0.1
 */