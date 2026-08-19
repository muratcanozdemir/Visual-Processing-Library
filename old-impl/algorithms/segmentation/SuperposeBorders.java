/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class SuperposeBorders
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:   */   public Image labelImage;
/* 11:   */   public Image baseImage;
/* 12:   */   public Image outputImage;
/* 13:   */   public int[] color;
/* 14:24 */   private final int WSHED = 0;
/* 15:   */   
/* 16:   */   public SuperposeBorders()
/* 17:   */   {
/* 18:27 */     this.inputFields = "labelImage,baseImage,color";
/* 19:28 */     this.outputFields = "outputImage";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:32 */     this.outputImage = this.baseImage.newInstance(true);
/* 26:   */     
/* 27:34 */     int xDim = this.outputImage.getXDim();
/* 28:35 */     int yDim = this.outputImage.getYDim();
/* 29:36 */     int cDim = this.outputImage.getCDim();
/* 30:38 */     if (cDim > 3) {
/* 31:38 */       throw new GlobalException("Only color or gray images can be processed.");
/* 32:   */     }
/* 33:39 */     if (cDim != this.color.length) {
/* 34:39 */       throw new GlobalException("Color dimensions do not correspond to the original image's channel number.");
/* 35:   */     }
/* 36:42 */     for (int x = 0; x < xDim; x++) {
/* 37:43 */       for (int y = 0; y < yDim; y++)
/* 38:   */       {
/* 39:44 */         int label = this.labelImage.getXYInt(x, y);
/* 40:47 */         if (label == 0) {
/* 41:48 */           for (int c = 0; c < cDim; c++) {
/* 42:49 */             this.outputImage.setXYCByte(x, y, c, this.color[c]);
/* 43:   */           }
/* 44:   */         }
/* 45:   */       }
/* 46:   */     }
/* 47:   */   }
/* 48:   */   
/* 49:   */   public static Image invoke(Image labels, Image original, int[] color)
/* 50:   */   {
/* 51:   */     try
/* 52:   */     {
/* 53:58 */       return (Image)new SuperposeBorders().preprocess(new Object[] { labels, original, color });
/* 54:   */     }
/* 55:   */     catch (GlobalException e)
/* 56:   */     {
/* 57:60 */       e.printStackTrace();
/* 58:   */     }
/* 59:61 */     return null;
/* 60:   */   }
/* 61:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.SuperposeBorders
 * JD-Core Version:    0.7.0.1
 */