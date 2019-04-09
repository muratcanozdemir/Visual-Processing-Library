/*  1:   */ package vpt.algorithms.segmentation.berkeley;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.PrintStream;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.flatzones.color.ColorQFZRGBMarginal;
/*  7:   */ import vpt.algorithms.io.Load;
/*  8:   */ import vpt.algorithms.segmentation.MP;
/*  9:   */ import vpt.algorithms.segmentation.MergeSingletons;
/* 10:   */ import vpt.algorithms.segmentation.OSR;
/* 11:   */ 
/* 12:   */ public class TestBerkeley
/* 13:   */ {
/* 14:   */   public static void main(String[] args)
/* 15:   */   {
/* 16:28 */     String path = "/media/data/arge/veri_yedegi/Berkeley_300/images/all/";
/* 17:29 */     String human = "/media/data/arge/veri_yedegi/Berkeley_300/human/color/";
/* 18:   */     
/* 19:   */ 
/* 20:32 */     String[] experts = { "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112", "1113", "1114", 
/* 21:33 */       "1115", "1116", "1117", "1118", "1119", "1121", "1122", "1123", "1124", "1126", "1127", "1128", "1129", "1130", "1132" };
/* 22:   */     
/* 23:   */ 
/* 24:36 */     File dir = new File(path);
/* 25:37 */     String[] images = dir.list();
/* 26:40 */     for (int a = 10; a <= 150; a += 10)
/* 27:   */     {
/* 28:42 */       double mp = 0.0D;
/* 29:43 */       double osr = 0.0D;
/* 30:46 */       for (int i = 0; i < images.length; i++)
/* 31:   */       {
/* 32:48 */         String imageNumber = images[i].substring(0, images[i].indexOf('.'));
/* 33:   */         
/* 34:   */ 
/* 35:   */ 
/* 36:52 */         Image img = Load.invoke(path + images[i]);
/* 37:53 */         Image map = ColorQFZRGBMarginal.invoke(img, a, a);
/* 38:54 */         map = MergeSingletons.invoke(map, img);
/* 39:   */         
/* 40:56 */         double tempMP = 0.0D;
/* 41:57 */         double tempOSR = 0.0D;
/* 42:58 */         int count = 0;
/* 43:61 */         for (int j = 0; j < experts.length; j++)
/* 44:   */         {
/* 45:62 */           File humanDir = new File(human + experts[j]);
/* 46:63 */           String[] referenceMaps = humanDir.list();
/* 47:66 */           for (int k = 0; k < referenceMaps.length; k++) {
/* 48:67 */             if (referenceMaps[k].substring(0, referenceMaps[k].indexOf('.')).equals(imageNumber))
/* 49:   */             {
/* 50:68 */               Image reference = Seg2Image.invoke(human + experts[j] + "/" + referenceMaps[k]);
/* 51:   */               
/* 52:70 */               tempOSR += OSR.invoke(map, reference).doubleValue();
/* 53:71 */               tempMP += MP.invoke(map, reference).doubleValue();
/* 54:72 */               count++;
/* 55:   */             }
/* 56:   */           }
/* 57:   */         }
/* 58:77 */         if (count == 0) {
/* 59:77 */           System.err.println("no reference map can be found");
/* 60:   */         }
/* 61:79 */         mp += tempMP / count;
/* 62:80 */         osr += tempOSR / count;
/* 63:   */       }
/* 64:83 */       mp /= images.length;
/* 65:84 */       osr /= images.length;
/* 66:   */       
/* 67:86 */       System.err.println(osr + "\t" + mp);
/* 68:   */     }
/* 69:   */   }
/* 70:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.TestBerkeley
 * JD-Core Version:    0.7.0.1
 */