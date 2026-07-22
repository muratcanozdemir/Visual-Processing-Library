/*  1:   */ package vpt.algorithms.point;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Inversion
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10: 9 */   public Image output = null;
/* 11:10 */   public Image input = null;
/* 12:   */   
/* 13:   */   public Inversion()
/* 14:   */   {
/* 15:13 */     this.inputFields = "input";
/* 16:14 */     this.outputFields = "output";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void execute()
/* 20:   */     throws GlobalException
/* 21:   */   {
/* 22:19 */     this.output = this.input.newInstance(true);
/* 23:21 */     for (int i = 0; i < this.output.getSize(); i++)
/* 24:   */     {
/* 25:22 */       double pixel = this.output.getDouble(i);
/* 26:23 */       this.output.setDouble(i, 1.0D - pixel);
/* 27:   */     }
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:31 */       return (Image)new Inversion().preprocess(new Object[] { image });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:33 */       e.printStackTrace();
/* 39:   */     }
/* 40:34 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.point.Inversion
 * JD-Core Version:    0.7.0.1
 */