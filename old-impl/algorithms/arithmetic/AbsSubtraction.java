/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ /*  5:   */ import vpt.Image;
/*  4:   */ 
/*  6:   */ 
/*  7:   */ public class AbsSubtraction
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:17 */   public Image output = null;
/* 11:18 */   public Image inputImage1 = null;
/* 12:19 */   public Image inputImage2 = null;
/* 13:   */   
/* 14:   */   public AbsSubtraction()
/* 15:   */   {
/* 16:22 */     this.inputFields = "inputImage1,inputImage2";
/* 17:23 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:28 */     if (!this.inputImage1.hasSameDimensionsAs(this.inputImage2)) {
/* 24:29 */       throw new GlobalException("Arguments must be of same dimensions!");
/* 25:   */     }
/* 26:31 */     this.output = this.inputImage1.newInstance(false);
/* 27:   */     
/* 28:33 */     int size = this.output.getSize();
/* 29:35 */     for (int i = 0; i < size; i++)
/* 30:   */     {
/* 31:36 */       double p1 = this.inputImage1.getDouble(i);
/* 32:37 */       double p2 = this.inputImage2.getDouble(i);
/* 33:   */       
/* 34:39 */       this.output.setDouble(i, Math.abs(p1 - p2));
/* 35:   */     }
/* 36:   */   }
/* 37:   */   
/* 38:   */   public static Image invoke(Image image1, Image image2)
/* 39:   */   {
/* 40:   */     try
/* 41:   */     {
/* 42:52 */       return (Image)new AbsSubtraction().preprocess(new Object[] { image1, image2 });
/* 43:   */     }
/* 44:   */     catch (GlobalException e)
/* 45:   */     {
/* 46:54 */       e.printStackTrace();
/* 47:   */     }
/* 48:55 */     return null;
/* 49:   */   }
/* 50:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.AbsSubtraction
 * JD-Core Version:    0.7.0.1
 */