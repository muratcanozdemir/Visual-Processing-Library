/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ import util.Tools;
/*  5:   */ import vpt.Image;
/*  4:   */ 
/*  6:   */ 
/*  7:   */ 
/*  8:   */ public class Minimum
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:14 */   public Image output = null;
/* 12:15 */   public Image input1 = null;
/* 13:16 */   public Image input2 = null;
/* 14:   */   
/* 15:   */   public Minimum()
/* 16:   */   {
/* 17:19 */     this.inputFields = "input1,input2";
/* 18:20 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:25 */     if (!this.input1.hasSameDimensionsAs(this.input2)) {
/* 25:26 */       throw new GlobalException("Arguments must be of same dimensions!");
/* 26:   */     }
/* 27:28 */     this.output = this.input1.newInstance(false);
/* 28:   */     
/* 29:30 */     int size = this.output.getSize();
/* 30:32 */     for (int i = 0; i < size; i++)
/* 31:   */     {
/* 32:33 */       double p1 = this.input1.getDouble(i);
/* 33:34 */       double p2 = this.input2.getDouble(i);
/* 34:   */       
/* 35:36 */       this.output.setDouble(i, Tools.cmpr(p1, p2) < 0 ? p1 : p2);
/* 36:   */     }
/* 37:   */   }
/* 38:   */   
/* 39:   */   public static Image invoke(Image image1, Image image2)
/* 40:   */   {
/* 41:   */     try
/* 42:   */     {
/* 43:52 */       return (Image)new Minimum().preprocess(new Object[] { image1, image2 });
/* 44:   */     }
/* 45:   */     catch (GlobalException e)
/* 46:   */     {
/* 47:55 */       e.printStackTrace();
/* 48:   */     }
/* 49:56 */     return null;
/* 50:   */   }
/* 51:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.Minimum
 * JD-Core Version:    0.7.0.1
 */