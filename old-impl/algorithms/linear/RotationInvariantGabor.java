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
/*  11:    */ public class RotationInvariantGabor
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14:    */   public Image input;
/*  15:    */   public double[] orientations;
/*  16:    */   public Double waveLength;
/*  17:    */   public Double phaseOffset;
/*  18:    */   public Double aspectRatio;
/*  19:    */   public Double bandwidth;
/*  20:    */   public Integer width;
/*  21:    */   public Integer height;
/*  22:    */   public Image output;
/*  23:    */   
/*  24:    */   public RotationInvariantGabor()
/*  25:    */   {
/*  26: 40 */     this.inputFields = "input, waveLength, orientations, phaseOffset, aspectRatio, bandwidth, width, height";
/*  27: 41 */     this.outputFields = "output";
/*  28:    */   }
/*  29:    */   
/*  30:    */   private static double calculateSigma(double waveLength, double bandwidth)
/*  31:    */   {
/*  32: 53 */     return waveLength * Math.sqrt(Math.log(2.0D) / 2.0D) * (Math.pow(2.0D, bandwidth) + 1.0D) / ((Math.pow(2.0D, bandwidth) - 1.0D) * 3.141592653589793D);
/*  33:    */   }
/*  34:    */   
/*  35:    */   private static double gaborFunction(double x, double y, double sigma, double aspectRatio, double waveLength, double phaseOffset)
/*  36:    */   {
/*  37: 68 */     double gaborReal = Math.exp(-(Math.pow(x / sigma, 2.0D) + Math.pow(y * aspectRatio / sigma, 2.0D)) / 2.0D) * Math.cos(6.283185307179586D * x / waveLength + phaseOffset);
/*  38: 69 */     double gaborImage = Math.exp(-(Math.pow(x / sigma, 2.0D) + Math.pow(y * aspectRatio / sigma, 2.0D)) / 2.0D) * Math.sin(6.283185307179586D * x / waveLength + phaseOffset);
/*  39: 70 */     return Math.sqrt(Math.pow(gaborReal, 2.0D) + Math.pow(gaborImage, 2.0D));
/*  40:    */   }
/*  41:    */   
/*  42:    */   public NonFlatSE getKernel()
/*  43:    */   {
/*  44: 79 */     double sigma = calculateSigma(this.waveLength.doubleValue(), this.bandwidth.doubleValue());
/*  45:    */     
/*  46: 81 */     DoubleImage dimage = new DoubleImage(this.width.intValue(), this.height.intValue(), 1);
/*  47: 82 */     dimage.fill(0.0D);
/*  48: 84 */     for (int x = -this.width.intValue() / 2; x <= this.width.intValue() / 2; x++) {
/*  49: 85 */       for (int y = -this.height.intValue() / 2; y <= this.height.intValue() / 2; y++)
/*  50:    */       {
/*  51: 86 */         double d = 0.0D;
/*  52: 87 */         for (double orientation : this.orientations)
/*  53:    */         {
/*  54: 88 */           double x1 = x * Math.cos(orientation) + y * Math.sin(orientation);
/*  55: 89 */           double y1 = -1 * x * Math.sin(orientation) + y * Math.cos(orientation);
/*  56: 90 */           d += (float)gaborFunction(x1, y1, sigma, this.aspectRatio.doubleValue(), this.waveLength.doubleValue(), this.phaseOffset.doubleValue());
/*  57:    */         }
/*  58: 93 */         dimage.setXYDouble(x + this.width.intValue() / 2, y + this.height.intValue() / 2, d);
/*  59:    */       }
/*  60:    */     }
/*  61: 97 */     float sum = 0.0F;
/*  62: 99 */     for (int x = 0; x < this.width.intValue(); x++) {
/*  63:100 */       for (int y = 0; y < this.height.intValue(); y++) {
/*  64:101 */         sum = (float)(sum + dimage.getXYDouble(x, y));
/*  65:    */       }
/*  66:    */     }
/*  67:105 */     sum /= this.width.intValue() * this.height.intValue();
/*  68:107 */     for (int x = 0; x < this.width.intValue(); x++) {
/*  69:108 */       for (int y = 0; y < this.height.intValue(); y++)
/*  70:    */       {
/*  71:109 */         double d = dimage.getXYDouble(x, y);
/*  72:110 */         dimage.setXYDouble(x, y, d - sum);
/*  73:    */       }
/*  74:    */     }
/*  75:114 */     return new NonFlatSE(new Point(this.width.intValue() / 2, this.height.intValue() / 2), dimage);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void execute()
/*  79:    */     throws GlobalException
/*  80:    */   {
/*  81:118 */     this.output = Convolution.invoke(this.input, getKernel());
/*  82:119 */     this.output = ContrastStretch.invoke(this.output);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static Image invoke(Image image, Double wavelength, double[] orientations, Double phaseOffset, Double aspectRatio, Double bandwidth, Integer width, Integer height)
/*  86:    */   {
/*  87:    */     try
/*  88:    */     {
/*  89:125 */       return (Image)new RotationInvariantGabor().preprocess(new Object[] { image, wavelength, orientations, phaseOffset, aspectRatio, bandwidth, width, height });
/*  90:    */     }
/*  91:    */     catch (GlobalException e)
/*  92:    */     {
/*  93:127 */       e.printStackTrace();
/*  94:    */     }
/*  95:128 */     return null;
/*  96:    */   }
/*  97:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.RotationInvariantGabor
 * JD-Core Version:    0.7.0.1
 */