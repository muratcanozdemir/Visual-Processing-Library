/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.GDilation;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class GClosingByReconstruction
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Image input = null;
/* 13:18 */   public FlatSE se = null;
/* 14:19 */   public Image output = null;
/* 15:   */   
/* 16:   */   public GClosingByReconstruction()
/* 17:   */   {
/* 18:22 */     this.inputFields = "input,se";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:27 */     Image marker = GDilation.invoke(this.input, this.se);
/* 26:   */     
/* 27:   */ 
/* 28:   */ 
/* 29:31 */     this.output = FastGrayReconstruction.invoke(marker, this.input, FastGrayReconstruction.CONNEXITY8, true);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public static Image invoke(Image image, FlatSE se)
/* 33:   */   {
/* 34:   */     try
/* 35:   */     {
/* 36:36 */       return (Image)new GClosingByReconstruction().preprocess(new Object[] { image, se });
/* 37:   */     }
/* 38:   */     catch (GlobalException e)
/* 39:   */     {
/* 40:38 */       e.printStackTrace();
/* 41:   */     }
/* 42:39 */     return null;
/* 43:   */   }
/* 44:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GClosingByReconstruction
 * JD-Core Version:    0.7.0.1
 */