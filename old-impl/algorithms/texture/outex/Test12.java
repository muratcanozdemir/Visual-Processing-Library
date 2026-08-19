/*   1:    */ package vpt.algorithms.texture.outex;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.flatzones.gray.GrayQFZLocalRanges;
/*   9:    */ import vpt.algorithms.histogram.Histogram;
/*  10:    */ import vpt.algorithms.io.Load;
/*  11:    */ 
/*  12:    */ public class Test12
/*  13:    */ {
/*  14:    */   public static void main(String[] args)
/*  15:    */   {
/*  16: 34 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Outex_TC_00012/images/";
/*  17: 35 */     int featureVectorLength = 30;
/*  18:    */     try
/*  19:    */     {
/*  20: 38 */       String s = null;
/*  21:    */       
/*  22: 40 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("train1.arff")));
/*  23:    */       
/*  24: 42 */       printHeader(pw, featureVectorLength);
/*  25: 44 */       for (int i = 0; i < 480; i++)
/*  26:    */       {
/*  27: 45 */         if (i < 10) {
/*  28: 45 */           s = "00000";
/*  29: 46 */         } else if (i < 100) {
/*  30: 46 */           s = "0000";
/*  31: 47 */         } else if (i < 1000) {
/*  32: 47 */           s = "000";
/*  33:    */         } else {
/*  34: 48 */           s = "00";
/*  35:    */         }
/*  36: 50 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  37:    */         
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41: 55 */         Image img2 = GrayQFZLocalRanges.invoke(img, 40, 40);
/*  42: 56 */         double[] feature = Histogram.invoke(img2, Boolean.valueOf(true));
/*  43: 58 */         for (int j = 0; j < 30; j++) {
/*  44: 59 */           pw.print(feature[j] + ",");
/*  45:    */         }
/*  46: 61 */         pw.println(i / 20);
/*  47:    */         
/*  48: 63 */         System.err.println(i + " train");
/*  49:    */       }
/*  50: 65 */       pw.close();
/*  51:    */       
/*  52:    */ 
/*  53:    */ 
/*  54: 69 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("test1a.arff")));
/*  55:    */       
/*  56: 71 */       printHeader(pw2, featureVectorLength);
/*  57: 73 */       for (int i = 480; i < 4800; i++)
/*  58:    */       {
/*  59: 74 */         if (i < 10) {
/*  60: 74 */           s = "00000";
/*  61: 75 */         } else if (i < 100) {
/*  62: 75 */           s = "0000";
/*  63: 76 */         } else if (i < 1000) {
/*  64: 76 */           s = "000";
/*  65:    */         } else {
/*  66: 77 */           s = "00";
/*  67:    */         }
/*  68: 79 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  69:    */         
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74: 85 */         Image img2 = GrayQFZLocalRanges.invoke(img, 40, 40);
/*  75: 86 */         double[] feature = Histogram.invoke(img2, Boolean.valueOf(true));
/*  76: 88 */         for (int j = 0; j < 30; j++) {
/*  77: 89 */           pw2.print(feature[j] + ",");
/*  78:    */         }
/*  79: 91 */         pw2.println((i - 480) / 180);
/*  80:    */         
/*  81: 93 */         System.err.println(i + " test_tl84");
/*  82:    */       }
/*  83: 95 */       pw2.close();
/*  84:    */       
/*  85:    */ 
/*  86:    */ 
/*  87: 99 */       PrintWriter pw3 = new PrintWriter(new BufferedWriter(new FileWriter("test1b.arff")));
/*  88:    */       
/*  89:101 */       printHeader(pw3, featureVectorLength);
/*  90:103 */       for (int i = 4800; i < 9120; i++)
/*  91:    */       {
/*  92:104 */         if (i < 10) {
/*  93:104 */           s = "00000";
/*  94:105 */         } else if (i < 100) {
/*  95:105 */           s = "0000";
/*  96:106 */         } else if (i < 1000) {
/*  97:106 */           s = "000";
/*  98:    */         } else {
/*  99:107 */           s = "00";
/* 100:    */         }
/* 101:109 */         Image img = Load.invoke(path + s + i + ".bmp");
/* 102:    */         
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:116 */         Image img2 = GrayQFZLocalRanges.invoke(img, 40, 40);
/* 109:117 */         double[] feature = Histogram.invoke(img2, Boolean.valueOf(true));
/* 110:119 */         for (int j = 0; j < 30; j++) {
/* 111:120 */           pw3.print(feature[j] + ",");
/* 112:    */         }
/* 113:122 */         pw3.println((i - 4800) / 180);
/* 114:    */         
/* 115:124 */         System.err.println(i + " test_hor");
/* 116:    */       }
/* 117:126 */       pw3.close();
/* 118:    */     }
/* 119:    */     catch (Exception e)
/* 120:    */     {
/* 121:129 */       e.printStackTrace();
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   private static void printHeader(PrintWriter pw, int flength)
/* 126:    */   {
/* 127:134 */     pw.println("@RELATION deneme");
/* 128:135 */     for (int i = 1; i <= flength; i++) {
/* 129:136 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 130:    */     }
/* 131:137 */     pw.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23}");
/* 132:138 */     pw.println("@DATA");
/* 133:    */   }
/* 134:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.Test12
 * JD-Core Version:    0.7.0.1
 */