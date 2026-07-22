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
/* 13:   */ public class ContourPointDistributionHistogram
/* 14:   */   extends Algorithm
/* 15:   */ {
/* 16:22 */   public double[] output = null;
/* 17:23 */   public Image input = null;
/* 18:24 */   public Integer slides = null;
/* 19:   */   
/* 20:   */   public ContourPointDistributionHistogram()
/* 21:   */   {
/* 22:27 */     this.inputFields = "input, slides";
/* 23:28 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:32 */     int cdim = this.input.getCDim();
/* 30:33 */     int ydim = this.input.getYDim();
/* 31:34 */     int xdim = this.input.getXDim();
/* 32:36 */     if (cdim != 1) {
/* 33:36 */       throw new GlobalException("Sorry, only monochannel images for now...");
/* 34:   */     }
/* 35:38 */     this.output = new double[this.slides.intValue()];
/* 36:   */     
/* 37:40 */     Point centroid = ImageCentroid.invoke(this.input);
/* 38:41 */     this.input = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 39:42 */     double totalVolume = Tools.volume(this.input, 0);
/* 40:   */     
/* 41:   */ 
/* 42:45 */     double maxDist = 0.0D;
/* 43:46 */     for (int y = 0; y < ydim; y++) {
/* 44:47 */       for (int x = 0; x < xdim; x++) {
/* 45:48 */         if (this.input.getXYBoolean(x, y))
/* 46:   */         {
/* 47:49 */           double dist = Distance.EuclideanDistance(centroid.x, centroid.y, x, y);
/* 48:50 */           if (dist > maxDist) {
/* 49:50 */             maxDist = dist;
/* 50:   */           }
/* 51:   */         }
/* 52:   */       }
/* 53:   */     }
/* 54:54 */     int slideWidth = (int)(maxDist / this.slides.intValue());
/* 55:57 */     for (int i = 0; i < this.slides.intValue(); i++) {
/* 56:59 */       for (int y = 0; y < ydim; y++) {
/* 57:60 */         for (int x = 0; x < xdim; x++) {
/* 58:61 */           if ((Distance.EuclideanDistance(x, y, centroid.x, centroid.y) < slideWidth * (i + 1)) && 
/* 59:62 */             (this.input.getXYBoolean(x, y))) {
/* 60:62 */             this.output[i] += 1.0D;
/* 61:   */           }
/* 62:   */         }
/* 63:   */       }
/* 64:   */     }
/* 65:68 */     for (int i = 0; i < this.output.length; i++) {
/* 66:69 */       this.output[i] /= totalVolume;
/* 67:   */     }
/* 68:   */   }
/* 69:   */   
/* 70:   */   public static double[] invoke(Image img, Integer slides)
/* 71:   */   {
/* 72:   */     try
/* 73:   */     {
/* 74:76 */       return (double[])new ContourPointDistributionHistogram().preprocess(new Object[] { img, slides });
/* 75:   */     }
/* 76:   */     catch (GlobalException e)
/* 77:   */     {
/* 78:78 */       e.printStackTrace();
/* 79:   */     }
/* 80:79 */     return null;
/* 81:   */   }
/* 82:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.ContourPointDistributionHistogram
 * JD-Core Version:    0.7.0.1
 */