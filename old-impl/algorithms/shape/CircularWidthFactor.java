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
/* 11:   */ public class CircularWidthFactor
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:23 */   public double[] output = null;
/* 15:24 */   public Image input = null;
/* 16:25 */   public Integer slides = null;
/* 17:   */   
/* 18:   */   public CircularWidthFactor()
/* 19:   */   {
/* 20:28 */     this.inputFields = "input, slides";
/* 21:29 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:33 */     int cdim = this.input.getCDim();
/* 28:34 */     int ydim = this.input.getYDim();
/* 29:35 */     int xdim = this.input.getXDim();
/* 30:37 */     if (cdim != 1) {
/* 31:37 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 32:   */     }
/* 33:39 */     this.output = new double[this.slides.intValue()];
/* 34:   */     
/* 35:41 */     Point centroid = ImageCentroid.invoke(this.input);
/* 36:42 */     double totalVolume = Tools.volume(this.input, 0);
/* 37:   */     
/* 38:   */ 
/* 39:45 */     int dist1 = Math.min(centroid.x, xdim - 1 - centroid.x);
/* 40:46 */     int dist2 = Math.min(centroid.y, ydim - 1 - centroid.y);
/* 41:47 */     int minDist = Math.min(dist1, dist2);
/* 42:48 */     int slideWidth = minDist / this.slides.intValue();
/* 43:52 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 44:   */     {
/* 45:54 */       double vol = 0.0D;
/* 46:56 */       for (int y = 0; y < ydim; y++) {
/* 47:57 */         for (int x = 0; x < xdim; x++) {
/* 48:58 */           if (Distance.EuclideanDistance(x, y, centroid.x, centroid.y) < slideWidth * (i + 1)) {
/* 49:59 */             vol += this.input.getXYDouble(x, y);
/* 50:   */           }
/* 51:   */         }
/* 52:   */       }
/* 53:63 */       this.output[i] = (vol / totalVolume);
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:   */   public static double[] invoke(Image img, Integer slides)
/* 58:   */   {
/* 59:   */     try
/* 60:   */     {
/* 61:70 */       return (double[])new CircularWidthFactor().preprocess(new Object[] { img, slides });
/* 62:   */     }
/* 63:   */     catch (GlobalException e)
/* 64:   */     {
/* 65:72 */       e.printStackTrace();
/* 66:   */     }
/* 67:73 */     return null;
/* 68:   */   }
/* 69:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.CircularWidthFactor
 * JD-Core Version:    0.7.0.1
 */