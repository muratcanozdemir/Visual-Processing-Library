/*  1:   */ package vpt.algorithms.segmentation.berkeley;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.PrintStream;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.conversion.RGB2HSY;
/*  7:   */ import vpt.algorithms.flatzones.angular.LabelBasedAngularQFZ;
/*  8:   */ import vpt.algorithms.io.Load;
/*  9:   */ import vpt.algorithms.segmentation.MP;
/* 10:   */ import vpt.algorithms.segmentation.OSR;
/* 11:   */ 
/* 12:   */ public class TestBerkeleyAngular2
/* 13:   */ {
/* 14:   */   public static void main(String[] args)
/* 15:   */   {
/* 16:31 */     String path = "/media/data/arge/veri_yedegi/Berkeley_300/images/all/";
/* 17:32 */     String human = "/media/data/arge/veri_yedegi/Berkeley_300/human/color/";
/* 18:   */     
/* 19:   */ 
/* 20:35 */     String[] experts = { "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112", "1113", "1114", 
/* 21:36 */       "1115", "1116", "1117", "1118", "1119", "1121", "1122", "1123", "1124", "1126", "1127", "1128", "1129", "1130", "1132" };
/* 22:   */     
/* 23:   */ 
/* 24:39 */     File dir = new File(path);
/* 25:40 */     String[] images = dir.list();
/* 26:43 */     for (double alpha = 0.01D; alpha <= 0.1D; alpha += 0.01D)
/* 27:   */     {
/* 28:45 */       double mp = 0.0D;
/* 29:46 */       double osr = 0.0D;
/* 30:49 */       for (int i = 0; i < images.length; i++)
/* 31:   */       {
/* 32:51 */         String imageNumber = images[i].substring(0, images[i].indexOf('.'));
/* 33:   */         
/* 34:   */ 
/* 35:   */ 
/* 36:55 */         Image img = Load.invoke(path + images[i]);
/* 37:56 */         img = RGB2HSY.invoke(img);
/* 38:57 */         Image map = LabelBasedAngularQFZ.invoke(img.getChannel(0), (int)Math.round(alpha * 2.0D * 255.0D), (int)Math.round(alpha * 2.0D * 255.0D), 0.66D);
/* 39:   */         
/* 40:59 */         double tempMP = 0.0D;
/* 41:60 */         double tempOSR = 0.0D;
/* 42:61 */         int count = 0;
/* 43:64 */         for (int j = 0; j < experts.length; j++)
/* 44:   */         {
/* 45:65 */           File humanDir = new File(human + experts[j]);
/* 46:66 */           String[] referenceMaps = humanDir.list();
/* 47:69 */           for (int k = 0; k < referenceMaps.length; k++) {
/* 48:70 */             if (referenceMaps[k].substring(0, referenceMaps[k].indexOf('.')).equals(imageNumber))
/* 49:   */             {
/* 50:71 */               Image reference = Seg2Image.invoke(human + experts[j] + "/" + referenceMaps[k]);
/* 51:   */               
/* 52:73 */               tempOSR += OSR.invoke(map, reference).doubleValue();
/* 53:74 */               tempMP += MP.invoke(map, reference).doubleValue();
/* 54:75 */               count++;
/* 55:   */             }
/* 56:   */           }
/* 57:   */         }
/* 58:80 */         if (count == 0) {
/* 59:80 */           System.err.println("no reference map can be found");
/* 60:   */         }
/* 61:82 */         mp += tempMP / count;
/* 62:83 */         osr += tempOSR / count;
/* 63:   */       }
/* 64:86 */       mp /= images.length;
/* 65:87 */       osr /= images.length;
/* 66:   */       
/* 67:89 */       System.err.println(osr + "\t" + mp + "\t" + Math.round(alpha * 2.0D * 255.0D));
/* 68:   */     }
/* 69:   */   }
/* 70:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.TestBerkeleyAngular2
 * JD-Core Version:    0.7.0.1
 */