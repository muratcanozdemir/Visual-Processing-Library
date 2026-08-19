/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Minimum;
/*  7:   */ import vpt.algorithms.mm.gray.GDilation;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class GGeodesicDilation
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image inputImage = null;
/* 14:12 */   public Image mask = null;
/* 15:13 */   public FlatSE se = null;
/* 16:14 */   public Image output = null;
/* 17:   */   
/* 18:   */   public GGeodesicDilation()
/* 19:   */   {
/* 20:17 */     this.inputFields = "inputImage,se,mask";
/* 21:18 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:23 */     this.output = Minimum.invoke(GDilation.invoke(this.inputImage, this.se), this.mask);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image, FlatSE se, Image mask)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:29 */       return (Image)new GGeodesicDilation().preprocess(new Object[] { image, se, mask });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:31 */       e.printStackTrace();
/* 39:   */     }
/* 40:32 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GGeodesicDilation
 * JD-Core Version:    0.7.0.1
 */