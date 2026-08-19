/*  1:   */ package vpt.algorithms.statistical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.Tools;
/*  7:   */ 
/*  8:   */ public class NMSE
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:15 */   public Double output = null;
/* 12:16 */   public Image filtered = null;
/* 13:17 */   public Image original = null;
/* 14:   */   
/* 15:   */   public NMSE()
/* 16:   */   {
/* 17:20 */     this.inputFields = "original, filtered";
/* 18:21 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:25 */     int xdim = this.original.getXDim();
/* 25:26 */     int ydim = this.original.getYDim();
/* 26:   */     
/* 27:28 */     double sum1 = 0.0D;
/* 28:29 */     double sum2 = 0.0D;
/* 29:31 */     for (int x = 0; x < xdim; x++) {
/* 30:32 */       for (int y = 0; y < ydim; y++)
/* 31:   */       {
/* 32:33 */         double[] p1 = this.original.getVXYDouble(x, y);
/* 33:34 */         double[] p2 = this.filtered.getVXYDouble(x, y);
/* 34:   */         
/* 35:36 */         double norm = Tools.EuclideanNorm(Tools.VSub(p1, p2));
/* 36:37 */         sum1 += norm * norm;
/* 37:   */         
/* 38:39 */         norm = Tools.EuclideanNorm(p1);
/* 39:40 */         sum2 += norm * norm;
/* 40:   */       }
/* 41:   */     }
/* 42:44 */     this.output = new Double(sum1 / sum2);
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static Double invoke(Image original, Image filtered)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:50 */       return (Double)new NMSE().preprocess(new Object[] { original, filtered });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:52 */       e.printStackTrace();
/* 54:   */     }
/* 55:53 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.statistical.NMSE
 * JD-Core Version:    0.7.0.1
 */