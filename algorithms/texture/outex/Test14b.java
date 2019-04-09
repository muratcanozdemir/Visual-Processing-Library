/*   1:    */ package vpt.algorithms.texture.outex;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.conversion.RGB2Gray;
/*   9:    */ import vpt.algorithms.io.Load;
/*  10:    */ import vpt.experimental.texture.rotInv.RotationInvariantPoints2;
/*  11:    */ 
/*  12:    */ public class Test14b
/*  13:    */ {
/*  14:    */   public static void main(String[] args)
/*  15:    */   {
/*  16: 26 */     String path = "/home/yoktish/workspace/Outex_TC_00014/images/";
/*  17:    */     try
/*  18:    */     {
/*  19: 29 */       String s = null;
/*  20: 30 */       int featureLength = 24;
/*  21:    */       
/*  22: 32 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("train-b.arff")));
/*  23:    */       
/*  24: 34 */       pw.println("@RELATION deneme");
/*  25: 35 */       for (int i = 1; i <= featureLength; i++) {
/*  26: 36 */         pw.println("@ATTRIBUTE o" + i + "\tREAL");
/*  27:    */       }
/*  28: 37 */       pw.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67}");
/*  29: 38 */       pw.println("@DATA");
/*  30: 40 */       for (int i = 0; i < 1360; i++)
/*  31:    */       {
/*  32: 41 */         if (i < 10) {
/*  33: 41 */           s = "00000";
/*  34: 42 */         } else if (i < 100) {
/*  35: 42 */           s = "0000";
/*  36: 43 */         } else if (i < 1000) {
/*  37: 43 */           s = "000";
/*  38:    */         } else {
/*  39: 44 */           s = "00";
/*  40:    */         }
/*  41: 46 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  42:    */         
/*  43: 48 */         img = RGB2Gray.invoke(img);
/*  44:    */         
/*  45: 50 */         double[] feature = RotationInvariantPoints2.invoke(img, Integer.valueOf(12));
/*  46: 52 */         for (int j = 0; j < feature.length; j++) {
/*  47: 53 */           pw.print(feature[j] + ",");
/*  48:    */         }
/*  49: 55 */         pw.println(i / 20);
/*  50:    */         
/*  51: 57 */         System.err.println(i + " train");
/*  52:    */       }
/*  53: 59 */       pw.close();
/*  54:    */       
/*  55: 61 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("tl84-test-b.arff")));
/*  56:    */       
/*  57: 63 */       pw2.println("@RELATION deneme");
/*  58: 64 */       for (int i = 1; i <= featureLength; i++) {
/*  59: 65 */         pw2.println("@ATTRIBUTE o" + i + "\tREAL");
/*  60:    */       }
/*  61: 66 */       pw2.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67}");
/*  62: 67 */       pw2.println("@DATA");
/*  63: 69 */       for (int i = 1360; i < 2720; i++)
/*  64:    */       {
/*  65: 70 */         if (i < 10) {
/*  66: 70 */           s = "00000";
/*  67: 71 */         } else if (i < 100) {
/*  68: 71 */           s = "0000";
/*  69: 72 */         } else if (i < 1000) {
/*  70: 72 */           s = "000";
/*  71:    */         } else {
/*  72: 73 */           s = "00";
/*  73:    */         }
/*  74: 75 */         Image img = Load.invoke(path + s + i + ".bmp");
/*  75:    */         
/*  76: 77 */         img = RGB2Gray.invoke(img);
/*  77:    */         
/*  78: 79 */         double[] feature = RotationInvariantPoints2.invoke(img, Integer.valueOf(12));
/*  79: 81 */         for (int j = 0; j < feature.length; j++) {
/*  80: 82 */           pw2.print(feature[j] + ",");
/*  81:    */         }
/*  82: 84 */         pw2.println((i - 1360) / 20);
/*  83:    */         
/*  84: 86 */         System.err.println(i + " test");
/*  85:    */       }
/*  86: 88 */       pw2.close();
/*  87:    */       
/*  88: 90 */       PrintWriter pw3 = new PrintWriter(new BufferedWriter(new FileWriter("horizon-test-b.arff")));
/*  89:    */       
/*  90: 92 */       pw3.println("@RELATION deneme");
/*  91: 93 */       for (int i = 1; i <= featureLength; i++) {
/*  92: 94 */         pw3.println("@ATTRIBUTE o" + i + "\tREAL");
/*  93:    */       }
/*  94: 95 */       pw3.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67}");
/*  95: 96 */       pw3.println("@DATA");
/*  96: 98 */       for (int i = 2720; i < 4080; i++)
/*  97:    */       {
/*  98: 99 */         if (i < 10) {
/*  99: 99 */           s = "00000";
/* 100:100 */         } else if (i < 100) {
/* 101:100 */           s = "0000";
/* 102:101 */         } else if (i < 1000) {
/* 103:101 */           s = "000";
/* 104:    */         } else {
/* 105:102 */           s = "00";
/* 106:    */         }
/* 107:104 */         Image img = Load.invoke(path + s + i + ".bmp");
/* 108:    */         
/* 109:106 */         img = RGB2Gray.invoke(img);
/* 110:    */         
/* 111:108 */         double[] feature = RotationInvariantPoints2.invoke(img, Integer.valueOf(12));
/* 112:110 */         for (int j = 0; j < feature.length; j++) {
/* 113:111 */           pw3.print(feature[j] + ",");
/* 114:    */         }
/* 115:113 */         pw3.println((i - 2720) / 20);
/* 116:    */         
/* 117:115 */         System.err.println(i + " test");
/* 118:    */       }
/* 119:117 */       pw3.close();
/* 120:    */     }
/* 121:    */     catch (Exception e)
/* 122:    */     {
/* 123:120 */       e.printStackTrace();
/* 124:    */     }
/* 125:    */   }
/* 126:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.Test14b
 * JD-Core Version:    0.7.0.1
 */