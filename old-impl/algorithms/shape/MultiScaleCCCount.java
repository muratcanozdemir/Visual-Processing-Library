/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.mm.binary.BOpening;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class MultiScaleCCCount
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:21 */   public double[] output = null;
/* 15:22 */   public Image input = null;
/* 16:23 */   public Integer length = null;
/* 17:24 */   public Integer step = null;
/* 18:25 */   public Integer startSize = null;
/* 19:   */   
/* 20:   */   public MultiScaleCCCount()
/* 21:   */   {
/* 22:28 */     this.inputFields = "input, length, step, startSize";
/* 23:29 */     this.outputFields = "output";
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void execute()
/* 27:   */     throws GlobalException
/* 28:   */   {
/* 29:34 */     if (this.input.getCDim() != 1) {
/* 30:34 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/* 31:   */     }
/* 32:36 */     this.output = new double[this.length.intValue()];
/* 33:   */     
/* 34:38 */     Image tmp = BOpening.invoke(this.input, FlatSE.square(this.startSize.intValue()));
/* 35:39 */     this.output[0] = getNumberOfCCs(tmp);
/* 36:41 */     for (int i = 1; i < this.length.intValue(); i++)
/* 37:   */     {
/* 38:42 */       tmp = BOpening.invoke(tmp, FlatSE.square(this.startSize.intValue() + this.step.intValue() * i));
/* 39:43 */       this.output[i] = getNumberOfCCs(tmp);
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   private static int getNumberOfCCs(Image img)
/* 44:   */   {
/* 45:48 */     ArrayList<ArrayList<Point>> CCs = ConnectedComponents.invoke(img);
/* 46:49 */     return CCs.size();
/* 47:   */   }
/* 48:   */   
/* 49:   */   public static double[] invoke(Image img, Integer length, Integer step, Integer startSize)
/* 50:   */   {
/* 51:   */     try
/* 52:   */     {
/* 53:55 */       return (double[])new MultiScaleCCCount().preprocess(new Object[] { img, length, step, startSize });
/* 54:   */     }
/* 55:   */     catch (GlobalException e)
/* 56:   */     {
/* 57:57 */       e.printStackTrace();
/* 58:   */     }
/* 59:58 */     return null;
/* 60:   */   }
/* 61:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.MultiScaleCCCount
 * JD-Core Version:    0.7.0.1
 */