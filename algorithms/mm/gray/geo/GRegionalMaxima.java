/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Subtraction;
/*  7:   */ import vpt.algorithms.arithmetic.SubtractionC;
/*  8:   */ 
/*  9:   */ public class GRegionalMaxima
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Image inputImage = null;
/* 13:18 */   public Image output = null;
/* 14:   */   
/* 15:   */   public GRegionalMaxima()
/* 16:   */   {
/* 17:21 */     this.inputFields = "inputImage";
/* 18:22 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:27 */     Image fMinusOne = SubtractionC.invoke(this.inputImage, Double.valueOf(0.00392156862745098D));
/* 25:   */     
/* 26:   */ 
/* 27:30 */     fMinusOne = FastGrayReconstruction.invoke(fMinusOne, this.inputImage, FastGrayReconstruction.CONNEXITY8, false);
/* 28:   */     
/* 29:32 */     this.output = Subtraction.invoke(this.inputImage, fMinusOne);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public static Image invoke(Image image)
/* 33:   */   {
/* 34:   */     try
/* 35:   */     {
/* 36:38 */       return (Image)new GRegionalMaxima().preprocess(new Object[] { image });
/* 37:   */     }
/* 38:   */     catch (GlobalException e)
/* 39:   */     {
/* 40:40 */       e.printStackTrace();
/* 41:   */     }
/* 42:41 */     return null;
/* 43:   */   }
/* 44:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GRegionalMaxima
 * JD-Core Version:    0.7.0.1
 */