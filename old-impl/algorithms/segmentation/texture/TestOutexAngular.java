/*  1:   */ package vpt.algorithms.segmentation.texture;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.PrintStream;
/*  5:   */ import java.util.Arrays;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.IntegerImage;
/*  8:   */ import vpt.algorithms.flatzones.gray.GrayQFZSoille;
/*  9:   */ import vpt.algorithms.io.Load;
/* 10:   */ import vpt.algorithms.segmentation.MP;
/* 11:   */ import vpt.algorithms.segmentation.OSR;
/* 12:   */ import vpt.algorithms.texture.OrientationMap;
/* 13:   */ 
/* 14:   */ public class TestOutexAngular
/* 15:   */ {
/* 16:   */   public static void main(String[] args)
/* 17:   */   {
/* 18:36 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Outex_SS_00000/";
/* 19:   */     
/* 20:38 */     File dir = new File(path);
/* 21:39 */     String[] folders = dir.list();
/* 22:40 */     Arrays.sort(folders);
/* 23:   */     
/* 24:42 */     Image tmp = Load.invoke(path + "ground_truth.png");
/* 25:   */     
/* 26:44 */     Image reference = new IntegerImage(tmp, false);
/* 27:46 */     for (int i = 0; i < tmp.getSize(); i++) {
/* 28:47 */       reference.setInt(i, tmp.getByte(i) - 1);
/* 29:   */     }
/* 30:49 */     for (double alpha = 0.01D; alpha <= 0.39D; alpha += 0.01D)
/* 31:   */     {
/* 32:51 */       double mp = 0.0D;
/* 33:52 */       double osr = 0.0D;
/* 34:54 */       for (int i = 0; i < 100; i++)
/* 35:   */       {
/* 36:56 */         String imgName = "";
/* 37:58 */         if (i < 10) {
/* 38:58 */           imgName = "00" + i;
/* 39:   */         } else {
/* 40:59 */           imgName = "0" + i;
/* 41:   */         }
/* 42:63 */         Image img = Load.invoke(path + imgName + "/problem.png");
/* 43:64 */         img = OrientationMap.invoke(img, Integer.valueOf(11));
/* 44:   */         
/* 45:   */ 
/* 46:   */ 
/* 47:68 */         Image map = GrayQFZSoille.invoke(img, (int)Math.round(alpha * 2.0D * 255.0D), (int)Math.round(alpha * 2.0D * 255.0D));
/* 48:   */         
/* 49:70 */         double tmpOSR = OSR.invoke(map, reference).doubleValue();
/* 50:71 */         double tmpMP = MP.invoke(map, reference).doubleValue();
/* 51:   */         
/* 52:   */ 
/* 53:   */ 
/* 54:75 */         osr += tmpOSR;
/* 55:76 */         mp += tmpMP;
/* 56:   */       }
/* 57:79 */       mp /= 100.0D;
/* 58:80 */       osr /= 100.0D;
/* 59:   */       
/* 60:82 */       System.err.println(osr + "\t" + mp);
/* 61:   */     }
/* 62:   */   }
/* 63:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.texture.TestOutexAngular
 * JD-Core Version:    0.7.0.1
 */