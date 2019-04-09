/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ 
/*  9:   */ public class DistanceFromHSYColor
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:18 */   public Image input = null;
/* 13:19 */   public Double red = null;
/* 14:20 */   public Double green = null;
/* 15:21 */   public Double blue = null;
/* 16:22 */   public Image output = null;
/* 17:23 */   public Boolean nonlinear = null;
/* 18:   */   
/* 19:   */   public DistanceFromHSYColor()
/* 20:   */   {
/* 21:26 */     this.inputFields = "input, red, green, blue";
/* 22:27 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:32 */     this.output = new DoubleImage(this.input.getXDim(), this.input.getYDim(), 1);
/* 29:   */     
/* 30:34 */     int xdim = this.input.getXDim();
/* 31:35 */     int ydim = this.input.getYDim();
/* 32:36 */     int cdim = this.input.getCDim();
/* 33:38 */     if (cdim != 3) {
/* 34:38 */       throw new GlobalException("The input must be a tristumulus color HSY image");
/* 35:   */     }
/* 36:40 */     double[] ref = RGB2HSY.convert(this.red.doubleValue(), this.green.doubleValue(), this.blue.doubleValue());
/* 37:42 */     for (int x = 0; x < xdim; x++) {
/* 38:43 */       for (int y = 0; y < ydim; y++)
/* 39:   */       {
/* 40:44 */         double h = this.input.getXYCDouble(x, y, 0);
/* 41:45 */         double s = this.input.getXYCDouble(x, y, 1);
/* 42:46 */         double l = this.input.getXYCDouble(x, y, 2);
/* 43:   */         
/* 44:48 */         double hueDist = Tools.hueDistance(h, ref[0]);
/* 45:49 */         double lumDist = Math.abs(l - ref[2]);
/* 46:   */         double distance;
/* 47:   */         double distance;
/* 48:54 */         if ((l <= 0.1D) || (s <= 0.1D)) {
/* 49:54 */           distance = 1.0D;
/* 50:   */         } else {
/* 51:56 */           distance = hueDist * (1.0D - sigma(s)) + lumDist * sigma(s);
/* 52:   */         }
/* 53:59 */         this.output.setXYDouble(x, y, distance);
/* 54:   */       }
/* 55:   */     }
/* 56:   */   }
/* 57:   */   
/* 58:   */   private double sigma(double z)
/* 59:   */   {
/* 60:66 */     return 1.0D / (1.0D + Math.exp(-10.0D * (z - 0.35D)));
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static Image invoke(Image image, Double red, Double green, Double blue)
/* 64:   */   {
/* 65:   */     try
/* 66:   */     {
/* 67:72 */       return (Image)new DistanceFromHSYColor().preprocess(new Object[] { image, red, green, blue });
/* 68:   */     }
/* 69:   */     catch (GlobalException e)
/* 70:   */     {
/* 71:74 */       e.printStackTrace();
/* 72:   */     }
/* 73:75 */     return null;
/* 74:   */   }
/* 75:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.DistanceFromHSYColor
 * JD-Core Version:    0.7.0.1
 */