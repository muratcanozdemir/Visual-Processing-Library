/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.conversion.RGB2Gray;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ import vpt.algorithms.segmentation.Threshold;
/* 10:   */ 
/* 11:   */ public class MultiLobeMeanShapeFeatures
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:32 */   public double[] output = null;
/* 15:33 */   public Image input = null;
/* 16:   */   
/* 17:   */   public MultiLobeMeanShapeFeatures()
/* 18:   */   {
/* 19:37 */     this.inputFields = "input";
/* 20:38 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:43 */     if (this.input.getCDim() != 1) {
/* 27:43 */       throw new GlobalException("Sorry, only mono-channel images for now...");
/* 28:   */     }
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static double[] invoke(Image img)
/* 32:   */   {
/* 33:   */     try
/* 34:   */     {
/* 35:53 */       return (double[])new MultiLobeMeanShapeFeatures().preprocess(new Object[] { img });
/* 36:   */     }
/* 37:   */     catch (GlobalException e)
/* 38:   */     {
/* 39:55 */       e.printStackTrace();
/* 40:   */     }
/* 41:56 */     return null;
/* 42:   */   }
/* 43:   */   
/* 44:   */   public static void main(String[] args)
/* 45:   */   {
/* 46:62 */     Image img = Load.invoke("../clef_2013/train/categorized/scan_indexed_126_classes/Pistacia_lentiscus/58/1572.png");
/* 47:63 */     double[] feature = MultiScaleCCCount.invoke(Threshold.invoke(RGB2Gray.invoke(img), 0.00392156862745098D), Integer.valueOf(10), Integer.valueOf(7), Integer.valueOf(5));
/* 48:64 */     for (double d : feature) {
/* 49:65 */       System.err.println(d);
/* 50:   */     }
/* 51:   */   }
/* 52:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.MultiLobeMeanShapeFeatures
 * JD-Core Version:    0.7.0.1
 */