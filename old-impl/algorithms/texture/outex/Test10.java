/*   1:    */ package vpt.algorithms.texture.outex;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.FileWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.io.Load;
/*   9:    */ import vpt.experimental.texture.MetaCCH;
/*  10:    */ 
/*  11:    */ public class Test10
/*  12:    */ {
/*  13:    */   public static void main(String[] args)
/*  14:    */   {
/*  15: 36 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Outex_TC_00010/images/";
/*  16: 37 */     int featureVectorLength = 96;
/*  17:    */     try
/*  18:    */     {
/*  19: 40 */       String s = null;
/*  20:    */       
/*  21: 42 */       PrintWriter pwTr = new PrintWriter(new BufferedWriter(new FileWriter("train2.arff")));
/*  22:    */       
/*  23: 44 */       printHeader(pwTr, featureVectorLength);
/*  24: 46 */       for (int t = 0; t < 24; t++) {
/*  25: 47 */         for (int i = 0; i < 20; i++)
/*  26:    */         {
/*  27: 48 */           int index = t * 180 + i;
/*  28: 49 */           if (index < 10) {
/*  29: 49 */             s = "00000";
/*  30: 50 */           } else if (index < 100) {
/*  31: 50 */             s = "0000";
/*  32: 51 */           } else if (index < 1000) {
/*  33: 51 */             s = "000";
/*  34:    */           } else {
/*  35: 52 */             s = "00";
/*  36:    */           }
/*  37: 54 */           Image img = Load.invoke(path + s + index + ".bmp");
/*  38:    */           
/*  39: 56 */           double[] feature = MetaCCH.invoke(img, Integer.valueOf(12), Integer.valueOf(1));
/*  40: 58 */           for (int j = 0; j < feature.length; j++) {
/*  41: 59 */             pwTr.print(feature[j] + ",");
/*  42:    */           }
/*  43: 61 */           pwTr.println(index / 180);
/*  44:    */           
/*  45: 63 */           System.err.println(index + " train");
/*  46:    */         }
/*  47:    */       }
/*  48: 66 */       pwTr.close();
/*  49:    */       
/*  50: 68 */       PrintWriter pwTs = new PrintWriter(new BufferedWriter(new FileWriter("test2.arff")));
/*  51:    */       
/*  52: 70 */       printHeader(pwTs, featureVectorLength);
/*  53: 72 */       for (int t = 0; t < 24; t++) {
/*  54: 73 */         for (int i = 0; i < 160; i++)
/*  55:    */         {
/*  56: 74 */           int index = 20 + t * 180 + i;
/*  57: 75 */           if (index < 10) {
/*  58: 75 */             s = "00000";
/*  59: 76 */           } else if (index < 100) {
/*  60: 76 */             s = "0000";
/*  61: 77 */           } else if (index < 1000) {
/*  62: 77 */             s = "000";
/*  63:    */           } else {
/*  64: 78 */             s = "00";
/*  65:    */           }
/*  66: 80 */           Image img = Load.invoke(path + s + index + ".bmp");
/*  67:    */           
/*  68: 82 */           double[] feature = MetaCCH.invoke(img, Integer.valueOf(12), Integer.valueOf(1));
/*  69: 84 */           for (int j = 0; j < feature.length; j++) {
/*  70: 85 */             pwTs.print(feature[j] + ",");
/*  71:    */           }
/*  72: 87 */           pwTs.println(index / 180);
/*  73:    */           
/*  74: 89 */           System.err.println(index + " test");
/*  75:    */         }
/*  76:    */       }
/*  77: 92 */       pwTs.close();
/*  78:    */     }
/*  79:    */     catch (Exception e)
/*  80:    */     {
/*  81: 94 */       e.printStackTrace();
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   private static void printHeader(PrintWriter pw, int flength)
/*  86:    */   {
/*  87: 99 */     pw.println("@RELATION deneme");
/*  88:100 */     for (int i = 1; i <= flength; i++) {
/*  89:101 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/*  90:    */     }
/*  91:102 */     pw.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23}");
/*  92:103 */     pw.println("@DATA");
/*  93:    */   }
/*  94:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.Test10
 * JD-Core Version:    0.7.0.1
 */