/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.geometric.ConvexHull;
/*  7:   */ import vpt.algorithms.mm.binary.BInternGradient;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class ConvexPerimeterRatio
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:19 */   public Double output = null;
/* 15:20 */   public Image input = null;
/* 16:   */   
/* 17:   */   public ConvexPerimeterRatio()
/* 18:   */   {
/* 19:23 */     this.inputFields = "input";
/* 20:24 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:28 */     int cdim = this.input.getCDim();
/* 27:30 */     if (cdim != 1) {
/* 28:30 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 29:   */     }
/* 30:36 */     Image borders = BInternGradient.invoke(this.input, FlatSE.square(3), Boolean.valueOf(false));
/* 31:37 */     double perimeter = Tools.volume(borders, 0);
/* 32:   */     
/* 33:39 */     this.input = ConvexHull.invoke(this.input, Boolean.valueOf(true));
/* 34:   */     
/* 35:41 */     borders = BInternGradient.invoke(this.input, FlatSE.square(3), Boolean.valueOf(false));
/* 36:42 */     double convexPerimeter = Tools.volume(borders, 0);
/* 37:   */     
/* 38:44 */     this.output = Double.valueOf(convexPerimeter / perimeter);
/* 39:   */   }
/* 40:   */   
/* 41:   */   public static Double invoke(Image img)
/* 42:   */   {
/* 43:   */     try
/* 44:   */     {
/* 45:50 */       return (Double)new ConvexPerimeterRatio().preprocess(new Object[] { img });
/* 46:   */     }
/* 47:   */     catch (GlobalException e)
/* 48:   */     {
/* 49:52 */       e.printStackTrace();
/* 50:   */     }
/* 51:53 */     return null;
/* 52:   */   }
/* 53:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.ConvexPerimeterRatio
 * JD-Core Version:    0.7.0.1
 */