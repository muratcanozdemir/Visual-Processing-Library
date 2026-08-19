/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.DoubleImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.mm.gray.GInternGradient;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MEuclideanGradient
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:18 */   public Image output = null;
/* 15:19 */   public Image input = null;
/* 16:20 */   public FlatSE se = null;
/* 17:   */   
/* 18:   */   public MEuclideanGradient()
/* 19:   */   {
/* 20:23 */     this.inputFields = "input,se";
/* 21:24 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:28 */     Image gradient = GInternGradient.invoke(this.input, this.se);
/* 28:   */     
/* 29:30 */     int xdim = this.input.getXDim();
/* 30:31 */     int ydim = this.input.getYDim();
/* 31:32 */     int cdim = this.input.getCDim();
/* 32:   */     
/* 33:34 */     this.output = new DoubleImage(xdim, ydim, 1);
/* 34:   */     
/* 35:36 */     double normalizer = Math.sqrt(cdim);
/* 36:38 */     for (int y = 0; y < ydim; y++) {
/* 37:39 */       for (int x = 0; x < xdim; x++)
/* 38:   */       {
/* 39:41 */         double norm = Tools.EuclideanNorm(gradient.getVXYDouble(x, y));
/* 40:   */         
/* 41:43 */         this.output.setXYDouble(x, y, norm / normalizer);
/* 42:   */       }
/* 43:   */     }
/* 44:   */   }
/* 45:   */   
/* 46:   */   public static Image invoke(Image image, FlatSE se)
/* 47:   */   {
/* 48:   */     try
/* 49:   */     {
/* 50:53 */       return (Image)new MEuclideanGradient().preprocess(new Object[] { image, se });
/* 51:   */     }
/* 52:   */     catch (GlobalException e)
/* 53:   */     {
/* 54:55 */       e.printStackTrace();
/* 55:   */     }
/* 56:56 */     return null;
/* 57:   */   }
/* 58:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MEuclideanGradient
 * JD-Core Version:    0.7.0.1
 */