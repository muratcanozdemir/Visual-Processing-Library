/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.ImageCentroid;
/*  8:   */ import vpt.util.Distance;
/*  9:   */ import vpt.util.Tools;
/* 10:   */ 
/* 11:   */ public class MeanDistanceToCentroid
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:19 */   public Double output = null;
/* 15:20 */   public Image input = null;
/* 16:   */   
/* 17:   */   public MeanDistanceToCentroid()
/* 18:   */   {
/* 19:23 */     this.inputFields = "input";
/* 20:24 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:28 */     if (this.input.getCDim() != 1) {
/* 27:28 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 28:   */     }
/* 29:30 */     int ydim = this.input.getYDim();
/* 30:31 */     int xdim = this.input.getXDim();
/* 31:32 */     int size = xdim * ydim;
/* 32:   */     
/* 33:34 */     Point centroid = ImageCentroid.invoke(this.input);
/* 34:   */     
/* 35:36 */     this.output = Double.valueOf(0.0D);
/* 36:38 */     for (int y = 0; y < ydim; y++) {
/* 37:39 */       for (int x = 0; x < xdim; x++) {
/* 38:40 */         this.output = Double.valueOf(this.output.doubleValue() + this.input.getXYDouble(x, y) * Distance.EuclideanDistance(x, y, centroid.x, centroid.y));
/* 39:   */       }
/* 40:   */     }
/* 41:44 */     this.output = Double.valueOf(this.output.doubleValue() / Tools.volume(this.input, 0));
/* 42:   */   }
/* 43:   */   
/* 44:   */   public static Double invoke(Image img)
/* 45:   */   {
/* 46:   */     try
/* 47:   */     {
/* 48:50 */       return (Double)new MeanDistanceToCentroid().preprocess(new Object[] { img });
/* 49:   */     }
/* 50:   */     catch (GlobalException e)
/* 51:   */     {
/* 52:52 */       e.printStackTrace();
/* 53:   */     }
/* 54:53 */     return null;
/* 55:   */   }
/* 56:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.MeanDistanceToCentroid
 * JD-Core Version:    0.7.0.1
 */