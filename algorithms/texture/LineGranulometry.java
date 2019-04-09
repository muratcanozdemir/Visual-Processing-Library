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
/* 13:   */ public class LineGranulometry
/* 14:   */   extends Algorithm
/* 15:   */ {
/* 16:22 */   public double[] output = null;
/* 17:23 */   public Image input = null;
/* 18:24 */   public Integer len = null;
/* 19:25 */   public Integer valuation = null;
/* 20:   */   public static final int VOLUME = 0;
/* 21:   */   public static final int FOURIER_VOLUME = 1;
/* 22:   */   public static final int MOMENT1 = 2;
/* 23:   */   public static final int FOURIER_MOMENT1 = 3;
/* 24:   */   
/* 25:   */   public LineGranulometry()
/* 26:   */   {
/* 27:33 */     this.inputFields = "input,len,valuation";
/* 28:34 */     this.outputFields = "output";
/* 29:   */   }
/* 30:   */   
/* 31:   */   public void execute()
/* 32:   */     throws GlobalException
/* 33:   */   {
/* 34:38 */     int cdim = this.input.getCDim();
/* 35:39 */     int size = this.len.intValue() * cdim * 2;
/* 36:40 */     this.output = new double[size];
/* 37:   */     
/* 38:42 */     double[] originalValues = new double[cdim];
/* 39:44 */     for (int c = 0; c < cdim; c++) {
/* 40:45 */       originalValues[c] = valuation(this.input, c);
/* 41:   */     }
/* 42:47 */     for (int i = 1; i <= this.len.intValue(); i++)
/* 43:   */     {
/* 44:48 */       FlatSE se = FlatSE.hLine(i * 2 + 1);
/* 45:   */       
/* 46:50 */       Image tmp = GOpening.invoke(this.input, se);
/* 47:52 */       for (int c = 0; c < cdim; c++) {
/* 48:53 */         this.output[(c * this.len.intValue() * 2 + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/* 49:   */       }
/* 50:55 */       tmp = GClosing.invoke(this.input, se);
/* 51:57 */       for (int c = 0; c < cdim; c++) {
/* 52:58 */         this.output[(c * this.len.intValue() * 2 + this.len.intValue() + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/* 53:   */       }
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:   */   private double valuation(Image img, int channel)
/* 58:   */     throws GlobalException
/* 59:   */   {
/* 60:63 */     switch (this.valuation.intValue())
/* 61:   */     {
/* 62:   */     case 0: 
/* 63:65 */       return Tools.volume(img, channel);
/* 64:   */     case 1: 
/* 65:68 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/* 66:69 */       return Tools.volume(fft[0], 0);
/* 67:   */     case 2: 
/* 68:72 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/* 69:   */     case 3: 
/* 70:75 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/* 71:76 */       return vpt.algorithms.statistical.InvariantMoment.invoke(fft[0], Integer.valueOf(1))[0];
/* 72:   */     }
/* 73:79 */     throw new GlobalException("Invalid valuation type");
/* 74:   */   }
/* 75:   */   
/* 76:   */   public static double[] invoke(Image image, Integer len, Integer valuation)
/* 77:   */   {
/* 78:   */     try
/* 79:   */     {
/* 80:86 */       return (double[])new LineGranulometry().preprocess(new Object[] { image, len, valuation });
/* 81:   */     }
/* 82:   */     catch (GlobalException e)
/* 83:   */     {
/* 84:88 */       e.printStackTrace();
/* 85:   */     }
/* 86:89 */     return null;
/* 87:   */   }
/* 88:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.LineGranulometry
 * JD-Core Version:    0.7.0.1
 */