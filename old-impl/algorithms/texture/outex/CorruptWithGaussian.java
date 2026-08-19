/*  1:   */ package vpt.algorithms.texture.outex;
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
/* 15:21 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Outex_TC_00012/images";
/* 16:22 */     String noisyPath = "/media/data/arge/veri_yedegi/texture_databases/Outex_TC_00012_16/images";
/* 17:   */     try
/* 18:   */     {
/* 19:26 */       File dir = new File(path);
/* 20:27 */       String[] filenames = dir.list();
/* 21:28 */       Arrays.sort(filenames);
/* 22:30 */       for (int i = 0; i < filenames.length; i++)
/* 23:   */       {
/* 24:31 */         Image img = Load.invoke(path + "/" + filenames[i]);
/* 25:   */         
/* 26:33 */         img = Gaussian.invoke(img, Double.valueOf(0.06274509803921569D));
/* 27:   */         
/* 28:35 */         Save.invoke(img, noisyPath + "/" + filenames[i]);
/* 29:   */         
/* 30:37 */         System.err.println(filenames[i]);
/* 31:   */       }
/* 32:   */     }
/* 33:   */     catch (Exception e)
/* 34:   */     {
/* 35:40 */       e.printStackTrace();
/* 36:   */     }
/* 37:   */   }
/* 38:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.CorruptWithGaussian
 * JD-Core Version:    0.7.0.1
 */