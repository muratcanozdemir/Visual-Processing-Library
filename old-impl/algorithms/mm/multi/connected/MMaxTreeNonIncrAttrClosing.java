/*  1:   */ package vpt.algorithms.mm.multi.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.multi.connected.attribute.MultiAttribute;
/*  7:   */ import vpt.algorithms.point.Inversion;
/*  8:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  9:   */ 
/* 10:   */ public class MMaxTreeNonIncrAttrClosing
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:21 */   public Image input = null;
/* 14:22 */   public MultiAttribute crit = null;
/* 15:23 */   public Image output = null;
/* 16:24 */   public AlgebraicOrdering vo = null;
/* 17:25 */   public Integer filteringStrategy = null;
/* 18:   */   
/* 19:   */   public MMaxTreeNonIncrAttrClosing()
/* 20:   */   {
/* 21:28 */     this.inputFields = "input, crit, vo, filteringStrategy";
/* 22:29 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:33 */     this.output = Inversion.invoke(MMaxTreeNonIncrAttrOpening.invoke(Inversion.invoke(this.input), this.crit, this.vo, this.filteringStrategy));
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static Image invoke(Image input, MultiAttribute c, AlgebraicOrdering vo)
/* 32:   */   {
/* 33:   */     try
/* 34:   */     {
/* 35:38 */       return (Image)new MMaxTreeNonIncrAttrClosing().preprocess(new Object[] { input, c, vo, null });
/* 36:   */     }
/* 37:   */     catch (GlobalException e)
/* 38:   */     {
/* 39:40 */       e.printStackTrace();
/* 40:   */     }
/* 41:41 */     return null;
/* 42:   */   }
/* 43:   */   
/* 44:   */   public static Image invoke(Image input, MultiAttribute c, AlgebraicOrdering vo, Integer filteringStrategy)
/* 45:   */   {
/* 46:   */     try
/* 47:   */     {
/* 48:47 */       return (Image)new MMaxTreeNonIncrAttrClosing().preprocess(new Object[] { input, c, vo, filteringStrategy });
/* 49:   */     }
/* 50:   */     catch (GlobalException e)
/* 51:   */     {
/* 52:49 */       e.printStackTrace();
/* 53:   */     }
/* 54:50 */     return null;
/* 55:   */   }
/* 56:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.connected.MMaxTreeNonIncrAttrClosing
 * JD-Core Version:    0.7.0.1
 */