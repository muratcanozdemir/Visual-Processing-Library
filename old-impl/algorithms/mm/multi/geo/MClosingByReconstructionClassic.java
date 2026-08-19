/*  1:   */ package vpt.algorithms.mm.multi.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.multi.MDilation;
/*  7:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MClosingByReconstructionClassic
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Image input = null;
/* 14:19 */   public FlatSE se = null;
/* 15:20 */   public Image output = null;
/* 16:21 */   public AlgebraicOrdering or = null;
/* 17:   */   
/* 18:   */   public MClosingByReconstructionClassic()
/* 19:   */   {
/* 20:24 */     this.inputFields = "input,se,or";
/* 21:25 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:29 */     Image marker = MDilation.invoke(this.input, this.se, this.or);
/* 28:30 */     this.output = MReconstructionByErosion.invoke(marker, FlatSE.square(3), this.input, this.or);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static Image invoke(Image image, FlatSE se, AlgebraicOrdering or)
/* 32:   */   {
/* 33:   */     try
/* 34:   */     {
/* 35:35 */       return (Image)new MClosingByReconstructionClassic().preprocess(new Object[] { image, se, or });
/* 36:   */     }
/* 37:   */     catch (GlobalException e)
/* 38:   */     {
/* 39:37 */       e.printStackTrace();
/* 40:   */     }
/* 41:38 */     return null;
/* 42:   */   }
/* 43:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.geo.MClosingByReconstructionClassic
 * JD-Core Version:    0.7.0.1
 */