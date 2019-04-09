/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.point.Inversion;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class GFillHole
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public Image inputImage = null;
/* 13:11 */   public Image output = null;
/* 14:   */   
/* 15:   */   public GFillHole()
/* 16:   */   {
/* 17:14 */     this.inputFields = "inputImage";
/* 18:15 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:19 */     Image marker = this.inputImage.newInstance(false);
/* 25:   */     
/* 26:21 */     int xdim = marker.getXDim();
/* 27:22 */     int ydim = marker.getYDim();
/* 28:23 */     int cdim = marker.getCDim();
/* 29:   */     
/* 30:   */ 
/* 31:26 */     Image rmin = GRegionalMinima.invoke(this.inputImage);
/* 32:29 */     for (int i = 0; i < rmin.getSize(); i++) {
/* 33:30 */       if (rmin.getByte(i) > 0) {
/* 34:30 */         this.inputImage.setByte(i, 0);
/* 35:   */       }
/* 36:   */     }
/* 37:33 */     for (int c = 0; c < cdim; c++) {
/* 38:34 */       for (int x = 0; x < xdim; x++) {
/* 39:35 */         for (int y = 0; y < ydim; y++)
/* 40:   */         {
/* 41:37 */           int p = this.inputImage.getXYCByte(x, y, c);
/* 42:39 */           if ((x == 0) || (y == 0) || (x == xdim - 1) || (y == ydim - 1))
/* 43:   */           {
/* 44:43 */             if (p > 0) {
/* 45:44 */               marker.setXYCByte(x, y, c, 255 - p);
/* 46:   */             } else {
/* 47:46 */               marker.setXYCByte(x, y, c, p);
/* 48:   */             }
/* 49:   */           }
/* 50:   */           else {
/* 51:48 */             marker.setXYCByte(x, y, c, 0);
/* 52:   */           }
/* 53:   */         }
/* 54:   */       }
/* 55:   */     }
/* 56:53 */     this.output = GReconstructionByDilation.invoke(marker, FlatSE.square(5), Inversion.invoke(this.inputImage));
/* 57:54 */     this.output = Inversion.invoke(this.output);
/* 58:   */   }
/* 59:   */   
/* 60:   */   public static Image invoke(Image image)
/* 61:   */   {
/* 62:   */     try
/* 63:   */     {
/* 64:60 */       return (Image)new GFillHole().preprocess(new Object[] { image });
/* 65:   */     }
/* 66:   */     catch (GlobalException e)
/* 67:   */     {
/* 68:62 */       e.printStackTrace();
/* 69:   */     }
/* 70:63 */     return null;
/* 71:   */   }
/* 72:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GFillHole
 * JD-Core Version:    0.7.0.1
 */