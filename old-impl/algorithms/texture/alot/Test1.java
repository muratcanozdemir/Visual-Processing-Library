/*  1:   */ package vpt.algorithms.texture.alot;
/*  2:   */ 
/*  3:   */ import java.io.BufferedWriter;
/*  4:   */ import java.io.File;
/*  5:   */ import java.io.FileWriter;
/*  6:   */ import java.io.PrintStream;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ import java.util.Arrays;
/*  9:   */ import vpt.Image;
/* 10:   */ import vpt.algorithms.io.Load;
/* 11:   */ import vpt.algorithms.texture.ExtendedMorphologicalCovariance;
/* 12:   */ 
/* 13:   */ public class Test1
/* 14:   */ {
/* 15:   */   public static void main(String[] args)
/* 16:   */   {
/* 17:23 */     String path = "/home/user/workspace/grey/";
/* 18:   */     try
/* 19:   */     {
/* 20:26 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("alot-train-194.dat")));
/* 21:28 */       for (int i = 1; i <= 200; i++)
/* 22:   */       {
/* 23:30 */         File dir = new File(path + i);
/* 24:31 */         String[] filenames = dir.list();
/* 25:32 */         Arrays.sort(filenames);
/* 26:34 */         for (int k = 0; k < filenames.length; k++) {
/* 27:36 */           if ((k == 1) || (k == 2) || (k == 13) || (k == 14) || (k == 21) || (k == 22) || (k == 76) || (k == 77) || (k == 88) || (k == 89) || (k == 96) || (k == 97))
/* 28:   */           {
/* 29:38 */             Image img = Load.invoke(path + i + "/" + filenames[k]);
/* 30:   */             
/* 31:40 */             double[] feature = ExtendedMorphologicalCovariance.invoke(img, Integer.valueOf(12));
/* 32:42 */             for (int l = 0; l < feature.length; l++) {
/* 33:43 */               pw.print(feature[l] + ",");
/* 34:   */             }
/* 35:45 */             pw.println(i - 1);
/* 36:   */             
/* 37:47 */             System.err.println("Material : " + i + ", image : " + k);
/* 38:   */           }
/* 39:   */         }
/* 40:   */       }
/* 41:51 */       pw.close();
/* 42:   */     }
/* 43:   */     catch (Exception e)
/* 44:   */     {
/* 45:80 */       e.printStackTrace();
/* 46:   */     }
/* 47:   */   }
/* 48:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.alot.Test1
 * JD-Core Version:    0.7.0.1
 */