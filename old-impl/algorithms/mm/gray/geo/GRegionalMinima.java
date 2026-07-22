/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Subtraction;
/*  7:   */ import vpt.algorithms.arithmetic.SubtractionC;
/*  8:   */ import vpt.algorithms.point.Inversion;
/*  9:   */ 
/* 10:   */ public class GRegionalMinima
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Image inputImage = null;
/* 14:19 */   public Image output = null;
/* 15:   */   
/* 16:   */   public GRegionalMinima()
/* 17:   */   {
/* 18:22 */     this.inputFields = "inputImage";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:29 */     this.inputImage = Inversion.invoke(this.inputImage);
/* 26:   */     
/* 27:31 */     Image fMinusOne = SubtractionC.invoke(this.inputImage, Double.valueOf(0.00392156862745098D));
/* 28:   */     
/* 29:   */ 
/* 30:34 */     fMinusOne = FastGrayReconstruction.invoke(fMinusOne, this.inputImage, FastGrayReconstruction.CONNEXITY8, false);
/* 31:   */     
/* 32:36 */     this.output = Subtraction.invoke(this.inputImage, fMinusOne);
/* 33:   */   }
/* 34:   */   
/* 35:   */   public static Image invoke(Image image)
/* 36:   */   {
/* 37:   */     try
/* 38:   */     {
/* 39:42 */       return (Image)new GRegionalMinima().preprocess(new Object[] { image });
/* 40:   */     }
/* 41:   */     catch (GlobalException e)
/* 42:   */     {
/* 43:44 */       e.printStackTrace();
/* 44:   */     }
/* 45:45 */     return null;
/* 46:   */   }
/* 47:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GRegionalMinima
 * JD-Core Version:    0.7.0.1
 */