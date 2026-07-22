/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.frequential.FFT;
/*   8:    */ import vpt.algorithms.mm.gray.GClosing;
/*   9:    */ import vpt.algorithms.mm.gray.GOpening;
/*  10:    */ import vpt.algorithms.statistical.SpatialMoment;
/*  11:    */ import vpt.util.Tools;
/*  12:    */ import vpt.util.se.FlatSE;
/*  13:    */ 
/*  14:    */ public class SpatialLineGranulometry
/*  15:    */   extends Algorithm
/*  16:    */ {
/*  17: 22 */   public double[] output = null;
/*  18: 23 */   public Image input = null;
/*  19: 24 */   public Integer len = null;
/*  20: 25 */   public Integer valuation = null;
/*  21: 26 */   public Integer momentx = Integer.valueOf(0);
/*  22: 27 */   public Integer momenty = Integer.valueOf(0);
/*  23:    */   public static final int VOLUME = 0;
/*  24:    */   public static final int FOURIER_VOLUME = 1;
/*  25:    */   public static final int MOMENT1 = 2;
/*  26:    */   public static final int FOURIER_MOMENT1 = 3;
/*  27:    */   public static final int MOMENT = 4;
/*  28:    */   
/*  29:    */   public SpatialLineGranulometry()
/*  30:    */   {
/*  31: 36 */     this.inputFields = "input,len,valuation,momentx,momenty";
/*  32: 37 */     this.outputFields = "output";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void execute()
/*  36:    */     throws GlobalException
/*  37:    */   {
/*  38: 41 */     int cdim = this.input.getCDim();
/*  39: 42 */     int size = this.len.intValue() * cdim * 2;
/*  40: 43 */     this.output = new double[size];
/*  41:    */     
/*  42: 45 */     double[] originalValues = new double[cdim];
/*  43: 47 */     for (int c = 0; c < cdim; c++) {
/*  44: 48 */       originalValues[c] = valuation(this.input, c);
/*  45:    */     }
/*  46: 50 */     for (int i = 1; i <= this.len.intValue(); i++)
/*  47:    */     {
/*  48: 51 */       FlatSE se = FlatSE.hLine(i * 2 + 1);
/*  49:    */       
/*  50: 53 */       Image tmp = GOpening.invoke(this.input, se);
/*  51: 55 */       for (int c = 0; c < cdim; c++) {
/*  52: 56 */         this.output[(c * this.len.intValue() * 2 + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/*  53:    */       }
/*  54: 58 */       tmp = GClosing.invoke(this.input, se);
/*  55: 60 */       for (int c = 0; c < cdim; c++) {
/*  56: 61 */         this.output[(c * this.len.intValue() * 2 + this.len.intValue() + i - 1)] = (valuation(tmp, c) / originalValues[c]);
/*  57:    */       }
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   private double valuation(Image img, int channel)
/*  62:    */     throws GlobalException
/*  63:    */   {
/*  64: 66 */     switch (this.valuation.intValue())
/*  65:    */     {
/*  66:    */     case 0: 
/*  67: 68 */       return Tools.volume(img, channel);
/*  68:    */     case 1: 
/*  69: 71 */       DoubleImage[] fft = FFT.invoke(img.getChannel(channel), Boolean.valueOf(false), Integer.valueOf(2));
/*  70: 72 */       return Tools.volume(fft[0], 0);
/*  71:    */     case 2: 
/*  72: 75 */       return vpt.algorithms.statistical.InvariantMoment.invoke(img, Integer.valueOf(1))[0];
/*  73:    */     case 3: 
/*  74: 78 */       DoubleImage[] fft = FFT.invoke(img, Boolean.valueOf(true), Integer.valueOf(2));
/*  75: 79 */       return vpt.algorithms.statistical.InvariantMoment.invoke(fft[0], Integer.valueOf(1))[0];
/*  76:    */     case 4: 
/*  77: 81 */       SpatialMoment.invoke(img.getChannel(channel), this.momentx, this.momenty);
/*  78:    */     }
/*  79: 84 */     throw new GlobalException("Invalid valuation type");
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static double[] invoke(Image image, Integer len, Integer valuation, int momentx, int momenty)
/*  83:    */   {
/*  84:    */     try
/*  85:    */     {
/*  86: 91 */       return (double[])new SpatialLineGranulometry().preprocess(new Object[] { image, len, valuation, Integer.valueOf(momentx), Integer.valueOf(momenty) });
/*  87:    */     }
/*  88:    */     catch (GlobalException e)
/*  89:    */     {
/*  90: 93 */       e.printStackTrace();
/*  91:    */     }
/*  92: 94 */     return null;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static double[] invoke(Image image, Integer len, Integer valuation)
/*  96:    */   {
/*  97:    */     try
/*  98:    */     {
/*  99:100 */       return (double[])new SpatialLineGranulometry().preprocess(new Object[] { image, len, valuation, Integer.valueOf(0), Integer.valueOf(0) });
/* 100:    */     }
/* 101:    */     catch (GlobalException e)
/* 102:    */     {
/* 103:102 */       e.printStackTrace();
/* 104:    */     }
/* 105:103 */     return null;
/* 106:    */   }
/* 107:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.SpatialLineGranulometry
 * JD-Core Version:    0.7.0.1
 */