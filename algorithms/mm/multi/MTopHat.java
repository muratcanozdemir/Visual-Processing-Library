/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.util.ordering.Ordering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MTopHat
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image output = null;
/* 14:12 */   public Image input = null;
/* 15:13 */   public FlatSE se = null;
/* 16:14 */   public Ordering or = null;
/* 17:   */   
/* 18:   */   public MTopHat()
/* 19:   */   {
/* 20:17 */     this.inputFields = "input,se,or";
/* 21:18 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:23 */     this.output = AbsSubtraction.invoke(this.input, MOpening.invoke(this.input, this.se, this.or));
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image, FlatSE se, Ordering or)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:28 */       return (Image)new MTopHat().preprocess(new Object[] { image, se, or });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:30 */       e.printStackTrace();
/* 39:   */     }
/* 40:31 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MTopHat
 * JD-Core Version:    0.7.0.1
 */