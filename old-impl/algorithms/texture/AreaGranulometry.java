/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.frequential.FFT;
/*   8:    */ import vpt.algorithms.mm.gray.connected.GAreaClosing;
/*   9:    */ import vpt.algorithms.mm.gray.connected.GAreaOpening;
/*  10:    */ import vpt.algorithms.statistical.Kurtosis;
/*  11:    */ import vpt.algorithms.statistical.Skewness;
/*  12:    */ import vpt.algorithms.statistical.Variance;
/*  13:    */ import vpt.util.Tools;
/*  14:    */ 
/*  15:    */ public class AreaGranulometry
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 23 */   public double[] output = null;
/*  19: 24 */   public Image input = null;
/*  20: 25 */   public Integer len = null;
/*  21: 26 */   public Integer step = null;
/*  22: 27 */   public Integer valuation = null;
/*  23:    */   public static final int VOLUME = 0;
/*  24:    */   public static final int FOURIER_VOLUME = 1;
/*  25:    */   public static final int FOURIER_VARIANCE = 2;
/*  26:    */   public static final int MOMENT1 = 3;
/*  27:    */   public static final int FOURIER_MOMENT1 = 4;
/*  28:    */   public static final int VARIANCE = 5;
/*  29:    */   public static final int SKEWNESS = 6;
/*  30:    */   public static final int KURTOSIS = 7;
/*  31:    */   public static final int FOURIER_KURTOSIS = 8;
/*  32:    */   public static final int MOMENT2 = 9;
/*  33:    */   
/*  34:    */   public AreaGranulometry()
/*  35:    */   {
/*  36: 41 */     this.inputFields = "input,len,step,valuation";
/*  37: 42 */     this.outputFields = "output";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void execute()
/*  41:    */     throws GlobalException
/*  42:    */   {
/*  43: 46 */     int cdim = this.input.getCDim();
/*  44: 47 */     int size = this.len.intValue() * cdim * 2;
/*  45: 48 */     this.output = new double[size];
/*  46:    */     
/*  47: 50 */     double[] originalValues = new double[cdim];
/*  48: 52 */     for (int c = 0; c < cdim; c++) {
/*  49: 53 */       originalValues[c] = valuation(this.input, c);
/*  50:    */     }
/*  51: 55 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  52:    */     {
/*  53: 56 */       Image tmp = GAreaOpening.invoke(this.input, Integer.valueOf(i * this.step.intValue()));
/*  54: 58 */       for (int c = 0; c < cdim; c++) {
/*  55: 59 */         this.output[(c * this.len.intValue() * 2 + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/*  56:    */       }
/*  57: 61 */       tmp = GAreaClosing.invoke(this.input, Integer.valueOf(i * this.step.intValue()));
/*  58: 63 */       for (int c = 0; c < cdim; c++) {
/*  59: 64 */         this.output[(c * this.len.intValue() * 2 + this.len.intValue() + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/*  60:    */       }
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   private double valuation(Image img, int channel)
/*  65:    */     throws GlobalException
/*  66:    */   {
/*  67: 69 */     switch (this.valuation.intValue())
/*  68:    */     {
/*  69:    */     case 0: 
/*  70: 71 */       return Tools.volume(img, channel);
/*  71:    */     case 5: 
/*  72: 74 */       return Variance.invoke(img).doubleValue();
/*  73:    */     case 6: 
/*  74: 77 */       return Skewness.invoke(img).doubleValue();
/*  75:    */     case 7: 
/*  76: 80 */       return Kurtosis.invoke(img).doubleValue();
/*  77:    */     case 2: 
/*  78: 83 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  79: 84 */       return Variance.invoke(fft[0]).doubleValue();
/*  80:    */     case 8: 
/*  81: 87 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  82: 88 */       return Kurtosis.invoke(fft[0]).doubleValue();
/*  83:    */     case 1: 
/*  84: 91 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  85: 92 */       return Tools.volume(fft[0], 0);
/*  86:    */     case 3: 
/*  87: 95 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/*  88:    */     case 9: 
/*  89: 98 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(2))[0];
/*  90:    */     case 4: 
/*  91:101 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  92:102 */       return vpt.algorithms.statistical.InvariantMoment.invoke(fft[0], Integer.valueOf(1))[0];
/*  93:    */     }
/*  94:105 */     throw new GlobalException("Invalid valuation type");
/*  95:    */   }
/*  96:    */   
/*  97:    */   public static double[] invoke(Image image, Integer len, Integer step, Integer valuation)
/*  98:    */   {
/*  99:    */     try
/* 100:    */     {
/* 101:112 */       return (double[])new AreaGranulometry().preprocess(new Object[] { image, len, step, valuation });
/* 102:    */     }
/* 103:    */     catch (GlobalException e)
/* 104:    */     {
/* 105:114 */       e.printStackTrace();
/* 106:    */     }
/* 107:115 */     return null;
/* 108:    */   }
/* 109:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.AreaGranulometry
 * JD-Core Version:    0.7.0.1
 */