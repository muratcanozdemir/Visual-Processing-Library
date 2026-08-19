/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class NumberOfSingletons
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:17 */   public Integer output = Integer.valueOf(0);
/* 11:18 */   public Image labels = null;
/* 12:   */   private int xdim;
/* 13:   */   private int ydim;
/* 14:   */   
/* 15:   */   public NumberOfSingletons()
/* 16:   */   {
/* 17:24 */     this.inputFields = "labels";
/* 18:25 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:30 */     this.xdim = this.labels.getXDim();
/* 25:31 */     this.ydim = this.labels.getYDim();
/* 26:32 */     int cdim = this.labels.getCDim();
/* 27:34 */     if (cdim != 1) {
/* 28:34 */       throw new GlobalException("A label image cannot have more than one channel!");
/* 29:   */     }
/* 30:36 */     for (int y = 0; y < this.ydim; y++) {
/* 31:37 */       for (int x = 0; x < this.xdim; x++) {
/* 32:39 */         if (isSingle(x, y, this.labels)) {
/* 33:39 */           this.output = Integer.valueOf(this.output.intValue() + 1);
/* 34:   */         }
/* 35:   */       }
/* 36:   */     }
/* 37:   */   }
/* 38:   */   
/* 39:   */   private boolean isSingle(int x, int y, Image img)
/* 40:   */   {
/* 41:46 */     int p = img.getXYInt(x, y);
/* 42:48 */     for (int _x = x - 1; _x <= x + 1; _x++) {
/* 43:49 */       for (int _y = y - 1; _y <= y + 1; _y++) {
/* 44:50 */         if (((_x != x) || (_y != y)) && 
/* 45:51 */           (_x >= 0) && (_y >= 0) && (_x <= this.xdim - 1) && (_y <= this.ydim - 1))
/* 46:   */         {
/* 47:53 */           int q = img.getXYInt(_x, _y);
/* 48:55 */           if (p == q) {
/* 49:55 */             return false;
/* 50:   */           }
/* 51:   */         }
/* 52:   */       }
/* 53:   */     }
/* 54:60 */     return true;
/* 55:   */   }
/* 56:   */   
/* 57:   */   public static Integer invoke(Image labels)
/* 58:   */   {
/* 59:   */     try
/* 60:   */     {
/* 61:65 */       return (Integer)new NumberOfSingletons().preprocess(new Object[] { labels });
/* 62:   */     }
/* 63:   */     catch (GlobalException e)
/* 64:   */     {
/* 65:67 */       e.printStackTrace();
/* 66:   */     }
/* 67:68 */     return null;
/* 68:   */   }
/* 69:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.NumberOfSingletons
 * JD-Core Version:    0.7.0.1
 */