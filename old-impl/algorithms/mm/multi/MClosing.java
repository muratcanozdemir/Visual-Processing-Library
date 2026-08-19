/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.ordering.Ordering;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class MClosing
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public Image output = null;
/* 13:11 */   public Image input = null;
/* 14:12 */   public FlatSE se = null;
/* 15:13 */   public Ordering or = null;
/* 16:   */   
/* 17:   */   public MClosing()
/* 18:   */   {
/* 19:16 */     this.inputFields = "input,se,or";
/* 20:17 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:22 */     this.output = MDilation.invoke(this.input, this.se, this.or);
/* 27:23 */     this.output = MErosion.invoke(this.output, this.se, this.or);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image, FlatSE se, Ordering or)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:29 */       return (Image)new MClosing().preprocess(new Object[] { image, se, or });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:31 */       e.printStackTrace();
/* 39:   */     }
/* 40:32 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MClosing
 * JD-Core Version:    0.7.0.1
 */