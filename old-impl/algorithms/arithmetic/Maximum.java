/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ /*  5:   */ import vpt.Image;
/*  4:   */ 
/*  6:   */ 
/*  7:   */ public class Maximum
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:13 */   public Image output = null;
/* 11:14 */   public Image input1 = null;
/* 12:15 */   public Image input2 = null;
/* 13:   */   
/* 14:   */   public Maximum()
/* 15:   */   {
/* 16:18 */     this.inputFields = "input1,input2";
/* 17:19 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:24 */     if (!this.input1.hasSameDimensionsAs(this.input2)) {
/* 24:25 */       throw new GlobalException("Arguments must be of same dimensions!");
/* 25:   */     }
/* 26:27 */     this.output = this.input1.newInstance(false);
/* 27:   */     
/* 28:29 */     int size = this.output.getSize();
/* 29:31 */     for (int i = 0; i < size; i++)
/* 30:   */     {
/* 31:32 */       double p1 = this.input1.getDouble(i);
/* 32:33 */       double p2 = this.input2.getDouble(i);
/* 33:   */       
/* 34:35 */       this.output.setDouble(i, p1 < p2 ? p2 : p1);
/* 35:   */     }
/* 36:   */   }
/* 37:   */   
/* 38:   */   public static Image invoke(Image image1, Image image2)
/* 39:   */   {
/* 40:   */     try
/* 41:   */     {
/* 42:50 */       return (Image)new Maximum().preprocess(new Object[] { image1, image2 });
/* 43:   */     }
/* 44:   */     catch (GlobalException e)
/* 45:   */     {
/* 46:52 */       e.printStackTrace();
/* 47:   */     }
/* 48:53 */     return null;
/* 49:   */   }
/* 50:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.Maximum
 * JD-Core Version:    0.7.0.1
 */