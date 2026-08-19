/*  1:   */ package vpt.algorithms.segmentation.berkeley;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.PrintStream;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.conversion.RGB2Gray;
/*  7:   */ import vpt.algorithms.flatzones.gray.GrayQFZSoille;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ import vpt.algorithms.segmentation.MP;
/* 10:   */ import vpt.algorithms.segmentation.OSR;
/* 11:   */ 
/* 12:   */ public class TestBerkeleyAngular
/* 13:   */ {
/* 14:   */   public static void main(String[] args)
/* 15:   */   {
/* 16:32 */     String path = "/media/data/arge/veri_yedegi/Berkeley_300/images/all/";
/* 17:33 */     String human = "/media/data/arge/veri_yedegi/Berkeley_300/human/color/";
/* 18:   */     
/* 19:   */ 
/* 20:36 */     String[] experts = { "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112", "1113", "1114", 
/* 21:37 */       "1115", "1116", "1117", "1118", "1119", "1121", "1122", "1123", "1124", "1126", "1127", "1128", "1129", "1130", "1132" };
/* 22:   */     
/* 23:   */ 
/* 24:40 */     File dir = new File(path);
/* 25:41 */     String[] images = dir.list();
/* 26:44 */     for (double alpha = 0.1D; alpha <= 0.1D; alpha += 0.01D)
/* 27:   */     {
/* 28:46 */       double mp = 0.0D;
/* 29:47 */       double osr = 0.0D;
/* 30:50 */       for (int i = 0; i < images.length; i++)
/* 31:   */       {
/* 32:52 */         String imageNumber = images[i].substring(0, images[i].indexOf('.'));
/* 33:   */         
/* 34:54 */         System.err.println("Testing image " + i + " " + images[i] + " with alpha-omega " + alpha);
/* 35:   */         
/* 36:56 */         Image img = Load.invoke(path + images[i]);
/* 37:   */         
/* 38:   */ 
/* 39:59 */         Image map = GrayQFZSoille.invoke(RGB2Gray.invoke(img), (int)Math.round(alpha * 2.0D * 255.0D), (int)Math.round(alpha * 2.0D * 255.0D));
/* 40:   */         
/* 41:61 */         double tempMP = 0.0D;
/* 42:62 */         double tempOSR = 0.0D;
/* 43:63 */         int count = 0;
/* 44:66 */         for (int j = 0; j < experts.length; j++)
/* 45:   */         {
/* 46:67 */           File humanDir = new File(human + experts[j]);
/* 47:68 */           String[] referenceMaps = humanDir.list();
/* 48:71 */           for (int k = 0; k < referenceMaps.length; k++) {
/* 49:72 */             if (referenceMaps[k].substring(0, referenceMaps[k].indexOf('.')).equals(imageNumber))
/* 50:   */             {
/* 51:73 */               Image reference = Seg2Image.invoke(human + experts[j] + "/" + referenceMaps[k]);
/* 52:   */               
/* 53:75 */               tempOSR += OSR.invoke(map, reference).doubleValue();
/* 54:76 */               tempMP += MP.invoke(map, reference).doubleValue();
/* 55:77 */               count++;
/* 56:   */             }
/* 57:   */           }
/* 58:   */         }
/* 59:82 */         if (count == 0) {
/* 60:82 */           System.err.println("no reference map can be found");
/* 61:   */         }
/* 62:84 */         mp += tempMP / count;
/* 63:85 */         osr += tempOSR / count;
/* 64:   */       }
/* 65:88 */       mp /= images.length;
/* 66:89 */       osr /= images.length;
/* 67:   */       
/* 68:91 */       System.err.println(osr + "\t" + mp);
/* 69:   */     }
/* 70:   */   }
/* 71:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.TestBerkeleyAngular
 * JD-Core Version:    0.7.0.1
 */