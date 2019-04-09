/*  1:   */ package vpt.algorithms.mm.binary.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Maximum;
/*  7:   */ import vpt.algorithms.mm.binary.BErosion;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class BGeodesicErosion
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:11 */   public Image input = null;
/* 14:12 */   public Image mask = null;
/* 15:13 */   public FlatSE se = null;
/* 16:14 */   public Image output = null;
/* 17:   */   
/* 18:   */   public BGeodesicErosion()
/* 19:   */   {
/* 20:17 */     this.inputFields = "input,se,mask";
/* 21:18 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:23 */     this.output = Maximum.invoke(BErosion.invoke(this.input, this.se), this.mask);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static Image invoke(Image image, FlatSE se, Image mask)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:29 */       return (Image)new BGeodesicErosion().preprocess(new Object[] { image, se, mask });
/* 35:   */     }
/* 36:   */     catch (GlobalException e)
/* 37:   */     {
/* 38:31 */       e.printStackTrace();
/* 39:   */     }
/* 40:32 */     return null;
/* 41:   */   }
/* 42:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.geo.BGeodesicErosion
 * JD-Core Version:    0.7.0.1
 */