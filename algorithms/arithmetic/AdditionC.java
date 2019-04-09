/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ /*  5:   */ import vpt.Image;
/*  4:   */ 
/*  6:   */ 
/*  7:   */ public class AdditionC
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:16 */   public Image inputImage = null;
/* 11:17 */   public Image output = null;
/* 12:18 */   public Double constant = null;
/* 13:   */   
/* 14:   */   public AdditionC()
/* 15:   */   {
/* 16:21 */     this.inputFields = "inputImage,constant";
/* 17:22 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:27 */     this.output = this.inputImage.newInstance(false);
/* 24:   */     
/* 25:29 */     int size = this.output.getSize();
/* 26:31 */     for (int i = 0; i < size; i++)
/* 27:   */     {
/* 28:32 */       double p = this.inputImage.getDouble(i);
/* 29:   */       
/* 30:34 */       this.output.setDouble(i, Math.min(1.0D, p + this.constant.doubleValue()));
/* 31:   */     }
/* 32:   */   }
/* 33:   */   
/* 34:   */   public static Image invoke(Image image, Double constant)
/* 35:   */   {
/* 36:   */     try
/* 37:   */     {
/* 38:48 */       return (Image)new AdditionC().preprocess(new Object[] { image, constant });
/* 39:   */     }
/* 40:   */     catch (GlobalException e)
/* 41:   */     {
/* 42:50 */       e.printStackTrace();
/* 43:   */     }
/* 44:51 */     return null;
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.AdditionC
 * JD-Core Version:    0.7.0.1
 */