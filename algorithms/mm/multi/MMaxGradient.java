/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.mm.gray.GGradient;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MMaxGradient
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Image output = null;
/* 14:18 */   public Image input = null;
/* 15:19 */   public FlatSE se = null;
/* 16:   */   
/* 17:   */   public MMaxGradient()
/* 18:   */   {
/* 19:22 */     this.inputFields = "input,se";
/* 20:23 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:27 */     Image gradient = GGradient.invoke(this.input, this.se);
/* 27:   */     
/* 28:29 */     int xdim = this.input.getXDim();
/* 29:30 */     int ydim = this.input.getYDim();
/* 30:31 */     int cdim = this.input.getCDim();
/* 31:   */     
/* 32:33 */     this.output = new ByteImage(xdim, ydim, 1);
/* 33:35 */     for (int y = 0; y < ydim; y++) {
/* 34:36 */       for (int x = 0; x < xdim; x++)
/* 35:   */       {
/* 36:38 */         int max = 0;
/* 37:40 */         for (int c = 0; c < cdim; c++)
/* 38:   */         {
/* 39:41 */           int tmp = gradient.getXYCByte(x, y, c);
/* 40:43 */           if (tmp > max) {
/* 41:43 */             max = tmp;
/* 42:   */           }
/* 43:   */         }
/* 44:46 */         this.output.setXYByte(x, y, max);
/* 45:   */       }
/* 46:   */     }
/* 47:   */   }
/* 48:   */   
/* 49:   */   public static Image invoke(Image image, FlatSE se)
/* 50:   */   {
/* 51:   */     try
/* 52:   */     {
/* 53:56 */       return (Image)new MMaxGradient().preprocess(new Object[] { image, se });
/* 54:   */     }
/* 55:   */     catch (GlobalException e)
/* 56:   */     {
/* 57:58 */       e.printStackTrace();
/* 58:   */     }
/* 59:59 */     return null;
/* 60:   */   }
/* 61:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MMaxGradient
 * JD-Core Version:    0.7.0.1
 */