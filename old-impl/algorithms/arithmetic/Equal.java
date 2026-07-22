/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ import util.Tools;
/*  6:   */ import vpt.Image;
/*  4:   */ 
/*  5:   */ 
/*  7:   */ 
/*  8:   */ 
/*  9:   */ public class Equal
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Image input1 = null;
/* 13:18 */   public Image input2 = null;
/* 14:19 */   public Boolean output = Boolean.valueOf(true);
/* 15:   */   
/* 16:   */   public Equal()
/* 17:   */   {
/* 18:22 */     this.inputFields = "input1,input2";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:28 */     if (!this.input1.hasSameDimensionsAs(this.input2)) {
/* 26:29 */       throw new GlobalException("Arguments must be of same dimensions!");
/* 27:   */     }
/* 28:31 */     int size = this.input1.getSize();
/* 29:33 */     for (int i = 0; i < size; i++)
/* 30:   */     {
/* 31:34 */       double p1 = this.input1.getDouble(i);
/* 32:35 */       double p2 = this.input2.getDouble(i);
/* 33:37 */       if (Tools.cmpr(p1, p2) != 0)
/* 34:   */       {
/* 35:38 */         System.err.println(p1 + " " + p2);
/* 36:39 */         this.output = Boolean.valueOf(false);
/* 37:40 */         break;
/* 38:   */       }
/* 39:   */     }
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Boolean invoke(Image image1, Image image2)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:57 */       return (Boolean)new Equal().preprocess(new Object[] { image1, image2 });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:60 */       e.printStackTrace();
/* 51:   */     }
/* 52:61 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.Equal
 * JD-Core Version:    0.7.0.1
 */