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
/*  16:    */ public class RotationInvariantMorphologicalCovariance
/*  17:    */   extends Algorithm
/*  18:    */ {
/*  19: 26 */   public double[] output = null;
/*  20: 27 */   public Image input = null;
/*  21: 28 */   public Integer len = null;
/*  22: 29 */   public Integer step = null;
/*  23: 30 */   public Integer valuation = null;
/*  24:    */   public static final int VOLUME = 0;
/*  25:    */   public static final int FOURIER_VOLUME = 1;
/*  26:    */   public static final int MOMENT1 = 2;
/*  27:    */   public static final int FOURIER_MOMENT1 = 3;
/*  28:    */   public static final int LOCATIONS = 4;
/*  29:    */   public static final int VARIANCE = 5;
/*  30:    */   public static final int SKEWNESS = 6;
/*  31:    */   public static final int KURTOSIS = 7;
/*  32:    */   public static final int FOURIER_VARIANCE = 8;
/*  33:    */   public static final int FOURIER_KURTOSIS = 9;
/*  34:    */   
/*  35:    */   public RotationInvariantMorphologicalCovariance()
/*  36:    */   {
/*  37: 44 */     this.inputFields = "input,len,step,valuation";
/*  38: 45 */     this.outputFields = "output";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void execute()
/*  42:    */     throws GlobalException
/*  43:    */   {
/*  44: 49 */     int cdim = this.input.getCDim();
/*  45: 50 */     int size = this.len.intValue() * 2;
/*  46: 51 */     this.output = new double[size];
/*  47: 53 */     if (cdim != 1) {
/*  48: 53 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  49:    */     }
/*  50: 55 */     double originalValue = valuation(this.input);
/*  51: 57 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  52:    */     {
/*  53: 58 */       FlatSE se = FlatSE.circle((i - 1) * this.step.intValue() + 1);
/*  54:    */       
/*  55: 60 */       Image tmp = GOpening.invoke(this.input, se);
/*  56:    */       
/*  57: 62 */       this.output[(i - 1)] = (valuation(tmp) / originalValue);
/*  58:    */       
/*  59: 64 */       tmp = GClosing.invoke(this.input, se);
/*  60:    */       
/*  61: 66 */       this.output[(this.len.intValue() + i - 1)] = (valuation(tmp) / originalValue);
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   private double valuation(Image img)
/*  66:    */     throws GlobalException
/*  67:    */   {
/*  68: 71 */     switch (this.valuation.intValue())
/*  69:    */     {
/*  70:    */     case 0: 
/*  71: 73 */       return Tools.volume(img, 0);
/*  72:    */     case 5: 
/*  73: 76 */       return Variance.invoke(img).doubleValue();
/*  74:    */     case 6: 
/*  75: 79 */       return Skewness.invoke(img).doubleValue();
/*  76:    */     case 7: 
/*  77: 82 */       return Kurtosis.invoke(img).doubleValue();
/*  78:    */     case 8: 
/*  79: 85 */       DoubleImage[] fft = FFT.invoke(img.getChannel(0), Boolean.valueOf(false), Integer.valueOf(2));
/*  80: 86 */       return Variance.invoke(fft[0]).doubleValue();
/*  81:    */     case 9: 
/*  82: 89 */       DoubleImage[] fft = FFT.invoke(img.getChannel(0), Boolean.valueOf(false), Integer.valueOf(2));
/*  83: 90 */       return Kurtosis.invoke(fft[0]).doubleValue();
/*  84:    */     case 2: 
/*  85: 93 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/*  86:    */     case 1: 
/*  87: 96 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  88: 97 */       return Tools.volume(fft[0], 0);
/*  89:    */     case 3: 
/*  90:100 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  91:101 */       return vpt.algorithms.statistical.InvariantMoment.invoke(fft[0], Integer.valueOf(1))[0];
/*  92:    */     case 4: 
/*  93:105 */       if ((img == null) || (img == this.input)) {
/*  94:105 */         return this.input.getXDim() * this.input.getYDim();
/*  95:    */       }
/*  96:107 */       long counter = Tools.numberOfDifferentPixels(this.input, img);
/*  97:108 */       return counter;
/*  98:    */     }
/*  99:111 */     throw new GlobalException("Invalid valuation type");
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static double[] invoke(Image image, Integer len, Integer step, Integer valuation)
/* 103:    */   {
/* 104:    */     try
/* 105:    */     {
/* 106:118 */       return (double[])new RotationInvariantMorphologicalCovariance().preprocess(new Object[] { image, len, step, valuation });
/* 107:    */     }
/* 108:    */     catch (GlobalException e)
/* 109:    */     {
/* 110:120 */       e.printStackTrace();
/* 111:    */     }
/* 112:121 */     return null;
/* 113:    */   }
/* 114:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.RotationInvariantMorphologicalCovariance
 * JD-Core Version:    0.7.0.1
 */