/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.frequential.FFT;
/*  8:   */ import vpt.algorithms.mm.gray.GClosing;
/*  9:   */ import vpt.algorithms.mm.gray.GOpening;
/* 10:   */ import vpt.util.se.FlatSE;
/* 11:   */ 
/* 12:   */ public class SpatialCircularGranulometryFFT
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:25 */   public double[] output = null;
/* 16:26 */   public Image input = null;
/* 17:27 */   public Integer len = null;
/* 18:28 */   public Integer step = null;
/* 19:29 */   public Integer momentX = null;
/* 20:30 */   public Integer momentY = null;
/* 21:   */   
/* 22:   */   public SpatialCircularGranulometryFFT()
/* 23:   */   {
/* 24:33 */     this.inputFields = "input,len,step,momentX,momentY";
/* 25:34 */     this.outputFields = "output";
/* 26:   */   }
/* 27:   */   
/* 28:   */   public void execute()
/* 29:   */     throws GlobalException
/* 30:   */   {
/* 31:38 */     int cdim = this.input.getCDim();
/* 32:39 */     int size = this.len.intValue() * cdim * 2;
/* 33:40 */     this.output = new double[size];
/* 34:   */     
/* 35:42 */     double[] originalValues = new double[cdim];
/* 36:44 */     for (int c = 0; c < cdim; c++) {
/* 37:45 */       originalValues[c] = valuation(this.input, c);
/* 38:   */     }
/* 39:47 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 40:   */     {
/* 41:48 */       FlatSE se = FlatSE.disk((i - 1) * this.step.intValue() + 1);
/* 42:   */       
/* 43:50 */       Image tmp = GOpening.invoke(this.input, se);
/* 44:52 */       for (int c = 0; c < cdim; c++) {
/* 45:53 */         this.output[(c * this.len.intValue() * 2 + i - 1)] = (1.0D - valuation(tmp, c) / originalValues[c]);
/* 46:   */       }
/* 47:55 */       tmp = GClosing.invoke(this.input, se);
/* 48:57 */       for (int c = 0; c < cdim; c++) {
/* 49:58 */         this.output[(c * this.len.intValue() * 2 + this.len.intValue() + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/* 50:   */       }
/* 51:   */     }
/* 52:   */   }
/* 53:   */   
/* 54:   */   private double valuation(Image img, int channel)
/* 55:   */   {
/* 56:63 */     DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/* 57:64 */     return vpt.algorithms.statistical.SpatialMoment.invoke(fft[0], this.momentX, this.momentY)[0];
/* 58:   */   }
/* 59:   */   
/* 60:   */   public static double[] invoke(Image image, Integer len, Integer step, Integer momentX, Integer momentY)
/* 61:   */   {
/* 62:   */     try
/* 63:   */     {
/* 64:69 */       return (double[])new SpatialCircularGranulometryFFT().preprocess(new Object[] { image, len, step, momentX, momentY });
/* 65:   */     }
/* 66:   */     catch (GlobalException e)
/* 67:   */     {
/* 68:71 */       e.printStackTrace();
/* 69:   */     }
/* 70:72 */     return null;
/* 71:   */   }
/* 72:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.SpatialCircularGranulometryFFT
 * JD-Core Version:    0.7.0.1
 */