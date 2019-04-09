/*  1:   */ package vpt.algorithms.mm.binary.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Equal;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class BReconstructionByDilation
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:10 */   public Image marker = null;
/* 13:11 */   public Image mask = null;
/* 14:12 */   public FlatSE se = null;
/* 15:13 */   public Image output = null;
/* 16:   */   
/* 17:   */   public BReconstructionByDilation()
/* 18:   */   {
/* 19:16 */     this.inputFields = "marker,se,mask";
/* 20:17 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:22 */     Image tmp = null;
/* 27:23 */     this.output = this.marker;
/* 28:   */     do
/* 29:   */     {
/* 30:26 */       tmp = this.output;
/* 31:27 */       this.output = BGeodesicDilation.invoke(tmp, this.se, this.mask);
/* 32:29 */     } while (!Equal.invoke(tmp, this.output).booleanValue());
/* 33:   */   }
/* 34:   */   
/* 35:   */   public static Image invoke(Image marker, FlatSE se, Image mask)
/* 36:   */   {
/* 37:   */     try
/* 38:   */     {
/* 39:35 */       return (Image)new BReconstructionByDilation().preprocess(new Object[] { marker, se, mask });
/* 40:   */     }
/* 41:   */     catch (GlobalException e)
/* 42:   */     {
/* 43:37 */       e.printStackTrace();
/* 44:   */     }
/* 45:38 */     return null;
/* 46:   */   }
/* 47:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.geo.BReconstructionByDilation
 * JD-Core Version:    0.7.0.1
 */