/*  1:   */ package vpt.algorithms.mm.multi.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.multi.MDilation;
/*  7:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MClosingByReconstruction
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Image input = null;
/* 14:19 */   public FlatSE se = null;
/* 15:20 */   public Image output = null;
/* 16:21 */   public AlgebraicOrdering or = null;
/* 17:   */   
/* 18:   */   public MClosingByReconstruction()
/* 19:   */   {
/* 20:24 */     this.inputFields = "input,se,or";
/* 21:25 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:29 */     Image marker = MDilation.invoke(this.input, this.se, this.or);
/* 28:   */     
/* 29:31 */     this.output = MFastReconstruction.invoke(marker, this.input, MFastReconstruction.CONNEXITY8, true, this.or);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public static Image invoke(Image image, FlatSE se, AlgebraicOrdering or)
/* 33:   */   {
/* 34:   */     try
/* 35:   */     {
/* 36:36 */       return (Image)new MClosingByReconstruction().preprocess(new Object[] { image, se, or });
/* 37:   */     }
/* 38:   */     catch (GlobalException e)
/* 39:   */     {
/* 40:38 */       e.printStackTrace();
/* 41:   */     }
/* 42:39 */     return null;
/* 43:   */   }
/* 44:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.geo.MClosingByReconstruction
 * JD-Core Version:    0.7.0.1
 */