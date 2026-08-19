/*  1:   */ package vpt.algorithms.texture;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.gray.GRankFilter;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class SmoothnessFactor
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:22 */   public Double output = null;
/* 14:23 */   public Image input = null;
/* 15:24 */   public Integer size = null;
/* 16:   */   
/* 17:   */   public SmoothnessFactor()
/* 18:   */   {
/* 19:27 */     this.inputFields = "input, size";
/* 20:28 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:32 */     if (this.input.getCDim() != 1) {
/* 27:32 */       throw new GlobalException("The input image must be grayscale");
/* 28:   */     }
/* 29:34 */     Image filtered = GRankFilter.invoke(this.input, FlatSE.square(this.size.intValue()), Integer.valueOf(this.size.intValue() * this.size.intValue() / 2));
/* 30:   */     
/* 31:36 */     this.output = Double.valueOf(Tools.volume(filtered, 0) / Tools.volume(this.input, 0));
/* 32:   */   }
/* 33:   */   
/* 34:   */   public static Double invoke(Image image, Integer size)
/* 35:   */   {
/* 36:   */     try
/* 37:   */     {
/* 38:42 */       return (Double)new SmoothnessFactor().preprocess(new Object[] { image, size });
/* 39:   */     }
/* 40:   */     catch (GlobalException e)
/* 41:   */     {
/* 42:44 */       e.printStackTrace();
/* 43:   */     }
/* 44:45 */     return null;
/* 45:   */   }
/* 46:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.SmoothnessFactor
 * JD-Core Version:    0.7.0.1
 */