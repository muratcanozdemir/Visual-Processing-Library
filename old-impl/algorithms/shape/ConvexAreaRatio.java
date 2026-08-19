/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.geometric.ConvexHull;
/*  7:   */ import vpt.util.Tools;
/*  8:   */ 
/*  9:   */ public class ConvexAreaRatio
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:17 */   public Double output = null;
/* 13:18 */   public Image input = null;
/* 14:   */   
/* 15:   */   public ConvexAreaRatio()
/* 16:   */   {
/* 17:21 */     this.inputFields = "input";
/* 18:22 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:26 */     int cdim = this.input.getCDim();
/* 25:28 */     if (cdim != 1) {
/* 26:28 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 27:   */     }
/* 28:34 */     double area = Tools.volume(this.input, 0);
/* 29:   */     
/* 30:36 */     this.input = ConvexHull.invoke(this.input, Boolean.valueOf(true));
/* 31:   */     
/* 32:38 */     double convexArea = Tools.volume(this.input, 0);
/* 33:   */     
/* 34:40 */     this.output = Double.valueOf((convexArea - area) / convexArea);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static Double invoke(Image img)
/* 38:   */   {
/* 39:   */     try
/* 40:   */     {
/* 41:46 */       return (Double)new ConvexAreaRatio().preprocess(new Object[] { img });
/* 42:   */     }
/* 43:   */     catch (GlobalException e)
/* 44:   */     {
/* 45:48 */       e.printStackTrace();
/* 46:   */     }
/* 47:49 */     return null;
/* 48:   */   }
/* 49:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.ConvexAreaRatio
 * JD-Core Version:    0.7.0.1
 */