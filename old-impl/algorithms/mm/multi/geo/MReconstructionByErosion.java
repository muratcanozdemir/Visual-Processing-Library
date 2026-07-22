/*  1:   */ package vpt.algorithms.mm.multi.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Equal;
/*  7:   */ import vpt.util.ordering.AlgebraicOrdering;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class MReconstructionByErosion
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image inputImage = null;
/* 14:12 */   public Image mask = null;
/* 15:13 */   public FlatSE se = null;
/* 16:14 */   public Image output = null;
/* 17:15 */   public AlgebraicOrdering or = null;
/* 18:   */   
/* 19:   */   public MReconstructionByErosion()
/* 20:   */   {
/* 21:18 */     this.inputFields = "inputImage,se,mask,or";
/* 22:19 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:24 */     Image tmp = null;
/* 29:25 */     this.output = this.inputImage;
/* 30:   */     do
/* 31:   */     {
/* 32:28 */       tmp = this.output;
/* 33:29 */       this.output = MGeodesicErosion.invoke(tmp, this.se, this.mask, this.or);
/* 34:30 */     } while (!Equal.invoke(tmp, this.output).booleanValue());
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Image invoke(Image image, FlatSE se, Image mask, AlgebraicOrdering or)
/* 38:   */   {
/* 39:   */     try
/* 40:   */     {
/* 41:36 */       return (Image)new MReconstructionByErosion().preprocess(new Object[] { image, se, mask, or });
/* 42:   */     }
/* 43:   */     catch (GlobalException e)
/* 44:   */     {
/* 45:38 */       e.printStackTrace();
/* 46:   */     }
/* 47:39 */     return null;
/* 48:   */   }
/* 49:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.geo.MReconstructionByErosion
 * JD-Core Version:    0.7.0.1
 */