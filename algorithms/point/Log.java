/*  1:   */ package vpt.algorithms.point;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Log
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:15 */   public Image output = null;
/* 11:16 */   public Image input = null;
/* 12:   */   
/* 13:   */   public Log()
/* 14:   */   {
/* 15:19 */     this.inputFields = "input";
/* 16:20 */     this.outputFields = "output";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void execute()
/* 20:   */     throws GlobalException
/* 21:   */   {
/* 22:25 */     this.output = this.input.newInstance(true);
/* 23:27 */     for (int i = 0; i < this.output.getSize(); i++)
/* 24:   */     {
/* 25:28 */       double pixel = this.output.getDouble(i);
/* 26:29 */       this.output.setDouble(i, Math.log10(1.0D + pixel));
/* 27:   */     }
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:37 */       return (Image)new Log().preprocess(new Object[] { image });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:39 */       e.printStackTrace();
/* 39:   */     }
/* 40:40 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.point.Log
 * JD-Core Version:    0.7.0.1
 */