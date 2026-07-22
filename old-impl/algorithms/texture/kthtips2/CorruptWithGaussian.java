/*  1:   */ package vpt.algorithms.texture.kthtips2;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.PrintStream;
/*  5:   */ import java.util.Arrays;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.io.Load;
/*  8:   */ import vpt.algorithms.io.Save;
/*  9:   */ import vpt.algorithms.noise.Gaussian;
/* 10:   */ 
/* 11:   */ public class CorruptWithGaussian
/* 12:   */ {
/* 13:   */   public static void main(String[] args)
/* 14:   */   {
/* 15:22 */     String path = "/media/data/arge/veri_yedegi/texture_databases/KTH-TIPS2-b/";
/* 16:23 */     String noisyPath = "/media/data/arge/veri_yedegi/texture_databases/KTH-TIPS2-b_32/";
/* 17:   */     try
/* 18:   */     {
/* 19:27 */       String[] materials = { "aluminium_foil", "brown_bread", "corduroy", "cork", "cotton", "cracker", "lettuce_leaf", "linen", "white_bread", "wood", "wool" };
/* 20:28 */       String[] samples = { "sample_a", "sample_b", "sample_c", "sample_d" };
/* 21:30 */       for (int i = 0; i < materials.length; i++) {
/* 22:32 */         for (int j = 0; j < samples.length; j++)
/* 23:   */         {
/* 24:33 */           File dir = new File(path + materials[i] + "/" + samples[j]);
/* 25:34 */           String[] filenames = dir.list();
/* 26:35 */           Arrays.sort(filenames);
/* 27:37 */           for (int k = 0; k < filenames.length; k++)
/* 28:   */           {
/* 29:38 */             Image img = Load.invoke(path + materials[i] + "/" + samples[j] + "/" + filenames[k]);
/* 30:   */             
/* 31:40 */             img = Gaussian.invoke(img, Double.valueOf(0.1254901960784314D));
/* 32:   */             
/* 33:42 */             Save.invoke(img, noisyPath + materials[i] + "/" + samples[j] + "/" + filenames[k]);
/* 34:   */             
/* 35:44 */             System.err.println("Material : " + i + ", sample : " + j + ", image : " + k);
/* 36:   */           }
/* 37:   */         }
/* 38:   */       }
/* 39:   */     }
/* 40:   */     catch (Exception e)
/* 41:   */     {
/* 42:49 */       e.printStackTrace();
/* 43:   */     }
/* 44:   */   }
/* 45:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.CorruptWithGaussian
 * JD-Core Version:    0.7.0.1
 */