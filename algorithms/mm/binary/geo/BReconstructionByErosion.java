/*  1:   */ package vpt.algorithms.mm.binary.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Equal;
/*  7:   */ import vpt.algorithms.display.Display2D;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class BReconstructionByErosion
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image marker = null;
/* 14:12 */   public Image mask = null;
/* 15:13 */   public FlatSE se = null;
/* 16:14 */   public Image output = null;
/* 17:   */   
/* 18:   */   public BReconstructionByErosion()
/* 19:   */   {
/* 20:17 */     this.inputFields = "marker,se,mask";
/* 21:18 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:23 */     Image tmp = null;
/* 28:24 */     this.output = this.marker;
/* 29:   */     do
/* 30:   */     {
/* 31:27 */       tmp = this.output;
/* 32:28 */       this.output = BGeodesicErosion.invoke(tmp, this.se, this.mask);
/* 33:29 */       Display2D.invoke(this.output, " ", new Boolean(false));
/* 34:30 */     } while (!Equal.invoke(tmp, this.output).booleanValue());
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Image invoke(Image marker, FlatSE se, Image mask)
/* 38:   */   {
/* 39:   */     try
/* 40:   */     {
/* 41:36 */       return (Image)new BReconstructionByErosion().preprocess(new Object[] { marker, se, mask });
/* 42:   */     }
/* 43:   */     catch (GlobalException e)
/* 44:   */     {
/* 45:38 */       e.printStackTrace();
/* 46:   */     }
/* 47:39 */     return null;
/* 48:   */   }
/* 49:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.geo.BReconstructionByErosion
 * JD-Core Version:    0.7.0.1
 */