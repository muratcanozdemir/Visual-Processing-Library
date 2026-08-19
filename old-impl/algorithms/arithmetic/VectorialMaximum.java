/*  1:   */ package vpt.algorithms.arithmetic;
/*  2:   */ 
/*  3:   */ import util.ordering.AlgebraicOrdering;
/*  5:   */ import vpt.Image;
/*  4:   */ 
/*  6:   */ 
/*  7:   */ 
/*  8:   */ public class VectorialMaximum
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:14 */   public Image output = null;
/* 12:15 */   public Image input1 = null;
/* 13:16 */   public Image input2 = null;
/* 14:17 */   public AlgebraicOrdering or = null;
/* 15:   */   
/* 16:   */   public VectorialMaximum()
/* 17:   */   {
/* 18:20 */     this.inputFields = "input1,input2,or";
/* 19:21 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:26 */     if (!this.input1.hasSameDimensionsAs(this.input2)) {
/* 26:27 */       throw new GlobalException("Arguments must be of same dimensions!");
/* 27:   */     }
/* 28:29 */     this.output = this.input1.newInstance(false);
/* 29:31 */     for (int x = 0; x < this.output.getXDim(); x++) {
/* 30:32 */       for (int y = 0; y < this.output.getYDim(); y++)
/* 31:   */       {
/* 32:34 */         double[] p1 = this.input1.getVXYDouble(x, y);
/* 33:35 */         double[] p2 = this.input2.getVXYDouble(x, y);
/* 34:   */         
/* 35:37 */         this.output.setVXYDouble(x, y, this.or.max(p1, p2));
/* 36:   */       }
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static Image invoke(Image image1, Image image2, AlgebraicOrdering or)
/* 41:   */   {
/* 42:   */     try
/* 43:   */     {
/* 44:52 */       return (Image)new VectorialMaximum().preprocess(new Object[] { image1, image2, or });
/* 45:   */     }
/* 46:   */     catch (GlobalException e)
/* 47:   */     {
/* 48:54 */       e.printStackTrace();
/* 49:   */     }
/* 50:55 */     return null;
/* 51:   */   }
/* 52:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.arithmetic.VectorialMaximum
 * JD-Core Version:    0.7.0.1
 */