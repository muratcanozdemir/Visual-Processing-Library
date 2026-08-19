/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.KeepLargestCC;
/*  8:   */ import vpt.algorithms.mm.binary.BInternGradient;
/*  9:   */ import vpt.algorithms.mm.binary.geo.BFillHole;
/* 10:   */ import vpt.util.se.FlatSE;
/* 11:   */ 
/* 12:   */ public class EdgeBackForeRatioHistogram
/* 13:   */   extends Algorithm
/* 14:   */ {
/* 15:25 */   public double[] output = null;
/* 16:26 */   public Image input = null;
/* 17:27 */   public Integer bins = null;
/* 18:28 */   public Integer windowRadius = null;
/* 19:   */   
/* 20:   */   public EdgeBackForeRatioHistogram()
/* 21:   */   {
/* 22:31 */     this.inputFields = "input, bins, windowRadius";
/* 23:32 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:37 */     if (this.input.getCDim() != 1) {
/* 30:37 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 31:   */     }
/* 32:39 */     int xdim = this.input.getXDim();
/* 33:40 */     int ydim = this.input.getYDim();
/* 34:   */     
/* 35:42 */     this.input = KeepLargestCC.invoke(this.input);
/* 36:43 */     this.input = BFillHole.invoke(this.input);
/* 37:   */     
/* 38:   */ 
/* 39:46 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 40:   */     
/* 41:48 */     this.output = new double[this.bins.intValue()];
/* 42:   */     
/* 43:50 */     FlatSE mask = FlatSE.circle(this.windowRadius.intValue());
/* 44:51 */     Point[] coords = mask.getCoords();
/* 45:   */     
/* 46:53 */     int size = 0;
/* 47:56 */     for (int x = 0; x < xdim; x++) {
/* 48:57 */       for (int y = 0; y < ydim; y++) {
/* 49:58 */         if (borderImage.getXYBoolean(x, y))
/* 50:   */         {
/* 51:60 */           double foreground = 0.0D;
/* 52:63 */           for (int i = 0; i < coords.length; i++)
/* 53:   */           {
/* 54:65 */             int _x = x - coords[i].x;
/* 55:66 */             int _y = y - coords[i].y;
/* 56:68 */             if ((_x >= 0) && (_y >= 0) && (_x < xdim) && (_y < ydim)) {
/* 57:71 */               if (this.input.getXYBoolean(_x, _y)) {
/* 58:71 */                 foreground += 1.0D;
/* 59:   */               }
/* 60:   */             }
/* 61:   */           }
/* 62:74 */           double ratio = foreground / coords.length;
/* 63:75 */           size++;
/* 64:76 */           this.output[((int)java.lang.Math.round(ratio * (this.bins.intValue() - 1)))] += 1.0D;
/* 65:   */         }
/* 66:   */       }
/* 67:   */     }
/* 68:80 */     for (int i = 0; i < this.output.length; i++) {
/* 69:81 */       this.output[i] /= size;
/* 70:   */     }
/* 71:   */   }
/* 72:   */   
/* 73:   */   public static double[] invoke(Image img, Integer bins, Integer windowRadius)
/* 74:   */   {
/* 75:   */     try
/* 76:   */     {
/* 77:87 */       return (double[])new EdgeBackForeRatioHistogram().preprocess(new Object[] { img, bins, windowRadius });
/* 78:   */     }
/* 79:   */     catch (GlobalException e)
/* 80:   */     {
/* 81:89 */       e.printStackTrace();
/* 82:   */     }
/* 83:90 */     return null;
/* 84:   */   }
/* 85:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.EdgeBackForeRatioHistogram
 * JD-Core Version:    0.7.0.1
 */