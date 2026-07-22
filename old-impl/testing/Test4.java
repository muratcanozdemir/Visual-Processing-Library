/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import java.util.Arrays;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.display.Display2D;
/*  7:   */ import vpt.algorithms.io.Load;
/*  8:   */ import vpt.algorithms.segmentation.OtsuThreshold;
/*  9:   */ import vpt.algorithms.segmentation.Threshold;
/* 10:   */ 
/* 11:   */ public class Test4
/* 12:   */ {
/* 13:   */   public static void main(String[] args)
/* 14:   */   {
/* 15:29 */     Image img = Load.invoke("samples/konak.png");
/* 16:   */     
/* 17:31 */     double[] histo = new double[256];
/* 18:33 */     for (int x = 0; x < img.getXDim(); x++) {
/* 19:34 */       for (int y = 0; y < img.getYDim(); y++)
/* 20:   */       {
/* 21:35 */         int p = img.getXYByte(x, y);
/* 22:36 */         histo[p] += 1.0D;
/* 23:   */       }
/* 24:   */     }
/* 25:43 */     double[] variances = new double['þ'];
/* 26:44 */     Arrays.fill(variances, 1.7976931348623157E+308D);
/* 27:46 */     for (int i = 1; i < 255; i++)
/* 28:   */     {
/* 29:50 */       double n1 = 0.0D;
/* 30:51 */       double n2 = 0.0D;
/* 31:   */       
/* 32:   */ 
/* 33:54 */       double mean1 = 0.0D;
/* 34:55 */       double mean2 = 0.0D;
/* 35:57 */       for (int j = 0; j < i; j++)
/* 36:   */       {
/* 37:58 */         n1 += histo[j];
/* 38:59 */         mean1 += histo[j] * j;
/* 39:   */       }
/* 40:62 */       for (int j = i; j < 256; j++)
/* 41:   */       {
/* 42:63 */         n2 += histo[j];
/* 43:64 */         mean2 += histo[j] * j;
/* 44:   */       }
/* 45:67 */       if ((n1 != 0.0D) && (n2 != 0.0D))
/* 46:   */       {
/* 47:70 */         mean1 /= n1;
/* 48:71 */         mean2 /= n2;
/* 49:   */         
/* 50:   */ 
/* 51:74 */         double var1 = 0.0D;
/* 52:75 */         double var2 = 0.0D;
/* 53:77 */         for (int j = 0; j < i; j++) {
/* 54:78 */           var1 += (j - mean1) * (j - mean1) * histo[j];
/* 55:   */         }
/* 56:80 */         for (int j = i; j < 256; j++) {
/* 57:81 */           var2 += (j - mean2) * (j - mean2) * histo[j];
/* 58:   */         }
/* 59:83 */         var1 /= n1;
/* 60:84 */         var2 /= n2;
/* 61:   */         
/* 62:86 */         variances[(i - 1)] = (n1 * var1 + n2 * var2);
/* 63:87 */         System.err.println(variances[(i - 1)]);
/* 64:   */       }
/* 65:   */     }
/* 66:90 */     int min = 0;
/* 67:91 */     for (int i = 1; i < variances.length; i++) {
/* 68:92 */       if (variances[i] < variances[min]) {
/* 69:93 */         min = i;
/* 70:   */       }
/* 71:   */     }
/* 72:95 */     System.err.println(min);
/* 73:   */     
/* 74:97 */     Display2D.invoke(Threshold.invoke(img, (min + 1) * 0.00392156862745098D));
/* 75:   */     
/* 76:99 */     Display2D.invoke(OtsuThreshold.invoke(img));
/* 77:   */   }
/* 78:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.Test4
 * JD-Core Version:    0.7.0.1
 */