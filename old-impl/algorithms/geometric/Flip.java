/*  1:   */ package vpt.algorithms.geometric;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Flip
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:14 */   public Image output = null;
/* 11:15 */   public Image input = null;
/* 12:16 */   public Integer direction = null;
/* 13:   */   public static final int horizontal = 0;
/* 14:   */   public static final int vertical = 1;
/* 15:   */   
/* 16:   */   public Flip()
/* 17:   */   {
/* 18:22 */     this.inputFields = "input, direction";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:27 */     int cdim = this.input.getCDim();
/* 26:28 */     int xdim = this.input.getXDim();
/* 27:29 */     int ydim = this.input.getYDim();
/* 28:   */     
/* 29:31 */     this.output = this.input.newInstance(false);
/* 30:33 */     if (this.direction.intValue() == 0) {
/* 31:35 */       for (int c = 0; c < cdim; c++) {
/* 32:36 */         for (int y = 0; y < ydim; y++) {
/* 33:37 */           for (int x = 0; x < xdim; x++) {
/* 34:38 */             this.output.setXYCByte(x, y, c, this.input.getXYCByte(xdim - 1 - x, y, c));
/* 35:   */           }
/* 36:   */         }
/* 37:   */       }
/* 38:43 */     } else if (this.direction.intValue() == 1) {
/* 39:45 */       for (int c = 0; c < cdim; c++) {
/* 40:46 */         for (int y = 0; y < ydim; y++) {
/* 41:47 */           for (int x = 0; x < xdim; x++) {
/* 42:48 */             this.output.setXYCByte(x, y, c, this.input.getXYCByte(x, ydim - 1 - y, c));
/* 43:   */           }
/* 44:   */         }
/* 45:   */       }
/* 46:   */     } else {
/* 47:53 */       throw new GlobalException("Invalid flipping direction");
/* 48:   */     }
/* 49:   */   }
/* 50:   */   
/* 51:   */   public static Image invoke(Image image, Integer direction)
/* 52:   */   {
/* 53:   */     try
/* 54:   */     {
/* 55:58 */       return (Image)new Flip().preprocess(new Object[] { image, direction });
/* 56:   */     }
/* 57:   */     catch (GlobalException e)
/* 58:   */     {
/* 59:60 */       e.printStackTrace();
/* 60:   */     }
/* 61:61 */     return null;
/* 62:   */   }
/* 63:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.Flip
 * JD-Core Version:    0.7.0.1
 */