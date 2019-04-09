/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.GErosion;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class GOpeningByReconstruction
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:16 */   public Image input = null;
/* 13:17 */   public FlatSE se = null;
/* 14:18 */   public Image output = null;
/* 15:   */   
/* 16:   */   public GOpeningByReconstruction()
/* 17:   */   {
/* 18:21 */     this.inputFields = "input,se";
/* 19:22 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:26 */     Image marker = GErosion.invoke(this.input, this.se);
/* 26:   */     
/* 27:   */ 
/* 28:   */ 
/* 29:   */ 
/* 30:   */ 
/* 31:32 */     this.output = FastGrayReconstruction.invoke(marker, this.input, FastGrayReconstruction.CONNEXITY8, false);
/* 32:   */   }
/* 33:   */   
/* 34:   */   public static Image invoke(Image image, FlatSE se)
/* 35:   */   {
/* 36:   */     try
/* 37:   */     {
/* 38:38 */       return (Image)new GOpeningByReconstruction().preprocess(new Object[] { image, se });
/* 39:   */     }
/* 40:   */     catch (GlobalException e)
/* 41:   */     {
/* 42:40 */       e.printStackTrace();
/* 43:   */     }
/* 44:41 */     return null;
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GOpeningByReconstruction
 * JD-Core Version:    0.7.0.1
 */