/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.ImageCentroid;
/*  8:   */ import vpt.algorithms.geometric.KeepLargestCC;
/*  9:   */ import vpt.algorithms.mm.binary.BInternGradient;
/* 10:   */ import vpt.algorithms.mm.binary.geo.BFillHole;
/* 11:   */ import vpt.util.se.FlatSE;
/* 12:   */ 
/* 13:   */ public class Dispersion
/* 14:   */   extends Algorithm
/* 15:   */ {
/* 16:21 */   public Double output = null;
/* 17:22 */   public Image input = null;
/* 18:   */   
/* 19:   */   public Dispersion()
/* 20:   */   {
/* 21:25 */     this.inputFields = "input";
/* 22:26 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:30 */     int cdim = this.input.getCDim();
/* 29:31 */     int xdim = this.input.getXDim();
/* 30:32 */     int ydim = this.input.getYDim();
/* 31:34 */     if (cdim != 1) {
/* 32:34 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 33:   */     }
/* 34:36 */     this.input = KeepLargestCC.invoke(this.input);
/* 35:37 */     this.input = BFillHole.invoke(this.input);
/* 36:   */     
/* 37:   */ 
/* 38:40 */     Point centroid = ImageCentroid.invoke(this.input);
/* 39:   */     
/* 40:   */ 
/* 41:43 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 42:   */     
/* 43:45 */     double max = 0.0D;
/* 44:46 */     double min = 1.7976931348623157E+308D;
/* 45:48 */     for (int x = 0; x < xdim; x++) {
/* 46:49 */       for (int y = 0; y < ydim; y++) {
/* 47:50 */         if (borderImage.getXYBoolean(x, y))
/* 48:   */         {
/* 49:52 */           double tmp = Math.sqrt((x - centroid.x) * (x - centroid.x) + (y - centroid.y) * (y - centroid.y));
/* 50:54 */           if (tmp > max) {
/* 51:54 */             max = tmp;
/* 52:   */           }
/* 53:55 */           if (tmp < min) {
/* 54:55 */             min = tmp;
/* 55:   */           }
/* 56:   */         }
/* 57:   */       }
/* 58:   */     }
/* 59:59 */     if (min != 0.0D) {
/* 60:59 */       this.output = Double.valueOf(max / min);
/* 61:   */     } else {
/* 62:60 */       this.output = Double.valueOf(0.0D);
/* 63:   */     }
/* 64:   */   }
/* 65:   */   
/* 66:   */   public static Double invoke(Image img)
/* 67:   */   {
/* 68:   */     try
/* 69:   */     {
/* 70:66 */       return (Double)new Dispersion().preprocess(new Object[] { img });
/* 71:   */     }
/* 72:   */     catch (GlobalException e)
/* 73:   */     {
/* 74:68 */       e.printStackTrace();
/* 75:   */     }
/* 76:69 */     return null;
/* 77:   */   }
/* 78:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.Dispersion
 * JD-Core Version:    0.7.0.1
 */