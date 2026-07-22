/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.frequential.FFT;
/*   8:    */ import vpt.algorithms.mm.gray.GClosing;
/*   9:    */ import vpt.algorithms.mm.gray.GOpening;
/*  10:    */ import vpt.algorithms.statistical.Kurtosis;
/*  11:    */ import vpt.algorithms.statistical.Skewness;
/*  12:    */ import vpt.algorithms.statistical.Variance;
/*  13:    */ import vpt.util.Tools;
/*  14:    */ import vpt.util.se.FlatSE;
/*  15:    */ 
/*  16:    */ public class CircularGranulometry
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 24 */   public double[] output = null;
/*  20: 25 */   public Image input = null;
/*  21: 26 */   public Integer len = null;
/*  22: 27 */   public Integer step = null;
/*  23: 28 */   public Integer valuation = null;
/*  24:    */   public static final int VOLUME = 0;
/*  25:    */   public static final int FOURIER_VOLUME = 1;
/*  26:    */   public static final int FOURIER_VARIANCE = 2;
/*  27:    */   public static final int MOMENT1 = 3;
/*  28:    */   public static final int FOURIER_MOMENT1 = 4;
/*  29:    */   public static final int VARIANCE = 5;
/*  30:    */   public static final int SKEWNESS = 6;
/*  31:    */   public static final int KURTOSIS = 7;
/*  32:    */   public static final int FOURIER_KURTOSIS = 8;
/*  33:    */   public static final int MOMENT2 = 9;
/*  34:    */   
/*  35:    */   public CircularGranulometry()
/*  36:    */   {
/*  37: 42 */     this.inputFields = "input,len,step,valuation";
/*  38: 43 */     this.outputFields = "output";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void execute()
/*  42:    */     throws GlobalException
/*  43:    */   {
/*  44: 47 */     int cdim = this.input.getCDim();
/*  45: 48 */     int size = this.len.intValue() * cdim * 2;
/*  46: 49 */     this.output = new double[size];
/*  47:    */     
/*  48: 51 */     double[] originalValues = new double[cdim];
/*  49: 53 */     for (int c = 0; c < cdim; c++) {
/*  50: 54 */       originalValues[c] = valuation(this.input, c);
/*  51:    */     }
/*  52: 56 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  53:    */     {
/*  54: 57 */       FlatSE se = FlatSE.disk((i - 1) * this.step.intValue() + 1);
/*  55:    */       
/*  56: 59 */       Image tmp = GOpening.invoke(this.input, se);
/*  57: 61 */       for (int c = 0; c < cdim; c++) {
/*  58: 62 */         this.output[(c * this.len.intValue() * 2 + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/*  59:    */       }
/*  60: 64 */       tmp = GClosing.invoke(this.input, se);
/*  61: 66 */       for (int c = 0; c < cdim; c++) {
/*  62: 67 */         this.output[(c * this.len.intValue() * 2 + this.len.intValue() + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/*  63:    */       }
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   private double valuation(Image img, int channel)
/*  68:    */     throws GlobalException
/*  69:    */   {
/*  70: 72 */     switch (this.valuation.intValue())
/*  71:    */     {
/*  72:    */     case 0: 
/*  73: 74 */       return Tools.volume(img, channel);
/*  74:    */     case 5: 
/*  75: 77 */       return Variance.invoke(img).doubleValue();
/*  76:    */     case 6: 
/*  77: 80 */       return Skewness.invoke(img).doubleValue();
/*  78:    */     case 7: 
/*  79: 83 */       return Kurtosis.invoke(img).doubleValue();
/*  80:    */     case 2: 
/*  81: 86 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  82: 87 */       return Variance.invoke(fft[0]).doubleValue();
/*  83:    */     case 8: 
/*  84: 90 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  85: 91 */       return Kurtosis.invoke(fft[0]).doubleValue();
/*  86:    */     case 1: 
/*  87: 94 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  88: 95 */       return Tools.volume(fft[0], 0);
/*  89:    */     case 3: 
/*  90: 98 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/*  91:    */     case 9: 
/*  92:101 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(2))[0];
/*  93:    */     case 4: 
/*  94:104 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  95:105 */       return vpt.algorithms.statistical.InvariantMoment.invoke(fft[0], Integer.valueOf(1))[0];
/*  96:    */     }
/*  97:108 */     throw new GlobalException("Invalid valuation type");
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static double[] invoke(Image image, Integer len, Integer step, Integer valuation)
/* 101:    */   {
/* 102:    */     try
/* 103:    */     {
/* 104:115 */       return (double[])new CircularGranulometry().preprocess(new Object[] { image, len, step, valuation });
/* 105:    */     }
/* 106:    */     catch (GlobalException e)
/* 107:    */     {
/* 108:117 */       e.printStackTrace();
/* 109:    */     }
/* 110:118 */     return null;
/* 111:    */   }
/* 112:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.CircularGranulometry
 * JD-Core Version:    0.7.0.1
 */