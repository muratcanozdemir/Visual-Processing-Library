/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AbsSubtraction;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class GTopHatByReconstruction
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public Image output = null;
/* 13:11 */   public Image input = null;
/* 14:12 */   public FlatSE se = null;
/* 15:   */   
/* 16:   */   public GTopHatByReconstruction()
/* 17:   */   {
/* 18:15 */     this.inputFields = "input,se";
/* 19:16 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:20 */     this.output = AbsSubtraction.invoke(this.input, GOpeningByReconstruction.invoke(this.input, this.se));
/* 26:   */   }
/* 27:   */   
/* 28:   */   public static Image invoke(Image image, FlatSE se)
/* 29:   */   {
/* 30:   */     try
/* 31:   */     {
/* 32:25 */       return (Image)new GTopHatByReconstruction().preprocess(new Object[] { image, se });
/* 33:   */     }
/* 34:   */     catch (GlobalException e)
/* 35:   */     {
/* 36:27 */       e.printStackTrace();
/* 37:   */     }
/* 38:28 */     return null;
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GTopHatByReconstruction
 * JD-Core Version:    0.7.0.1
 */