/*  1:   */ package vpt.algorithms.mm.binary;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class BInternGradient
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public Image output = null;
/* 13:11 */   public Image input = null;
/* 14:12 */   public FlatSE se = null;
/* 15:13 */   public Boolean flag = null;
/* 16:   */   
/* 17:   */   public BInternGradient()
/* 18:   */   {
/* 19:16 */     this.inputFields = "input, se, flag";
/* 20:17 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:22 */     this.output = AbsSubtraction.invoke(this.input, BErosion.invoke(this.input, this.se, this.flag));
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static Image invoke(Image image, FlatSE se)
/* 30:   */   {
/* 31:   */     try
/* 32:   */     {
/* 33:28 */       return (Image)new BInternGradient().preprocess(new Object[] { image, se, Boolean.valueOf(true) });
/* 34:   */     }
/* 35:   */     catch (GlobalException e)
/* 36:   */     {
/* 37:30 */       e.printStackTrace();
/* 38:   */     }
/* 39:31 */     return null;
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Image invoke(Image image, FlatSE se, Boolean flag)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:38 */       return (Image)new BInternGradient().preprocess(new Object[] { image, se, flag });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:40 */       e.printStackTrace();
/* 51:   */     }
/* 52:41 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.BInternGradient
 * JD-Core Version:    0.7.0.1
 */