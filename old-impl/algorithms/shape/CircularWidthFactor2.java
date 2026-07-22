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
/* 11:   */ public class CircularWidthFactor2
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:25 */   public double[] output = null;
/* 15:26 */   public Image input = null;
/* 16:27 */   public Integer slides = null;
/* 17:   */   
/* 18:   */   public CircularWidthFactor2()
/* 19:   */   {
/* 20:30 */     this.inputFields = "input, slides";
/* 21:31 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:35 */     int cdim = this.input.getCDim();
/* 28:36 */     int ydim = this.input.getYDim();
/* 29:37 */     int xdim = this.input.getXDim();
/* 30:39 */     if (cdim != 1) {
/* 31:39 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 32:   */     }
/* 33:41 */     this.output = new double[this.slides.intValue()];
/* 34:   */     
/* 35:43 */     Point centroid = ImageCentroid.invoke(this.input);
/* 36:44 */     double totalVolume = Tools.volume(this.input, 0);
/* 37:   */     
/* 38:   */ 
/* 39:47 */     int dist1 = Math.min(centroid.x, xdim - 1 - centroid.x);
/* 40:48 */     int dist2 = Math.min(centroid.y, ydim - 1 - centroid.y);
/* 41:49 */     int minDist = Math.min(dist1, dist2);
/* 42:50 */     int slideWidth = minDist / this.slides.intValue();
/* 43:54 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 44:   */     {
/* 45:56 */       double vol = 0.0D;
/* 46:58 */       for (int y = 0; y < ydim; y++) {
/* 47:59 */         for (int x = 0; x < xdim; x++) {
/* 48:60 */           if (Distance.EuclideanDistance(x, y, centroid.x, centroid.y) < slideWidth * (i + 1)) {
/* 49:61 */             vol += this.input.getXYDouble(x, y);
/* 50:   */           }
/* 51:   */         }
/* 52:   */       }
/* 53:65 */       if (i == 0) {
/* 54:65 */         this.output[i] = (vol / totalVolume);
/* 55:   */       } else {
/* 56:66 */         this.output[i] = (vol / totalVolume - this.output[(i - 1)]);
/* 57:   */       }
/* 58:   */     }
/* 59:   */   }
/* 60:   */   
/* 61:   */   public static double[] invoke(Image img, Integer slides)
/* 62:   */   {
/* 63:   */     try
/* 64:   */     {
/* 65:73 */       return (double[])new CircularWidthFactor2().preprocess(new Object[] { img, slides });
/* 66:   */     }
/* 67:   */     catch (GlobalException e)
/* 68:   */     {
/* 69:75 */       e.printStackTrace();
/* 70:   */     }
/* 71:76 */     return null;
/* 72:   */   }
/* 73:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.CircularWidthFactor2
 * JD-Core Version:    0.7.0.1
 */