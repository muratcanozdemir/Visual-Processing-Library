/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class BorderToDominantNeighborLabel
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:   */   public Image labelImage;
/* 11:   */   public Image outputImage;
/* 12:24 */   private final int WSHED = 0;
/* 13:   */   
/* 14:   */   public BorderToDominantNeighborLabel()
/* 15:   */   {
/* 16:27 */     this.inputFields = "labelImage";
/* 17:28 */     this.outputFields = "outputImage";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:32 */     this.outputImage = this.labelImage.newInstance(true);
/* 24:   */     
/* 25:34 */     int xDim = this.outputImage.getXDim();
/* 26:35 */     int yDim = this.outputImage.getYDim();
/* 27:37 */     if (this.outputImage.getCDim() != 1) {
/* 28:37 */       throw new GlobalException("The label image must be in grayscale...");
/* 29:   */     }
/* 30:40 */     for (int x = 0; x < xDim; x++) {
/* 31:41 */       for (int y = 0; y < yDim; y++)
/* 32:   */       {
/* 33:42 */         int label = this.outputImage.getXYInt(x, y);
/* 34:45 */         if (label == 0)
/* 35:   */         {
/* 36:51 */           boolean flag = true;
/* 37:53 */           for (int _x = x - 1; (_x <= x + 1) && (flag); _x++) {
/* 38:54 */             for (int _y = y - 1; (_y <= y + 1) && (flag); _y++) {
/* 39:56 */               if ((_x >= 0) && (_y >= 0) && (_x < xDim) && (_y < yDim))
/* 40:   */               {
/* 41:58 */                 int q = this.labelImage.getXYInt(_x, _y);
/* 42:60 */                 if (q != 0)
/* 43:   */                 {
/* 44:61 */                   this.outputImage.setXYInt(x, y, q);
/* 45:62 */                   flag = false;
/* 46:   */                 }
/* 47:   */               }
/* 48:   */             }
/* 49:   */           }
/* 50:   */         }
/* 51:   */       }
/* 52:   */     }
/* 53:   */   }
/* 54:   */   
/* 55:   */   public static Image invoke(Image labels)
/* 56:   */   {
/* 57:   */     try
/* 58:   */     {
/* 59:74 */       return (Image)new BorderToDominantNeighborLabel().preprocess(new Object[] { labels });
/* 60:   */     }
/* 61:   */     catch (GlobalException e)
/* 62:   */     {
/* 63:76 */       e.printStackTrace();
/* 64:   */     }
/* 65:77 */     return null;
/* 66:   */   }
/* 67:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.BorderToDominantNeighborLabel
 * JD-Core Version:    0.7.0.1
 */