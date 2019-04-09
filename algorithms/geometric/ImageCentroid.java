/*  1:   */ package vpt.algorithms.geometric;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ 
/*  9:   */ public class ImageCentroid
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Point output = null;
/* 13:18 */   public Image input = null;
/* 14:   */   
/* 15:   */   public ImageCentroid()
/* 16:   */   {
/* 17:22 */     this.inputFields = "input";
/* 18:23 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     int cdim = this.input.getCDim();
/* 25:29 */     int xdim = this.input.getXDim();
/* 26:30 */     int ydim = this.input.getYDim();
/* 27:32 */     if (cdim != 1) {
/* 28:32 */       throw new GlobalException("Sorry, only mono-channel for now...");
/* 29:   */     }
/* 30:34 */     double x_c = 0.0D;
/* 31:35 */     double y_c = 0.0D;
/* 32:37 */     for (int x = 0; x < xdim; x++) {
/* 33:38 */       for (int y = 0; y < ydim; y++)
/* 34:   */       {
/* 35:39 */         double p = this.input.getXYDouble(x, y);
/* 36:40 */         x_c += x * p;
/* 37:41 */         y_c += y * p;
/* 38:   */       }
/* 39:   */     }
/* 40:45 */     double volume = Tools.volume(this.input, 0);
/* 41:   */     
/* 42:47 */     this.output = new Point((int)(x_c / volume), (int)(y_c / volume));
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static Point invoke(Image image)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:53 */       return (Point)new ImageCentroid().preprocess(new Object[] { image });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:55 */       e.printStackTrace();
/* 54:   */     }
/* 55:56 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.ImageCentroid
 * JD-Core Version:    0.7.0.1
 */