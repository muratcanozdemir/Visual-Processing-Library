/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class GLaplacian
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:15 */   public Image output = null;
/* 13:16 */   public Image input = null;
/* 14:17 */   public FlatSE se = null;
/* 15:   */   
/* 16:   */   public GLaplacian()
/* 17:   */   {
/* 18:20 */     this.inputFields = "input,se";
/* 19:21 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:25 */     Image externGrad = GExternGradient.invoke(this.input, this.se);
/* 26:26 */     Image internGrad = GInternGradient.invoke(this.input, this.se);
/* 27:   */     
/* 28:28 */     this.output = AbsSubtraction.invoke(externGrad, internGrad);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static Image invoke(Image image, FlatSE se)
/* 32:   */   {
/* 33:   */     try
/* 34:   */     {
/* 35:34 */       return (Image)new GLaplacian().preprocess(new Object[] { image, se });
/* 36:   */     }
/* 37:   */     catch (GlobalException e)
/* 38:   */     {
/* 39:36 */       e.printStackTrace();
/* 40:   */     }
/* 41:37 */     return null;
/* 42:   */   }
/* 43:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GLaplacian
 * JD-Core Version:    0.7.0.1
 */