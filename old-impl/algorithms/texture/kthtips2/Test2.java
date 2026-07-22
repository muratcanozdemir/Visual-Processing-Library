/*  1:   */ package vpt.algorithms.texture.kthtips2;
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
/* 12:   */ import vpt.algorithms.texture.RotationInvariantMorphologicalCovariance;
/* 13:   */ 
/* 14:   */ public class Test2
/* 15:   */ {
/* 16:   */   public static void main(String[] args)
/* 17:   */   {
/* 18:23 */     String path = "/media/data/arge/veri_yedegi/texture_databases/KTH-TIPS2-b_8/";
/* 19:   */     try
/* 20:   */     {
/* 21:26 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("test.dat")));
/* 22:   */       
/* 23:28 */       String[] materials = { "aluminium_foil", "brown_bread", "corduroy", "cork", "cotton", "cracker", "lettuce_leaf", "linen", "white_bread", "wood", "wool" };
/* 24:29 */       String[] samples = { "sample_a", "sample_b", "sample_c", "sample_d" };
/* 25:31 */       for (int i = 5; i < materials.length; i++) {
/* 26:33 */         for (int j = 0; j < samples.length; j++)
/* 27:   */         {
/* 28:34 */           File dir = new File(path + materials[i] + "/" + samples[j]);
/* 29:35 */           String[] filenames = dir.list();
/* 30:36 */           Arrays.sort(filenames);
/* 31:38 */           for (int k = 0; k < filenames.length; k++)
/* 32:   */           {
/* 33:39 */             Image img = Load.invoke(path + materials[i] + "/" + samples[j] + "/" + filenames[k]);
/* 34:   */             
/* 35:41 */             img = RGB2Gray.invoke(img);
/* 36:   */             
/* 37:43 */             double[] feature = RotationInvariantMorphologicalCovariance.invoke(img, Integer.valueOf(12), Integer.valueOf(1), Integer.valueOf(3));
/* 38:46 */             for (int l = 0; l < feature.length; l++) {
/* 39:47 */               pw.print(feature[l] + ",");
/* 40:   */             }
/* 41:49 */             pw.println(i);
/* 42:   */             
/* 43:51 */             System.err.println("Material : " + i + ", sample : " + j + ", image : " + k);
/* 44:   */           }
/* 45:   */         }
/* 46:   */       }
/* 47:55 */       pw.close();
/* 48:   */     }
/* 49:   */     catch (Exception e)
/* 50:   */     {
/* 51:57 */       e.printStackTrace();
/* 52:   */     }
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.Test2
 * JD-Core Version:    0.7.0.1
 */