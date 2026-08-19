/*  1:   */ package vpt.algorithms.texture.kthtips2;
/*  2:   */ 
/*  3:   */ import java.io.BufferedWriter;
/*  4:   */ import java.io.File;
/*  5:   */ import java.io.FileWriter;
/*  6:   */ import java.io.PrintStream;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ import java.util.Arrays;
/*  9:   */ import vpt.Image;
/* 10:   */ import vpt.algorithms.conversion.RGB2NUHSY733;
/* 11:   */ import vpt.algorithms.io.Load;
/* 12:   */ 
/* 13:   */ public class Test1
/* 14:   */ {
/* 15:   */   public static void main(String[] args)
/* 16:   */   {
/* 17:23 */     String path = "/home/yoktish/workspace/KTH-TIPS2-b/";
/* 18:   */     try
/* 19:   */     {
/* 20:26 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("tips2b-huehisto733.dat")));
/* 21:   */       
/* 22:28 */       String[] materials = { "aluminium_foil", "brown_bread", "corduroy", "cork", "cotton", "cracker", "lettuce_leaf", "linen", "white_bread", "wood", "wool" };
/* 23:29 */       String[] samples = { "sample_a", "sample_b", "sample_c", "sample_d" };
/* 24:31 */       for (int i = 0; i < materials.length; i++) {
/* 25:33 */         for (int j = 0; j < samples.length; j++)
/* 26:   */         {
/* 27:34 */           File dir = new File(path + materials[i] + "/" + samples[j]);
/* 28:35 */           String[] filenames = dir.list();
/* 29:36 */           Arrays.sort(filenames);
/* 30:38 */           for (int k = 0; k < filenames.length; k++)
/* 31:   */           {
/* 32:39 */             Image img = Load.invoke(path + materials[i] + "/" + samples[j] + "/" + filenames[k]);
/* 33:   */             
/* 34:41 */             img = RGB2NUHSY733.invoke(img);
/* 35:42 */             img = img.getChannel(0);
/* 36:   */             
/* 37:44 */             double[] feature = new double[7];
/* 38:45 */             Arrays.fill(feature, 0.0D);
/* 39:47 */             for (int m = 0; m < img.getSize(); m++)
/* 40:   */             {
/* 41:48 */               int p = img.getByte(m);
/* 42:   */               
/* 43:50 */               feature[p] += 1.0D;
/* 44:   */             }
/* 45:53 */             for (int m = 0; m < 7; m++) {
/* 46:54 */               feature[m] /= img.getSize();
/* 47:   */             }
/* 48:56 */             for (int l = 0; l < feature.length; l++) {
/* 49:57 */               pw.print(feature[l] + ",");
/* 50:   */             }
/* 51:59 */             pw.println(i);
/* 52:   */             
/* 53:61 */             System.err.println("Material : " + i + ", sample : " + j + ", image : " + k);
/* 54:   */           }
/* 55:   */         }
/* 56:   */       }
/* 57:65 */       pw.close();
/* 58:   */     }
/* 59:   */     catch (Exception e)
/* 60:   */     {
/* 61:67 */       e.printStackTrace();
/* 62:   */     }
/* 63:   */   }
/* 64:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.Test1
 * JD-Core Version:    0.7.0.1
 */