/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.ImageCentroid;
/*  8:   */ import vpt.algorithms.mm.binary.BInternGradient;
/*  9:   */ import vpt.util.Distance;
/* 10:   */ import vpt.util.Tools;
/* 11:   */ import vpt.util.se.FlatSE;
/* 12:   */ 
/* 13:   */ public class CircularWidthFactor3
/* 14:   */   extends Algorithm
/* 15:   */ {
/* 16:31 */   public double[] output = null;
/* 17:32 */   public Image input = null;
/* 18:33 */   public Integer slides = null;
/* 19:   */   
/* 20:   */   public CircularWidthFactor3()
/* 21:   */   {
/* 22:36 */     this.inputFields = "input, slides";
/* 23:37 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:41 */     int cdim = this.input.getCDim();
/* 30:42 */     int ydim = this.input.getYDim();
/* 31:43 */     int xdim = this.input.getXDim();
/* 32:45 */     if (cdim != 1) {
/* 33:45 */       throw new GlobalException("Sorry, only monochannel images for now...");
/* 34:   */     }
/* 35:47 */     this.output = new double[this.slides.intValue()];
/* 36:   */     
/* 37:49 */     Point centroid = ImageCentroid.invoke(this.input);
/* 38:50 */     Image contour = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 39:51 */     double totalVolume = Tools.volume(this.input, 0);
/* 40:   */     
/* 41:   */ 
/* 42:54 */     double maxDist = 0.0D;
/* 43:55 */     for (int y = 0; y < ydim; y++) {
/* 44:56 */       for (int x = 0; x < xdim; x++) {
/* 45:57 */         if (contour.getXYBoolean(x, y))
/* 46:   */         {
/* 47:58 */           double dist = Distance.EuclideanDistance(centroid.x, centroid.y, x, y);
/* 48:59 */           if (dist > maxDist) {
/* 49:59 */             maxDist = dist;
/* 50:   */           }
/* 51:   */         }
/* 52:   */       }
/* 53:   */     }
/* 54:63 */     int slideWidth = (int)(maxDist / this.slides.intValue());
/* 55:66 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 56:   */     {
/* 57:68 */       double vol = 0.0D;
/* 58:70 */       for (int y = 0; y < ydim; y++) {
/* 59:71 */         for (int x = 0; x < xdim; x++) {
/* 60:72 */           if (Distance.EuclideanDistance(x, y, centroid.x, centroid.y) < slideWidth * (i + 1)) {
/* 61:73 */             vol += this.input.getXYDouble(x, y);
/* 62:   */           }
/* 63:   */         }
/* 64:   */       }
/* 65:77 */       if (i == 0) {
/* 66:77 */         this.output[i] = (vol / totalVolume);
/* 67:   */       } else {
/* 68:78 */         this.output[i] = (vol / totalVolume - this.output[(i - 1)]);
/* 69:   */       }
/* 70:   */     }
/* 71:   */   }
/* 72:   */   
/* 73:   */   public static double[] invoke(Image img, Integer slides)
/* 74:   */   {
/* 75:   */     try
/* 76:   */     {
/* 77:85 */       return (double[])new CircularWidthFactor3().preprocess(new Object[] { img, slides });
/* 78:   */     }
/* 79:   */     catch (GlobalException e)
/* 80:   */     {
/* 81:87 */       e.printStackTrace();
/* 82:   */     }
/* 83:88 */     return null;
/* 84:   */   }
/* 85:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.CircularWidthFactor3
 * JD-Core Version:    0.7.0.1
 */