/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Distance;
/*  8:   */ 
/*  9:   */ public class EuclideanDistanceFromRGBColor
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Image input = null;
/* 13:19 */   public Double red = null;
/* 14:20 */   public Double green = null;
/* 15:21 */   public Double blue = null;
/* 16:23 */   public Image output = null;
/* 17:25 */   public Boolean nonlinear = null;
/* 18:   */   
/* 19:   */   public EuclideanDistanceFromRGBColor()
/* 20:   */   {
/* 21:28 */     this.inputFields = "input, red, green, blue, nonlinear";
/* 22:29 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:34 */     this.output = new DoubleImage(this.input.getXDim(), this.input.getYDim(), 1);
/* 29:   */     
/* 30:36 */     int xdim = this.input.getXDim();
/* 31:37 */     int ydim = this.input.getYDim();
/* 32:38 */     int cdim = this.input.getCDim();
/* 33:40 */     if (cdim != 3) {
/* 34:41 */       throw new GlobalException("The input must be a tristumulus RGB image");
/* 35:   */     }
/* 36:43 */     double[] p = new double[3];
/* 37:44 */     double[] q = new double[3];
/* 38:45 */     q[0] = this.red.doubleValue();
/* 39:46 */     q[1] = this.green.doubleValue();
/* 40:47 */     q[2] = this.blue.doubleValue();
/* 41:49 */     for (int x = 0; x < xdim; x++) {
/* 42:50 */       for (int y = 0; y < ydim; y++)
/* 43:   */       {
/* 44:51 */         double R = this.input.getXYCDouble(x, y, 0);
/* 45:52 */         double G = this.input.getXYCDouble(x, y, 1);
/* 46:53 */         double B = this.input.getXYCDouble(x, y, 2);
/* 47:   */         
/* 48:55 */         p[0] = R;
/* 49:56 */         p[1] = G;
/* 50:57 */         p[2] = B;
/* 51:   */         
/* 52:   */ 
/* 53:60 */         double distance = Distance.euclidean(p, q) / Math.sqrt(3.0D);
/* 54:62 */         if (this.nonlinear.booleanValue()) {
/* 55:62 */           distance = sigma(distance);
/* 56:   */         }
/* 57:64 */         this.output.setXYDouble(x, y, distance);
/* 58:   */       }
/* 59:   */     }
/* 60:   */   }
/* 61:   */   
/* 62:   */   private double sigma(double z)
/* 63:   */   {
/* 64:72 */     return 1.0D / (1.0D + Math.exp(-5.0D * (z - 0.5D)));
/* 65:   */   }
/* 66:   */   
/* 67:   */   public static Image invoke(Image image, Double red, Double green, Double blue, Boolean nonlinear)
/* 68:   */   {
/* 69:   */     try
/* 70:   */     {
/* 71:85 */       return (Image)new EuclideanDistanceFromRGBColor().preprocess(new Object[] { image, red, green, blue, nonlinear });
/* 72:   */     }
/* 73:   */     catch (GlobalException e)
/* 74:   */     {
/* 75:87 */       e.printStackTrace();
/* 76:   */     }
/* 77:88 */     return null;
/* 78:   */   }
/* 79:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.EuclideanDistanceFromRGBColor
 * JD-Core Version:    0.7.0.1
 */