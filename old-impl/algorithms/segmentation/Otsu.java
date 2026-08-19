/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.histogram.Histogram;
/*  8:   */ 
/*  9:   */ public class Otsu
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:18 */   public Integer output = null;
/* 13:19 */   public Image input = null;
/* 14:   */   
/* 15:   */   public Otsu()
/* 16:   */   {
/* 17:22 */     this.inputFields = "input";
/* 18:23 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     double[] variances = new double['þ'];
/* 25:29 */     Arrays.fill(variances, 0.0D);
/* 26:   */     
/* 27:31 */     double[] histo = Histogram.invoke(this.input, Boolean.valueOf(false));
/* 28:34 */     for (int i = 1; i < 255; i++)
/* 29:   */     {
/* 30:37 */       double n1 = 0.0D;
/* 31:38 */       double n2 = 0.0D;
/* 32:   */       
/* 33:   */ 
/* 34:41 */       double mean1 = 0.0D;
/* 35:42 */       double mean2 = 0.0D;
/* 36:44 */       for (int j = 0; j < i; j++)
/* 37:   */       {
/* 38:45 */         n1 += histo[j];
/* 39:46 */         mean1 += histo[j] * j;
/* 40:   */       }
/* 41:49 */       for (int j = i; j < 256; j++)
/* 42:   */       {
/* 43:50 */         n2 += histo[j];
/* 44:51 */         mean2 += histo[j] * j;
/* 45:   */       }
/* 46:54 */       if ((n1 != 0.0D) && (n2 != 0.0D))
/* 47:   */       {
/* 48:57 */         mean1 /= n1;
/* 49:58 */         mean2 /= n2;
/* 50:   */         
/* 51:   */ 
/* 52:   */ 
/* 53:   */ 
/* 54:63 */         variances[(i - 1)] = (n1 * n2 * (mean1 - mean2) * (mean1 - mean2));
/* 55:   */       }
/* 56:   */     }
/* 57:67 */     int max = 0;
/* 58:68 */     for (int i = 1; i < variances.length; i++) {
/* 59:69 */       if (variances[i] > variances[max]) {
/* 60:70 */         max = i;
/* 61:   */       }
/* 62:   */     }
/* 63:73 */     this.output = Integer.valueOf(max + 1);
/* 64:   */   }
/* 65:   */   
/* 66:   */   public static Integer invoke(Image input)
/* 67:   */   {
/* 68:   */     try
/* 69:   */     {
/* 70:79 */       return (Integer)new Otsu().preprocess(new Object[] { input });
/* 71:   */     }
/* 72:   */     catch (GlobalException e)
/* 73:   */     {
/* 74:81 */       e.printStackTrace();
/* 75:   */     }
/* 76:82 */     return null;
/* 77:   */   }
/* 78:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.Otsu
 * JD-Core Version:    0.7.0.1
 */