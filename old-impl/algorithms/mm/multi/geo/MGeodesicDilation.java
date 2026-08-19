/*  1:   */ package vpt.algorithms.mm.multi.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.VectorialMinimum;
/*  7:   */ import vpt.algorithms.mm.multi.MDilation;
/*  8:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MGeodesicDilation
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:12 */   public Image input = null;
/* 15:13 */   public Image mask = null;
/* 16:14 */   public FlatSE se = null;
/* 17:15 */   public Image output = null;
/* 18:16 */   public AlgebraicOrdering or = null;
/* 19:   */   
/* 20:   */   public MGeodesicDilation()
/* 21:   */   {
/* 22:19 */     this.inputFields = "input,se,mask,or";
/* 23:20 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:25 */     this.output = VectorialMinimum.invoke(MDilation.invoke(this.input, this.se, this.or), this.mask, this.or);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public static Image invoke(Image image, FlatSE se, Image mask, AlgebraicOrdering or)
/* 33:   */   {
/* 34:   */     try
/* 35:   */     {
/* 36:31 */       return (Image)new MGeodesicDilation().preprocess(new Object[] { image, se, mask, or });
/* 37:   */     }
/* 38:   */     catch (GlobalException e)
/* 39:   */     {
/* 40:33 */       e.printStackTrace();
/* 41:   */     }
/* 42:34 */     return null;
/* 43:   */   }
/* 44:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.geo.MGeodesicDilation
 * JD-Core Version:    0.7.0.1
 */