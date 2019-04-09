/*  1:   */ package vpt.algorithms.mm.multi.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.multi.MErosion;
/*  7:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MOpeningByReconstructionClassic
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:17 */   public Image input = null;
/* 14:18 */   public FlatSE se = null;
/* 15:19 */   public Image output = null;
/* 16:   */   public AlgebraicOrdering or;
/* 17:   */   
/* 18:   */   public MOpeningByReconstructionClassic()
/* 19:   */   {
/* 20:23 */     this.inputFields = "input,se,or";
/* 21:24 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:28 */     Image marker = MErosion.invoke(this.input, this.se, this.or);
/* 28:29 */     this.output = MReconstructionByDilation.invoke(marker, FlatSE.square(3), this.input, this.or);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static Image invoke(Image image, FlatSE se, AlgebraicOrdering or)
/* 32:   */   {
/* 33:   */     try
/* 34:   */     {
/* 35:35 */       return (Image)new MOpeningByReconstructionClassic().preprocess(new Object[] { image, se, or });
/* 36:   */     }
/* 37:   */     catch (GlobalException e)
/* 38:   */     {
/* 39:37 */       e.printStackTrace();
/* 40:   */     }
/* 41:38 */     return null;
/* 42:   */   }
/* 43:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.geo.MOpeningByReconstructionClassic
 * JD-Core Version:    0.7.0.1
 */