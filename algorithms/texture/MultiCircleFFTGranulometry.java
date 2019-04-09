/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.frequential.FFT;
/*  8:   */ import vpt.algorithms.mm.gray.GClosing;
/*  9:   */ import vpt.algorithms.mm.gray.GOpening;
/* 10:   */ import vpt.util.Tools;
/* 11:   */ import vpt.util.se.FlatSE;
/* 12:   */ 
/* 13:   */ public class MultiCircleFFTGranulometry
/* 14:   */   extends Algorithm
/* 15:   */ {
/* 16:18 */   public double[] output = null;
/* 17:19 */   public Image input = null;
/* 18:20 */   public Integer len = null;
/* 19:21 */   public Integer step = null;
/* 20:22 */   public Integer howManyCircles = null;
/* 21:   */   
/* 22:   */   public MultiCircleFFTGranulometry()
/* 23:   */   {
/* 24:25 */     this.inputFields = "input,len,step,howManyCircles";
/* 25:26 */     this.outputFields = "output";
/* 26:   */   }
/* 27:   */   
/* 28:   */   public void execute()
/* 29:   */     throws GlobalException
/* 30:   */   {
/* 31:30 */     int cdim = this.input.getCDim();
/* 32:31 */     int size = this.len.intValue() * this.howManyCircles.intValue() * 2;
/* 33:32 */     this.output = new double[size];
/* 34:34 */     if (cdim != 1) {
/* 35:34 */       throw new GlobalException("Sorry, only grayscale for now");
/* 36:   */     }
/* 37:36 */     double[] originalValues = new double[this.howManyCircles.intValue()];
/* 38:   */     
/* 39:38 */     DoubleImage[] fft = FFT.invoke(this.input, Boolean.valueOf(true), Integer.valueOf(2));
/* 40:40 */     for (int j = 0; j < this.howManyCircles.intValue(); j++) {
/* 41:41 */       originalValues[j] = Tools.circularVolume(fft[0], 0, j, this.howManyCircles.intValue());
/* 42:   */     }
/* 43:43 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 44:   */     {
/* 45:44 */       FlatSE se = FlatSE.disk((i - 1) * this.step.intValue() + 1);
/* 46:   */       
/* 47:46 */       Image tmp = GOpening.invoke(this.input, se);
/* 48:   */       
/* 49:48 */       fft = FFT.invoke(tmp, Boolean.valueOf(true), Integer.valueOf(2));
/* 50:50 */       for (int j = 0; j < this.howManyCircles.intValue(); j++) {
/* 51:51 */         this.output[((i - 1) * this.howManyCircles.intValue() + j)] = (Tools.circularVolume(fft[0], 0, j, this.howManyCircles.intValue()) / originalValues[j]);
/* 52:   */       }
/* 53:53 */       tmp = GClosing.invoke(this.input, se);
/* 54:   */       
/* 55:55 */       fft = FFT.invoke(tmp, Boolean.valueOf(true), Integer.valueOf(2));
/* 56:57 */       for (int j = 0; j < this.howManyCircles.intValue(); j++) {
/* 57:58 */         this.output[(this.howManyCircles.intValue() * this.len.intValue() + (i - 1) * this.howManyCircles.intValue() + j)] = (Tools.circularVolume(fft[0], 0, j, this.howManyCircles.intValue()) / originalValues[j]);
/* 58:   */       }
/* 59:   */     }
/* 60:   */   }
/* 61:   */   
/* 62:   */   public static double[] invoke(Image image, Integer len, Integer step, Integer howManyCircles)
/* 63:   */   {
/* 64:   */     try
/* 65:   */     {
/* 66:64 */       return (double[])new MultiCircleFFTGranulometry().preprocess(new Object[] { image, len, step, howManyCircles });
/* 67:   */     }
/* 68:   */     catch (GlobalException e)
/* 69:   */     {
/* 70:66 */       e.printStackTrace();
/* 71:   */     }
/* 72:67 */     return null;
/* 73:   */   }
/* 74:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.MultiCircleFFTGranulometry
 * JD-Core Version:    0.7.0.1
 */