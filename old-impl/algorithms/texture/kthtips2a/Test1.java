/*  1:   */ package vpt.algorithms.texture.kthtips2a;
/*  2:   */ 
/*  3:   */ import java.io.BufferedWriter;
/*  4:   */ import java.io.File;
/*  5:   */ import java.io.FileWriter;
/*  6:   */ import java.io.PrintStream;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ import java.util.Arrays;
/*  9:   */ import vpt.Image;
/* 10:   */ import vpt.algorithms.conversion.RGB2Gray;
/* 11:   */ import vpt.algorithms.io.Load;
/* 12:   */ import vpt.algorithms.texture.ExtendedMorphologicalCovariance;
/* 13:   */ 
/* 14:   */ public class Test1
/* 15:   */ {
/* 16:   */   public static void main(String[] args)
/* 17:   */   {
/* 18:25 */     String path = "/home/yoktish/workspace/KTH-TIPS2-a/";
/* 19:26 */     int featureVectorLength = 24;
/* 20:   */     try
/* 21:   */     {
/* 22:29 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("kth-tips2a.dat")));
/* 23:   */       
/* 24:31 */       String[] materials = { "aluminium_foil", "brown_bread", "corduroy", "cork", "cotton", "cracker", "lettuce_leaf", "linen", "white_bread", "wood", "wool" };
/* 25:32 */       String[] samples = { "sample_a", "sample_b", "sample_c", "sample_d" };
/* 26:34 */       for (int i = 0; i < materials.length; i++) {
/* 27:36 */         for (int j = 0; j < samples.length; j++)
/* 28:   */         {
/* 29:37 */           File dir = new File(path + materials[i] + "/" + samples[j]);
/* 30:38 */           String[] filenames = dir.list();
/* 31:39 */           Arrays.sort(filenames);
/* 32:41 */           for (int k = 0; k < filenames.length; k++)
/* 33:   */           {
/* 34:42 */             Image img = Load.invoke(path + materials[i] + "/" + samples[j] + "/" + filenames[k]);
/* 35:   */             
/* 36:44 */             img = RGB2Gray.invoke(img);
/* 37:   */             
/* 38:46 */             double[] feature = ExtendedMorphologicalCovariance.invoke(img, Integer.valueOf(12));
/* 39:48 */             for (int l = 0; l < feature.length; l++) {
/* 40:49 */               pw.print(feature[l] + ",");
/* 41:   */             }
/* 42:51 */             pw.println(i);
/* 43:   */             
/* 44:53 */             System.err.println("Material : " + i + ", sample : " + j + ", image : " + k);
/* 45:   */           }
/* 46:56 */           pw.println(" ");
/* 47:   */         }
/* 48:   */       }
/* 49:59 */       pw.close();
/* 50:   */     }
/* 51:   */     catch (Exception e)
/* 52:   */     {
/* 53:61 */       e.printStackTrace();
/* 54:   */     }
/* 55:   */   }
/* 56:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2a.Test1
 * JD-Core Version:    0.7.0.1
 */