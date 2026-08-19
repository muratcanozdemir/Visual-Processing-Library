/*  1:   */ package vpt.algorithms.texture.curet;
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
/* 15:21 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Curet/sample";
/* 16:22 */     String noisyPath = "/media/data/arge/veri_yedegi/texture_databases/Curet32/sample";
/* 17:   */     try
/* 18:   */     {
/* 19:25 */       for (int i = 1; i <= 61; i++)
/* 20:   */       {
/* 21:26 */         String sampleNo = "";
/* 22:27 */         if (i < 10) {
/* 23:27 */           sampleNo = "0" + i;
/* 24:   */         } else {
/* 25:28 */           sampleNo = i;
/* 26:   */         }
/* 27:30 */         File dir = new File(path + sampleNo);
/* 28:31 */         String[] filenames = dir.list();
/* 29:32 */         Arrays.sort(filenames);
/* 30:34 */         for (int j = 0; j < 92; j++)
/* 31:   */         {
/* 32:35 */           Image img = Load.invoke(path + sampleNo + "/" + filenames[j]);
/* 33:   */           
/* 34:37 */           img = Gaussian.invoke(img, Double.valueOf(0.1254901960784314D));
/* 35:   */           
/* 36:39 */           Save.invoke(img, noisyPath + sampleNo + "/" + filenames[j]);
/* 37:   */           
/* 38:41 */           System.err.println("Sample : " + i + ", image : " + (j + 1));
/* 39:   */         }
/* 40:   */       }
/* 41:   */     }
/* 42:   */     catch (Exception e)
/* 43:   */     {
/* 44:45 */       e.printStackTrace();
/* 45:   */     }
/* 46:   */   }
/* 47:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.CorruptWithGaussian
 * JD-Core Version:    0.7.0.1
 */