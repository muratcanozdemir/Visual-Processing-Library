/*  1:   */ package vpt.algorithms.mm.binary.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.BooleanImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.point.Inversion;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class BFillHole
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image input = null;
/* 14:12 */   public Image output = null;
/* 15:   */   
/* 16:   */   public BFillHole()
/* 17:   */   {
/* 18:15 */     this.inputFields = "input";
/* 19:16 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:20 */     Image marker = new BooleanImage(this.input, false);
/* 26:   */     
/* 27:22 */     int xdim = marker.getXDim();
/* 28:23 */     int ydim = marker.getYDim();
/* 29:24 */     int cdim = marker.getCDim();
/* 30:26 */     for (int c = 0; c < cdim; c++) {
/* 31:27 */       for (int x = 0; x < xdim; x++) {
/* 32:28 */         for (int y = 0; y < ydim; y++) {
/* 33:32 */           if ((x == 0) || (y == 0) || (x == xdim - 1) || (y == ydim - 1)) {
/* 34:33 */             marker.setXYCBoolean(x, y, c, true);
/* 35:   */           } else {
/* 36:35 */             marker.setXYCBoolean(x, y, c, false);
/* 37:   */           }
/* 38:   */         }
/* 39:   */       }
/* 40:   */     }
/* 41:40 */     this.output = BReconstructionByDilation.invoke(marker, FlatSE.cross(3), Inversion.invoke(this.input));
/* 42:41 */     this.output = Inversion.invoke(this.output);
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static Image invoke(Image image)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:47 */       return (Image)new BFillHole().preprocess(new Object[] { image });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:49 */       e.printStackTrace();
/* 54:   */     }
/* 55:50 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.geo.BFillHole
 * JD-Core Version:    0.7.0.1
 */