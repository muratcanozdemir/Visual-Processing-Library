/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.BooleanImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ 
/*  9:   */ public class Threshold
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public BooleanImage output = null;
/* 13:11 */   public Image input = null;
/* 14:12 */   public Double threshold = null;
/* 15:   */   
/* 16:   */   public Threshold()
/* 17:   */   {
/* 18:15 */     this.inputFields = "input, threshold";
/* 19:16 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:21 */     this.output = new BooleanImage(this.input, false);
/* 26:22 */     int size = this.output.getSize();
/* 27:24 */     for (int i = 0; i < size; i++)
/* 28:   */     {
/* 29:25 */       double pixel = this.input.getDouble(i);
/* 30:27 */       if (Tools.cmpr(pixel, this.threshold.doubleValue()) >= 0) {
/* 31:28 */         this.output.setBoolean(i, true);
/* 32:   */       } else {
/* 33:30 */         this.output.setBoolean(i, false);
/* 34:   */       }
/* 35:   */     }
/* 36:   */   }
/* 37:   */   
/* 38:   */   public static BooleanImage invoke(Image image, double threshold)
/* 39:   */   {
/* 40:   */     try
/* 41:   */     {
/* 42:38 */       return (BooleanImage)new Threshold().preprocess(new Object[] { image, Double.valueOf(threshold) });
/* 43:   */     }
/* 44:   */     catch (GlobalException e)
/* 45:   */     {
/* 46:40 */       e.printStackTrace();
/* 47:   */     }
/* 48:41 */     return null;
/* 49:   */   }
/* 50:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.Threshold
 * JD-Core Version:    0.7.0.1
 */