/*   1:    */ package vpt.algorithms.texture.outex;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.conversion.RGB2Gray;
/*   9:    */ import vpt.algorithms.io.Load;
/*  10:    */ import vpt.algorithms.texture.MorphologicalCovariance;
/*  11:    */ 
/*  12:    */ public class Test14
/*  13:    */ {
/*  14:    */   public static void main(String[] args)
/*  15:    */   {
/*  16: 28 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Outex_TC_00014/images/";
/*  17:    */     try
/*  18:    */     {
/*  19: 31 */       String s = null;
/*  20: 32 */       int featureVectorLength = 48;
/*  21:    */       
/*  22: 34 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("train1.arff")));
/*  23:    */       
/*  24: 36 */       printHeader(pw, featureVectorLength);
/*  25: 38 */       for (int i = 0; i < 1360; i++)
/*  26:    */       {
/*  27: 39 */         if (i < 10) {
/*  28: 39 */           s = "00000";
/*  29: 40 */         } else if (i < 100) {
/*  30: 40 */           s = "0000";
/*  31: 41 */         } else if (i < 1000) {
/*  32: 41 */           s = "000";
/*  33:    */         } else {
/*  34: 42 */           s = "00";
/*  35:    */         }
/*  36: 44 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  37:    */         
/*  38: 46 */         img = RGB2Gray.invoke(img);
/*  39:    */         
/*  40:    */ 
/*  41: 49 */         double[] feature = MorphologicalCovariance.invoke(img, Integer.valueOf(12));
/*  42: 51 */         for (int j = 0; j < feature.length; j++) {
/*  43: 52 */           pw.print(feature[j] + ",");
/*  44:    */         }
/*  45: 54 */         pw.println(i / 20);
/*  46:    */         
/*  47: 56 */         System.err.println(i + " train");
/*  48:    */       }
/*  49: 58 */       pw.close();
/*  50:    */       
/*  51: 60 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("test1a.arff")));
/*  52:    */       
/*  53: 62 */       printHeader(pw2, featureVectorLength);
/*  54: 64 */       for (int i = 1360; i < 2720; i++)
/*  55:    */       {
/*  56: 65 */         if (i < 10) {
/*  57: 65 */           s = "00000";
/*  58: 66 */         } else if (i < 100) {
/*  59: 66 */           s = "0000";
/*  60: 67 */         } else if (i < 1000) {
/*  61: 67 */           s = "000";
/*  62:    */         } else {
/*  63: 68 */           s = "00";
/*  64:    */         }
/*  65: 70 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  66:    */         
/*  67: 72 */         img = RGB2Gray.invoke(img);
/*  68:    */         
/*  69:    */ 
/*  70: 75 */         double[] feature = MorphologicalCovariance.invoke(img, Integer.valueOf(12));
/*  71: 77 */         for (int j = 0; j < feature.length; j++) {
/*  72: 78 */           pw2.print(feature[j] + ",");
/*  73:    */         }
/*  74: 80 */         pw2.println((i - 1360) / 20);
/*  75:    */         
/*  76: 82 */         System.err.println(i + " test");
/*  77:    */       }
/*  78: 84 */       pw2.close();
/*  79:    */       
/*  80: 86 */       PrintWriter pw3 = new PrintWriter(new BufferedWriter(new FileWriter("test1b.arff")));
/*  81:    */       
/*  82: 88 */       printHeader(pw3, featureVectorLength);
/*  83: 90 */       for (int i = 2720; i < 4080; i++)
/*  84:    */       {
/*  85: 91 */         if (i < 10) {
/*  86: 91 */           s = "00000";
/*  87: 92 */         } else if (i < 100) {
/*  88: 92 */           s = "0000";
/*  89: 93 */         } else if (i < 1000) {
/*  90: 93 */           s = "000";
/*  91:    */         } else {
/*  92: 94 */           s = "00";
/*  93:    */         }
/*  94: 96 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  95:    */         
/*  96: 98 */         img = RGB2Gray.invoke(img);
/*  97:    */         
/*  98:    */ 
/*  99:101 */         double[] feature = MorphologicalCovariance.invoke(img, Integer.valueOf(12));
/* 100:103 */         for (int j = 0; j < feature.length; j++) {
/* 101:104 */           pw3.print(feature[j] + ",");
/* 102:    */         }
/* 103:106 */         pw3.println((i - 2720) / 20);
/* 104:    */         
/* 105:108 */         System.err.println(i + " test");
/* 106:    */       }
/* 107:110 */       pw3.close();
/* 108:    */     }
/* 109:    */     catch (Exception e)
/* 110:    */     {
/* 111:113 */       e.printStackTrace();
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   private static void printHeader(PrintWriter pw, int flength)
/* 116:    */   {
/* 117:118 */     pw.println("@RELATION deneme");
/* 118:119 */     for (int i = 1; i <= flength; i++) {
/* 119:120 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 120:    */     }
/* 121:121 */     pw.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67}");
/* 122:122 */     pw.println("@DATA");
/* 123:    */   }
/* 124:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.Test14
 * JD-Core Version:    0.7.0.1
 */