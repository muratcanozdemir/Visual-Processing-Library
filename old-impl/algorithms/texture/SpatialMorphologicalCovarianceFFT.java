/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.frequential.FFT;
/*   9:    */ import vpt.algorithms.mm.gray.GErosion;
/*  10:    */ import vpt.algorithms.statistical.SpatialMoment;
/*  11:    */ import vpt.util.se.FlatSE;
/*  12:    */ 
/*  13:    */ public class SpatialMorphologicalCovarianceFFT
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16: 22 */   public double[] output = null;
/*  17: 23 */   public Image input = null;
/*  18: 24 */   public Integer len = null;
/*  19: 25 */   public Integer momentX = null;
/*  20: 26 */   public Integer momentY = null;
/*  21:    */   
/*  22:    */   public SpatialMorphologicalCovarianceFFT()
/*  23:    */   {
/*  24: 29 */     this.inputFields = "input, len, momentX, momentY";
/*  25: 30 */     this.outputFields = "output";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 34 */     int cdim = this.input.getCDim();
/*  32: 35 */     int size = this.len.intValue() * 4 * cdim;
/*  33: 36 */     this.output = new double[size];
/*  34:    */     
/*  35: 38 */     DoubleImage[] fft = FFT.invoke(this.input, Boolean.valueOf(false), Integer.valueOf(2));
/*  36: 39 */     double[] originalVolumes = SpatialMoment.invoke(fft[0], this.momentX, this.momentY);
/*  37: 41 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  38:    */     {
/*  39: 42 */       int side = i * 2 + 1;
/*  40:    */       
/*  41:    */ 
/*  42: 45 */       FlatSE se = new FlatSE(1, side, new Point(0, i));
/*  43: 46 */       se.setXYBoolean(0, 0, true);
/*  44: 47 */       se.setXYBoolean(0, side - 1, true);
/*  45:    */       
/*  46: 49 */       Image tmp = GErosion.invoke(this.input, se);
/*  47:    */       
/*  48: 51 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  49: 52 */       double[] moments = SpatialMoment.invoke(fft[0], this.momentX, this.momentY);
/*  50: 54 */       for (int c = 0; c < cdim; c++) {
/*  51: 55 */         this.output[(c * 4 * this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/*  52:    */       }
/*  53: 58 */       se = new FlatSE(side, side, new Point(i, i));
/*  54: 59 */       se.setXYBoolean(0, 0, true);
/*  55: 60 */       se.setXYBoolean(side - 1, side - 1, true);
/*  56:    */       
/*  57: 62 */       tmp = GErosion.invoke(this.input, se);
/*  58:    */       
/*  59: 64 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  60: 65 */       moments = SpatialMoment.invoke(fft[0], this.momentX, this.momentY);
/*  61: 67 */       for (int c = 0; c < cdim; c++) {
/*  62: 68 */         this.output[(c * 4 * this.len.intValue() + this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/*  63:    */       }
/*  64: 71 */       se = new FlatSE(side, 1, new Point(i, 0));
/*  65: 72 */       se.setXYBoolean(0, 0, true);
/*  66: 73 */       se.setXYBoolean(side - 1, 0, true);
/*  67:    */       
/*  68: 75 */       tmp = GErosion.invoke(this.input, se);
/*  69:    */       
/*  70: 77 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  71: 78 */       moments = SpatialMoment.invoke(fft[0], this.momentX, this.momentY);
/*  72: 80 */       for (int c = 0; c < cdim; c++) {
/*  73: 81 */         this.output[(c * 4 * this.len.intValue() + 2 * this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/*  74:    */       }
/*  75: 84 */       se = new FlatSE(side, side, new Point(i, i));
/*  76: 85 */       se.setXYBoolean(side - 1, 0, true);
/*  77: 86 */       se.setXYBoolean(0, side - 1, true);
/*  78:    */       
/*  79: 88 */       tmp = GErosion.invoke(this.input, se);
/*  80:    */       
/*  81: 90 */       fft = FFT.invoke(tmp, Boolean.valueOf(false), Integer.valueOf(2));
/*  82: 91 */       moments = SpatialMoment.invoke(fft[0], this.momentX, this.momentY);
/*  83: 93 */       for (int c = 0; c < cdim; c++) {
/*  84: 94 */         this.output[(c * 4 * this.len.intValue() + 3 * this.len.intValue() + i - 1)] = (moments[c] / originalVolumes[c]);
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static double[] invoke(Image image, Integer len, Integer momentX, Integer momentY)
/*  90:    */   {
/*  91:    */     try
/*  92:    */     {
/*  93:100 */       return (double[])new SpatialMorphologicalCovarianceFFT().preprocess(new Object[] { image, len, momentX, momentY });
/*  94:    */     }
/*  95:    */     catch (GlobalException e)
/*  96:    */     {
/*  97:102 */       e.printStackTrace();
/*  98:    */     }
/*  99:103 */     return null;
/* 100:    */   }
/* 101:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.SpatialMorphologicalCovarianceFFT
 * JD-Core Version:    0.7.0.1
 */