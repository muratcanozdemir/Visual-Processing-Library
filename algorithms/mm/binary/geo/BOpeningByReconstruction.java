/*  1:   */ package vpt.algorithms.mm.binary.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.binary.BErosion;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class BOpeningByReconstruction
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:16 */   public Image input = null;
/* 13:17 */   public FlatSE se = null;
/* 14:18 */   public Image output = null;
/* 15:   */   
/* 16:   */   public BOpeningByReconstruction()
/* 17:   */   {
/* 18:21 */     this.inputFields = "input,se";
/* 19:22 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:26 */     Image marker = BErosion.invoke(this.input, this.se);
/* 26:   */     
/* 27:28 */     this.output = BReconstructionByDilation.invoke(marker, this.se, this.input);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image, FlatSE se)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:34 */       return (Image)new BOpeningByReconstruction().preprocess(new Object[] { image, se });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:36 */       e.printStackTrace();
/* 39:   */     }
/* 40:37 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.geo.BOpeningByReconstruction
 * JD-Core Version:    0.7.0.1
 */