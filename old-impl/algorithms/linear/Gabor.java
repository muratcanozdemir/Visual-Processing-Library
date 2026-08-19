/*   1:    */ package vpt.algorithms.linear;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.histogram.ContrastStretch;
/*   9:    */ import vpt.util.se.NonFlatSE;
/*  10:    */ 
/*  11:    */ public class Gabor
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 15 */   private static final double[] DEFAULT_ORIENTATIONS = { 0.0D };
/*  15:    */   private static final double DEFAULT_WAVE_LENGTH = 1.0D;
/*  16:    */   private static final double DEFAULT_PHASE_OFFSET = 0.0D;
/*  17:    */   private static final double DEFAULT_ASPECT_RATIO = 0.5D;
/*  18:    */   private static final double DEFAULT_BANDWIDTH = 1.0D;
/*  19:    */   private static final int DEFAULT_WIDTH = 3;
/*  20:    */   private static final int DEFAULT_HEIGHT = 3;
/*  21:    */   private static final double MIN_ASPECT_RATIO = 0.0D;
/*  22:    */   private static final double MAX_ASPECT_RATIO = 1.0D;
/*  23:    */   public Image input;
/*  24:    */   public double[] orientations;
/*  25:    */   public Double waveLength;
/*  26:    */   public Double phaseOffset;
/*  27:    */   public Double aspectRatio;
/*  28:    */   public Double bandwidth;
/*  29:    */   public Integer width;
/*  30:    */   public Integer height;
/*  31:    */   public Image output;
/*  32:    */   
/*  33:    */   public Gabor()
/*  34:    */   {
/*  35: 37 */     this.inputFields = "input, waveLength, orientations, phaseOffset, aspectRatio, bandwidth, width, height";
/*  36: 38 */     this.outputFields = "output";
/*  37:    */   }
/*  38:    */   
/*  39:    */   private static double calculateSigma(double waveLength, double bandwidth)
/*  40:    */   {
/*  41: 50 */     return waveLength * Math.sqrt(Math.log(2.0D) / 2.0D) * (Math.pow(2.0D, bandwidth) + 1.0D) / ((Math.pow(2.0D, bandwidth) - 1.0D) * 3.141592653589793D);
/*  42:    */   }
/*  43:    */   
/*  44:    */   private static double gaborFunction(double x, double y, double sigma, double aspectRatio, double waveLength, double phaseOffset)
/*  45:    */   {
/*  46: 65 */     double gaborReal = Math.exp(-(Math.pow(x / sigma, 2.0D) + Math.pow(y * aspectRatio / sigma, 2.0D)) / 2.0D) * Math.cos(6.283185307179586D * x / waveLength + phaseOffset);
/*  47: 66 */     double gaborImage = Math.exp(-(Math.pow(x / sigma, 2.0D) + Math.pow(y * aspectRatio / sigma, 2.0D)) / 2.0D) * Math.sin(6.283185307179586D * x / waveLength + phaseOffset);
/*  48: 67 */     return Math.sqrt(Math.pow(gaborReal, 2.0D) + Math.pow(gaborImage, 2.0D));
/*  49:    */   }
/*  50:    */   
/*  51:    */   public NonFlatSE getKernel()
/*  52:    */   {
/*  53: 76 */     double sigma = calculateSigma(this.waveLength.doubleValue(), this.bandwidth.doubleValue());
/*  54:    */     
/*  55: 78 */     DoubleImage dimage = new DoubleImage(this.width.intValue(), this.height.intValue(), 1);
/*  56: 79 */     dimage.fill(0.0D);
/*  57: 81 */     for (int x = -this.width.intValue() / 2; x <= this.width.intValue() / 2; x++) {
/*  58: 82 */       for (int y = -this.height.intValue() / 2; y <= this.height.intValue() / 2; y++)
/*  59:    */       {
/*  60: 83 */         double d = 0.0D;
/*  61: 84 */         for (double orientation : this.orientations)
/*  62:    */         {
/*  63: 85 */           double x1 = x * Math.cos(orientation) + y * Math.sin(orientation);
/*  64: 86 */           double y1 = -1 * x * Math.sin(orientation) + y * Math.cos(orientation);
/*  65: 87 */           d += (float)gaborFunction(x1, y1, sigma, this.aspectRatio.doubleValue(), this.waveLength.doubleValue(), this.phaseOffset.doubleValue());
/*  66:    */         }
/*  67: 90 */         dimage.setXYDouble(x + this.width.intValue() / 2, y + this.height.intValue() / 2, d);
/*  68:    */       }
/*  69:    */     }
/*  70: 94 */     float sum = 0.0F;
/*  71: 96 */     for (int x = 0; x < this.width.intValue(); x++) {
/*  72: 97 */       for (int y = 0; y < this.height.intValue(); y++) {
/*  73: 98 */         sum = (float)(sum + dimage.getXYDouble(x, y));
/*  74:    */       }
/*  75:    */     }
/*  76:102 */     sum /= this.width.intValue() * this.height.intValue();
/*  77:104 */     for (int x = 0; x < this.width.intValue(); x++) {
/*  78:105 */       for (int y = 0; y < this.height.intValue(); y++)
/*  79:    */       {
/*  80:106 */         double d = dimage.getXYDouble(x, y);
/*  81:107 */         dimage.setXYDouble(x, y, d - sum);
/*  82:    */       }
/*  83:    */     }
/*  84:111 */     return new NonFlatSE(new Point(this.width.intValue() / 2, this.height.intValue() / 2), dimage);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void execute()
/*  88:    */     throws GlobalException
/*  89:    */   {
/*  90:115 */     this.output = Convolution.invoke(this.input, getKernel());
/*  91:116 */     this.output = ContrastStretch.invoke(this.output);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static Image invoke(Image image, Double wavelength, double[] orientations, Double phaseOffset, Double aspectRatio, Double bandwidth, Integer width, Integer height)
/*  95:    */   {
/*  96:    */     try
/*  97:    */     {
/*  98:122 */       return (Image)new Gabor().preprocess(new Object[] { image, wavelength, orientations, phaseOffset, aspectRatio, bandwidth, width, height });
/*  99:    */     }
/* 100:    */     catch (GlobalException e)
/* 101:    */     {
/* 102:124 */       e.printStackTrace();
/* 103:    */     }
/* 104:125 */     return null;
/* 105:    */   }
/* 106:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.Gabor
 * JD-Core Version:    0.7.0.1
 */