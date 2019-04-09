/*   1:    */ package vpt.algorithms.segmentation.berkeley;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.conversion.RGB2XYZ;
/*   8:    */ import vpt.algorithms.conversion.XYZ2LAB;
/*   9:    */ import vpt.algorithms.io.Load;
/*  10:    */ import vpt.algorithms.segmentation.MP;
/*  11:    */ import vpt.algorithms.segmentation.OSR;
/*  12:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  13:    */ import vpt.util.ordering.Lexico;
/*  14:    */ 
/*  15:    */ public class TestBerkeley2
/*  16:    */ {
/*  17:    */   public static void main(String[] args)
/*  18:    */   {
/*  19: 35 */     String path = "/media/data/arge/veri_yedegi/Berkeley_300/images/all/";
/*  20: 36 */     String human = "/media/data/arge/veri_yedegi/Berkeley_300/human/color/";
/*  21:    */     
/*  22: 38 */     String[] experts = { "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112", "1113", "1114", 
/*  23: 39 */       "1115", "1116", "1117", "1118", "1119", "1121", "1122", "1123", "1124", "1126", "1127", "1128", "1129", "1130", "1132" };
/*  24:    */     
/*  25:    */ 
/*  26: 42 */     File dir = new File(path);
/*  27: 43 */     String[] images = dir.list();
/*  28: 44 */     Arrays.sort(images);
/*  29:    */     
/*  30:    */ 
/*  31: 47 */     ColorVector[] cv = new ColorVector[16777216];
/*  32: 48 */     AlgebraicOrdering vo = new Lexico();
/*  33: 49 */     initColorVectors(cv, vo);
/*  34: 52 */     for (int a = 10; a <= 100; a += 10)
/*  35:    */     {
/*  36: 54 */       double mp = 0.0D;
/*  37: 55 */       double osr = 0.0D;
/*  38: 58 */       for (int i = 0; i < 30; i++)
/*  39:    */       {
/*  40: 60 */         String imageNumber = images[i].substring(0, images[i].indexOf('.'));
/*  41:    */         
/*  42: 62 */         System.err.println("Testing image " + images[i] + " with alpha-omega " + a);
/*  43:    */         
/*  44: 64 */         Image img = Load.invoke(path + images[i]);
/*  45: 65 */         img = RGB2XYZ.invoke(img);
/*  46: 66 */         img = XYZ2LAB.invoke(img);
/*  47: 67 */         img = XYZ2LAB.scaleToByte(img);
/*  48: 68 */         Image map = ColorQFZRGB.invoke(img, new int[] { a, a, a }, new int[] { a, a, a }, cv, vo);
/*  49:    */         
/*  50: 70 */         double tempMP = 0.0D;
/*  51: 71 */         double tempOSR = 0.0D;
/*  52: 72 */         int count = 0;
/*  53: 75 */         for (int j = 0; j < experts.length; j++)
/*  54:    */         {
/*  55: 76 */           File humanDir = new File(human + experts[j]);
/*  56: 77 */           String[] referenceMaps = humanDir.list();
/*  57: 80 */           for (int k = 0; k < referenceMaps.length; k++) {
/*  58: 81 */             if (referenceMaps[k].substring(0, referenceMaps[k].indexOf('.')).equals(imageNumber))
/*  59:    */             {
/*  60: 82 */               Image reference = Seg2Image.invoke(human + experts[j] + "/" + referenceMaps[k]);
/*  61:    */               
/*  62: 84 */               tempOSR += OSR.invoke(map, reference).doubleValue();
/*  63: 85 */               tempMP += MP.invoke(map, reference).doubleValue();
/*  64: 86 */               count++;
/*  65:    */             }
/*  66:    */           }
/*  67:    */         }
/*  68: 92 */         if (count == 0) {
/*  69: 92 */           System.err.println("no reference map can be found");
/*  70:    */         }
/*  71: 94 */         mp += tempMP / count;
/*  72: 95 */         osr += tempOSR / count;
/*  73:    */       }
/*  74: 98 */       mp /= 30.0D;
/*  75: 99 */       osr /= 30.0D;
/*  76:    */       
/*  77:101 */       System.err.println(osr + "\t" + mp);
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   private static void initColorVectors(ColorVector[] cv, AlgebraicOrdering vo)
/*  82:    */   {
/*  83:107 */     int j = 0;
/*  84:108 */     for (int r = 0; r < 256; r++) {
/*  85:109 */       for (int g = 0; g < 256; g++) {
/*  86:110 */         for (int b = 0; b < 256; b++) {
/*  87:111 */           cv[(j++)] = new ColorVector(r, g, b, vo);
/*  88:    */         }
/*  89:    */       }
/*  90:    */     }
/*  91:116 */     Arrays.sort(cv);
/*  92:    */   }
/*  93:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.TestBerkeley2
 * JD-Core Version:    0.7.0.1
 */