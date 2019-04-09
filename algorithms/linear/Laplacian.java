/*  1:   */ package vpt.algorithms.linear;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.histogram.ContrastStretch;
/*  8:   */ import vpt.util.se.NonFlatSE;
/*  9:   */ 
/* 10:   */ public class Laplacian
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public Image output = null;
/* 14:19 */   public Image input = null;
/* 15:20 */   public Boolean normalized = null;
/* 16:   */   
/* 17:   */   public Laplacian()
/* 18:   */   {
/* 19:23 */     this.inputFields = "input,normalized";
/* 20:24 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:28 */     NonFlatSE kernel = new NonFlatSE(new Point(1, 1), new double[][] { { 0.0D, -1.0D, 0.0D }, { -1.0D, 4.0D, -1.0D }, { 0.0D, -1.0D, 0.0D } });
/* 27:29 */     this.output = Convolution.invoke(this.input, kernel);
/* 28:31 */     if (this.normalized.booleanValue()) {
/* 29:31 */       this.output = ContrastStretch.invoke(this.output);
/* 30:   */     }
/* 31:   */   }
/* 32:   */   
/* 33:   */   public static Image invoke(Image image, Boolean normalized)
/* 34:   */   {
/* 35:   */     try
/* 36:   */     {
/* 37:37 */       return (Image)new Laplacian().preprocess(new Object[] { image, normalized });
/* 38:   */     }
/* 39:   */     catch (GlobalException e)
/* 40:   */     {
/* 41:39 */       e.printStackTrace();
/* 42:   */     }
/* 43:40 */     return null;
/* 44:   */   }
/* 45:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.Laplacian
 * JD-Core Version:    0.7.0.1
 */