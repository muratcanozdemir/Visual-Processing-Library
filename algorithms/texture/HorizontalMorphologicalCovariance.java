/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.frequential.FFT;
/*   9:    */ import vpt.algorithms.mm.gray.GClosing;
/*  10:    */ import vpt.algorithms.mm.gray.GOpening;
/*  11:    */ import vpt.algorithms.statistical.Kurtosis;
/*  12:    */ import vpt.algorithms.statistical.Skewness;
/*  13:    */ import vpt.algorithms.statistical.Variance;
/*  14:    */ import vpt.util.Tools;
/*  15:    */ import vpt.util.se.FlatSE;
/*  16:    */ 
/*  17:    */ public class HorizontalMorphologicalCovariance
/*  18:    */   extends Algorithm
/*  19:    */ {
/*  20: 28 */   public double[] output = null;
/*  21: 29 */   public Image input = null;
/*  22: 30 */   public Integer len = null;
/*  23: 31 */   public Integer valuation = null;
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
/*  35:    */   public HorizontalMorphologicalCovariance()
/*  36:    */   {
/*  37: 45 */     this.inputFields = "input,len,valuation";
/*  38: 46 */     this.outputFields = "output";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void execute()
/*  42:    */     throws GlobalException
/*  43:    */   {
/*  44: 50 */     int size = 2 * this.len.intValue();
/*  45: 51 */     this.output = new double[size];
/*  46: 53 */     if (this.input.getCDim() != 1) {
/*  47: 53 */       throw new GlobalException("Only grayscale please");
/*  48:    */     }
/*  49: 55 */     double originalValue = valuation(this.input);
/*  50: 57 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  51:    */     {
/*  52: 58 */       int side = i * 2 + 1;
/*  53:    */       
/*  54: 60 */       FlatSE se = new FlatSE(side, 1, new Point(i, 0));
/*  55: 61 */       se.setXYBoolean(0, 0, true);
/*  56: 62 */       se.setXYBoolean(side - 1, 0, true);
/*  57:    */       
/*  58: 64 */       Image tmp = GOpening.invoke(this.input, se);
/*  59:    */       
/*  60: 66 */       this.output[(i - 1)] = (valuation(tmp) / originalValue);
/*  61:    */       
/*  62: 68 */       tmp = GClosing.invoke(this.input, se);
/*  63:    */       
/*  64: 70 */       this.output[(this.len.intValue() + i - 1)] = (valuation(tmp) / originalValue);
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   private double valuation(Image img)
/*  69:    */     throws GlobalException
/*  70:    */   {
/*  71: 75 */     switch (this.valuation.intValue())
/*  72:    */     {
/*  73:    */     case 0: 
/*  74: 77 */       return Tools.volume(img, 0);
/*  75:    */     case 5: 
/*  76: 80 */       return Variance.invoke(img).doubleValue();
/*  77:    */     case 6: 
/*  78: 83 */       return Skewness.invoke(img).doubleValue();
/*  79:    */     case 7: 
/*  80: 86 */       return Kurtosis.invoke(img).doubleValue();
/*  81:    */     case 8: 
/*  82: 89 */       DoubleImage[] fft = FFT.invoke(img.getChannel(0), Boolean.valueOf(false), Integer.valueOf(2));
/*  83: 90 */       return Variance.invoke(fft[0]).doubleValue();
/*  84:    */     case 9: 
/*  85: 93 */       DoubleImage[] fft = FFT.invoke(img.getChannel(0), Boolean.valueOf(false), Integer.valueOf(2));
/*  86: 94 */       return Kurtosis.invoke(fft[0]).doubleValue();
/*  87:    */     case 2: 
/*  88: 97 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/*  89:    */     case 1: 
/*  90:100 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  91:101 */       return Tools.volume(fft[0], 0);
/*  92:    */     case 3: 
/*  93:104 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  94:105 */       return vpt.algorithms.statistical.InvariantMoment.invoke(fft[0], Integer.valueOf(1))[0];
/*  95:    */     case 4: 
/*  96:109 */       if ((img == null) || (img == this.input)) {
/*  97:109 */         return this.input.getXDim() * this.input.getYDim();
/*  98:    */       }
/*  99:111 */       long counter = Tools.numberOfDifferentPixels(this.input, img);
/* 100:112 */       return counter;
/* 101:    */     }
/* 102:115 */     throw new GlobalException("Invalid valuation type");
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static double[] invoke(Image image, Integer len, Integer valuation)
/* 106:    */   {
/* 107:    */     try
/* 108:    */     {
/* 109:121 */       return (double[])new HorizontalMorphologicalCovariance().preprocess(new Object[] { image, len, valuation });
/* 110:    */     }
/* 111:    */     catch (GlobalException e)
/* 112:    */     {
/* 113:123 */       e.printStackTrace();
/* 114:    */     }
/* 115:124 */     return null;
/* 116:    */   }
/* 117:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.HorizontalMorphologicalCovariance
 * JD-Core Version:    0.7.0.1
 */