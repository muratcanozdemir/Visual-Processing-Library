/*  1:   */ package vpt.algorithms.texture.kthtips;
/*  2:   */ 
/*  3:   */ import java.io.BufferedWriter;
/*  4:   */ import java.io.File;
/*  5:   */ import java.io.FileWriter;
/*  6:   */ import java.io.PrintStream;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ import java.util.Arrays;
/*  9:   */ import vpt.Image;
/* 10:   */ import vpt.algorithms.io.Load;
/* 11:   */ import vpt.experimental.texture.rotInv.RotationInvariantPointsMed;
/* 12:   */ 
/* 13:   */ public class Test3
/* 14:   */ {
/* 15:   */   public static void main(String[] args)
/* 16:   */   {
/* 17:24 */     String path = "/home/erhan/workspace/KTH_TIPS/";
/* 18:25 */     int featureVectorLength = 24;
/* 19:   */     try
/* 20:   */     {
/* 21:28 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("kth-tips-med.dat")));
/* 22:   */       
/* 23:30 */       String[] materials = { "aluminium_foil", "brown_bread", "corduroy", "cotton", "cracker", "linen", "orange_peel", "sandpaper", "sponge", "styrofoam" };
/* 24:32 */       for (int i = 0; i < materials.length; i++)
/* 25:   */       {
/* 26:33 */         File dir = new File(path + materials[i]);
/* 27:34 */         String[] filenames = dir.list();
/* 28:35 */         Arrays.sort(filenames);
/* 29:37 */         for (int k = 0; k < filenames.length; k++)
/* 30:   */         {
/* 31:38 */           Image img = Load.invoke(path + materials[i] + "/" + filenames[k]);
/* 32:   */           
/* 33:   */ 
/* 34:   */ 
/* 35:42 */           double[] feature = RotationInvariantPointsMed.invoke(img, Integer.valueOf(12));
/* 36:44 */           for (int l = 0; l < feature.length; l++) {
/* 37:45 */             pw.print(feature[l] + ",");
/* 38:   */           }
/* 39:47 */           pw.println(i);
/* 40:   */           
/* 41:49 */           System.err.println("Material : " + i + ", image : " + k);
/* 42:   */         }
/* 43:   */       }
/* 44:53 */       pw.close();
/* 45:   */     }
/* 46:   */     catch (Exception e)
/* 47:   */     {
/* 48:55 */       e.printStackTrace();
/* 49:   */     }
/* 50:   */   }
/* 51:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips.Test3
 * JD-Core Version:    0.7.0.1
 */