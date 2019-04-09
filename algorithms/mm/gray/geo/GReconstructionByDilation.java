/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Equal;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class GReconstructionByDilation
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public Image inputImage = null;
/* 13:11 */   public Image mask = null;
/* 14:12 */   public FlatSE se = null;
/* 15:13 */   public Image output = null;
/* 16:   */   
/* 17:   */   public GReconstructionByDilation()
/* 18:   */   {
/* 19:16 */     this.inputFields = "inputImage,se,mask";
/* 20:17 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:23 */     Image tmp = null;
/* 27:24 */     this.output = this.inputImage;
/* 28:   */     do
/* 29:   */     {
/* 30:27 */       tmp = this.output;
/* 31:28 */       this.output = GGeodesicDilation.invoke(tmp, this.se, this.mask);
/* 32:29 */     } while (!Equal.invoke(tmp, this.output).booleanValue());
/* 33:   */   }
/* 34:   */   
/* 35:   */   public static Image invoke(Image image, FlatSE se, Image mask)
/* 36:   */   {
/* 37:   */     try
/* 38:   */     {
/* 39:35 */       return (Image)new GReconstructionByDilation().preprocess(new Object[] { image, se, mask });
/* 40:   */     }
/* 41:   */     catch (GlobalException e)
/* 42:   */     {
/* 43:37 */       e.printStackTrace();
/* 44:   */     }
/* 45:38 */     return null;
/* 46:   */   }
/* 47:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GReconstructionByDilation
 * JD-Core Version:    0.7.0.1
 */