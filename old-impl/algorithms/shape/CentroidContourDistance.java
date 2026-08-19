/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.BooleanImage;
/*  7:   */ import vpt.GlobalException;
/*  8:   */ import vpt.Image;
/*  9:   */ import vpt.algorithms.geometric.ImageCentroid;
/* 10:   */ import vpt.algorithms.mm.binary.BInternGradient;
/* 11:   */ import vpt.util.Distance;
/* 12:   */ import vpt.util.se.FlatSE;
/* 13:   */ 
/* 14:   */ public class CentroidContourDistance
/* 15:   */   extends Algorithm
/* 16:   */ {
/* 17:26 */   public double[] output = null;
/* 18:27 */   public Image input = null;
/* 19:   */   
/* 20:   */   public CentroidContourDistance()
/* 21:   */   {
/* 22:30 */     this.inputFields = "input";
/* 23:31 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:35 */     int cdim = this.input.getCDim();
/* 30:36 */     int ydim = this.input.getYDim();
/* 31:37 */     int xdim = this.input.getXDim();
/* 32:39 */     if (cdim != 1) {
/* 33:39 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 34:   */     }
/* 35:46 */     Point centroid = ImageCentroid.invoke(this.input);
/* 36:   */     
/* 37:   */ 
/* 38:49 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 39:   */     
/* 40:   */ 
/* 41:   */ 
/* 42:53 */     ArrayList<Double> borders = new ArrayList();
/* 43:   */     
/* 44:55 */     BooleanImage mask = new BooleanImage(this.input, false);
/* 45:56 */     mask.fill(false);
/* 46:58 */     for (int x = 0; x < xdim; x++) {
/* 47:59 */       for (int y = 0; y < ydim; y++)
/* 48:   */       {
/* 49:60 */         boolean r = borderImage.getXYBoolean(x, y);
/* 50:61 */         if (r)
/* 51:   */         {
/* 52:63 */           boolean m = mask.getXYBoolean(x, y);
/* 53:64 */           if (!m)
/* 54:   */           {
/* 55:67 */             Point p = new Point(x, y);
/* 56:   */             for (;;)
/* 57:   */             {
/* 58:70 */               mask.setXYBoolean(p.x, p.y, true);
/* 59:71 */               borders.add(Double.valueOf(Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y)));
/* 60:74 */               if ((p.x + 1 < xdim) && (borderImage.getXYBoolean(p.x + 1, p.y)) && (!mask.getXYBoolean(p.x + 1, p.y)))
/* 61:   */               {
/* 62:75 */                 p.x += 1;
/* 63:   */               }
/* 64:76 */               else if ((p.y + 1 < ydim) && (borderImage.getXYBoolean(p.x, p.y + 1)) && (!mask.getXYBoolean(p.x, p.y + 1)))
/* 65:   */               {
/* 66:77 */                 p.y += 1;
/* 67:   */               }
/* 68:78 */               else if ((p.x - 1 >= 0) && (borderImage.getXYBoolean(p.x - 1, p.y)) && (!mask.getXYBoolean(p.x - 1, p.y)))
/* 69:   */               {
/* 70:79 */                 p.x -= 1;
/* 71:   */               }
/* 72:   */               else
/* 73:   */               {
/* 74:80 */                 if ((p.y - 1 < 0) || (!borderImage.getXYBoolean(p.x, p.y - 1)) || (mask.getXYBoolean(p.x, p.y - 1))) {
/* 75:   */                   break;
/* 76:   */                 }
/* 77:81 */                 p.y -= 1;
/* 78:   */               }
/* 79:   */             }
/* 80:   */           }
/* 81:   */         }
/* 82:   */       }
/* 83:   */     }
/* 84:   */   }
/* 85:   */   
/* 86:   */   public static double[] invoke(Image img, Integer length)
/* 87:   */   {
/* 88:   */     try
/* 89:   */     {
/* 90:92 */       return (double[])new CentroidContourDistance().preprocess(new Object[] { img, length });
/* 91:   */     }
/* 92:   */     catch (GlobalException e)
/* 93:   */     {
/* 94:94 */       e.printStackTrace();
/* 95:   */     }
/* 96:95 */     return null;
/* 97:   */   }
/* 98:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.CentroidContourDistance
 * JD-Core Version:    0.7.0.1
 */