/*  1:   */ package vpt.algorithms.mm.binary;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class BExternGradient
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:11 */   public Image output = null;
/* 13:12 */   public Image input = null;
/* 14:13 */   public FlatSE se = null;
/* 15:   */   
/* 16:   */   public BExternGradient()
/* 17:   */   {
/* 18:16 */     this.inputFields = "input,se";
/* 19:17 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:22 */     this.output = AbsSubtraction.invoke(BDilation.invoke(this.input, this.se), this.input);
/* 26:   */   }
/* 27:   */   
/* 28:   */   public static Image invoke(Image image, FlatSE se)
/* 29:   */   {
/* 30:   */     try
/* 31:   */     {
/* 32:28 */       return (Image)new BExternGradient().preprocess(new Object[] { image, se });
/* 33:   */     }
/* 34:   */     catch (GlobalException e)
/* 35:   */     {
/* 36:30 */       e.printStackTrace();
/* 37:   */     }
/* 38:31 */     return null;
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.BExternGradient
 * JD-Core Version:    0.7.0.1
 */