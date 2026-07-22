/*  1:   */ package vpt.algorithms.segmentation;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.BooleanImage;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.algorithms.histogram.Histogram;
/*  9:   */ 
/* 10:   */ public class OtsuThreshold
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:19 */   public BooleanImage output = null;
/* 14:20 */   public Image input = null;
/* 15:   */   
/* 16:   */   public OtsuThreshold()
/* 17:   */   {
/* 18:23 */     this.inputFields = "input";
/* 19:24 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:28 */     this.output = new BooleanImage(this.input, false);
/* 26:   */     
/* 27:30 */     double[] variances = new double['þ'];
/* 28:31 */     Arrays.fill(variances, 0.0D);
/* 29:   */     
/* 30:33 */     double[] histo = Histogram.invoke(this.input, Boolean.valueOf(false));
/* 31:36 */     for (int i = 1; i < 255; i++)
/* 32:   */     {
/* 33:39 */       double n1 = 0.0D;
/* 34:40 */       double n2 = 0.0D;
/* 35:   */       
/* 36:   */ 
/* 37:43 */       double mean1 = 0.0D;
/* 38:44 */       double mean2 = 0.0D;
/* 39:46 */       for (int j = 0; j < i; j++)
/* 40:   */       {
/* 41:47 */         n1 += histo[j];
/* 42:48 */         mean1 += histo[j] * j;
/* 43:   */       }
/* 44:51 */       for (int j = i; j < 256; j++)
/* 45:   */       {
/* 46:52 */         n2 += histo[j];
/* 47:53 */         mean2 += histo[j] * j;
/* 48:   */       }
/* 49:56 */       if ((n1 != 0.0D) && (n2 != 0.0D))
/* 50:   */       {
/* 51:59 */         mean1 /= n1;
/* 52:60 */         mean2 /= n2;
/* 53:   */         
/* 54:   */ 
/* 55:   */ 
/* 56:   */ 
/* 57:65 */         variances[(i - 1)] = (n1 * n2 * (mean1 - mean2) * (mean1 - mean2));
/* 58:   */       }
/* 59:   */     }
/* 60:69 */     int max = 0;
/* 61:70 */     for (int i = 1; i < variances.length; i++) {
/* 62:71 */       if (variances[i] > variances[max]) {
/* 63:72 */         max = i;
/* 64:   */       }
/* 65:   */     }
/* 66:75 */     this.output = Threshold.invoke(this.input, (max + 1) * 0.00392156862745098D);
/* 67:   */   }
/* 68:   */   
/* 69:   */   public static Image invoke(Image input)
/* 70:   */   {
/* 71:   */     try
/* 72:   */     {
/* 73:81 */       return (Image)new OtsuThreshold().preprocess(new Object[] { input });
/* 74:   */     }
/* 75:   */     catch (GlobalException e)
/* 76:   */     {
/* 77:83 */       e.printStackTrace();
/* 78:   */     }
/* 79:84 */     return null;
/* 80:   */   }
/* 81:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.OtsuThreshold
 * JD-Core Version:    0.7.0.1
 */