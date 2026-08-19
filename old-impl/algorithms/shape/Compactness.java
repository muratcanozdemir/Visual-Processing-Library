/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.mm.binary.BInternGradient;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class Compactness
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Double output = null;
/* 14:19 */   public Image input = null;
/* 15:   */   
/* 16:   */   public Compactness()
/* 17:   */   {
/* 18:22 */     this.inputFields = "input";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:27 */     int cdim = this.input.getCDim();
/* 26:29 */     if (cdim != 1) {
/* 27:29 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 28:   */     }
/* 29:35 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/* 30:   */     
/* 31:37 */     double perimeter = Tools.volume(borderImage, 0);
/* 32:38 */     double area = Tools.volume(this.input, 0);
/* 33:   */     
/* 34:40 */     this.output = Double.valueOf(perimeter * perimeter / area);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Double invoke(Image img)
/* 38:   */   {
/* 39:   */     try
/* 40:   */     {
/* 41:46 */       return (Double)new Compactness().preprocess(new Object[] { img });
/* 42:   */     }
/* 43:   */     catch (GlobalException e)
/* 44:   */     {
/* 45:48 */       e.printStackTrace();
/* 46:   */     }
/* 47:49 */     return null;
/* 48:   */   }
/* 49:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.Compactness
 * JD-Core Version:    0.7.0.1
 */