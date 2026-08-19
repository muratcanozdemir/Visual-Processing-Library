/*  1:   */ package vpt.algorithms.conversion;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Distance;
/*  8:   */ 
/*  9:   */ public class EuclideanDistanceFromLABColor
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:21 */   public Image input = null;
/* 13:23 */   public Double red = null;
/* 14:24 */   public Double green = null;
/* 15:25 */   public Double blue = null;
/* 16:27 */   public Image output = null;
/* 17:   */   
/* 18:   */   public EuclideanDistanceFromLABColor()
/* 19:   */   {
/* 20:30 */     this.inputFields = "input, red, green, blue";
/* 21:31 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:36 */     this.output = new DoubleImage(this.input.getXDim(), this.input.getYDim(), 1);
/* 28:   */     
/* 29:38 */     int xdim = this.input.getXDim();
/* 30:39 */     int ydim = this.input.getYDim();
/* 31:40 */     int cdim = this.input.getCDim();
/* 32:42 */     if (cdim != 3) {
/* 33:42 */       throw new GlobalException("The input must be a tristumulus RGB image");
/* 34:   */     }
/* 35:44 */     double[] q = RGB2XYZ.convert((int)(this.red.doubleValue() * 255.0D), (int)(this.green.doubleValue() * 255.0D), (int)(this.blue.doubleValue() * 255.0D));
/* 36:45 */     q = XYZ2LAB.convert(q[0], q[1], q[2]);
/* 37:46 */     q[0] = (q[0] * 2.55D / 255.0D);
/* 38:47 */     q[1] = ((q[1] + 128.0D) / 255.0D);
/* 39:48 */     q[2] = ((q[2] + 128.0D) / 255.0D);
/* 40:   */     
/* 41:50 */     double refHue = RGB2HSY.convert(this.red.doubleValue(), this.green.doubleValue(), this.blue.doubleValue())[0];
/* 42:   */     
/* 43:   */ 
/* 44:   */ 
/* 45:54 */     this.input = RGB2XYZ.invoke(this.input);
/* 46:55 */     this.input = XYZ2LAB.invoke(this.input);
/* 47:56 */     this.input = XYZ2LAB.scaleToByte(this.input);
/* 48:58 */     for (int x = 0; x < xdim; x++) {
/* 49:59 */       for (int y = 0; y < ydim; y++)
/* 50:   */       {
/* 51:60 */         double[] p = this.input.getVXYDouble(x, y);
/* 52:   */         
/* 53:   */ 
/* 54:63 */         double distance = Distance.euclidean(p, q) / Math.sqrt(3.0D);
/* 55:   */         
/* 56:65 */         this.output.setXYDouble(x, y, distance);
/* 57:   */       }
/* 58:   */     }
/* 59:   */   }
/* 60:   */   
/* 61:   */   public static Image invoke(Image image, Double red, Double green, Double blue)
/* 62:   */   {
/* 63:   */     try
/* 64:   */     {
/* 65:81 */       return (Image)new EuclideanDistanceFromLABColor().preprocess(new Object[] { image, red, green, blue });
/* 66:   */     }
/* 67:   */     catch (GlobalException e)
/* 68:   */     {
/* 69:83 */       e.printStackTrace();
/* 70:   */     }
/* 71:84 */     return null;
/* 72:   */   }
/* 73:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.EuclideanDistanceFromLABColor
 * JD-Core Version:    0.7.0.1
 */