/*  1:   */ package vpt.algorithms.mm.binary.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.BooleanImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.arithmetic.Subtraction;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class BClearBorderObjects
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image input = null;
/* 14:12 */   public Image output = null;
/* 15:   */   
/* 16:   */   public BClearBorderObjects()
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
/* 33:30 */           if ((x == 0) || (y == 0) || (x == xdim - 1) || (y == ydim - 1)) {
/* 34:31 */             marker.setXYCBoolean(x, y, c, this.input.getXYCBoolean(x, y, c));
/* 35:   */           } else {
/* 36:33 */             marker.setXYCBoolean(x, y, c, false);
/* 37:   */           }
/* 38:   */         }
/* 39:   */       }
/* 40:   */     }
/* 41:38 */     this.output = BReconstructionByDilation.invoke(marker, FlatSE.square(3), this.input);
/* 42:   */     
/* 43:40 */     this.output = Subtraction.invoke(this.input, this.output);
/* 44:   */   }
/* 45:   */   
/* 46:   */   public static Image invoke(Image image)
/* 47:   */   {
/* 48:   */     try
/* 49:   */     {
/* 50:46 */       return (Image)new BClearBorderObjects().preprocess(new Object[] { image });
/* 51:   */     }
/* 52:   */     catch (GlobalException e)
/* 53:   */     {
/* 54:48 */       e.printStackTrace();
/* 55:   */     }
/* 56:49 */     return null;
/* 57:   */   }
/* 58:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.geo.BClearBorderObjects
 * JD-Core Version:    0.7.0.1
 */