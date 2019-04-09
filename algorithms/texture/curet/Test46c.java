/*   1:    */ package vpt.algorithms.texture.curet;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileWriter;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ import java.util.Arrays;
/*   9:    */ import vpt.Image;
/*  10:    */ import vpt.algorithms.conversion.RGB2Gray;
/*  11:    */ import vpt.algorithms.io.Load;
/*  12:    */ import vpt.algorithms.texture.CircularGranulometry;
/*  13:    */ 
/*  14:    */ public class Test46c
/*  15:    */ {
/*  16:    */   public static void main(String[] args)
/*  17:    */   {
/*  18: 33 */     String path = "/media/data/arge/veri_yedegi/texture_databases/Curet8/sample";
/*  19: 34 */     int featureVectorLength = 24;
/*  20:    */     try
/*  21:    */     {
/*  22: 37 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("train.arff")));
/*  23: 38 */       printHeader(pw1, featureVectorLength);
/*  24:    */       
/*  25: 40 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("test.arff")));
/*  26: 41 */       printHeader(pw2, featureVectorLength);
/*  27: 44 */       for (int i = 1; i <= 61; i++)
/*  28:    */       {
/*  29: 45 */         String sampleNo = "";
/*  30: 46 */         if (i < 10) {
/*  31: 46 */           sampleNo = "0" + i;
/*  32:    */         } else {
/*  33: 47 */           sampleNo = i;
/*  34:    */         }
/*  35: 49 */         File dir = new File(path + sampleNo);
/*  36: 50 */         String[] filenames = dir.list();
/*  37: 51 */         Arrays.sort(filenames);
/*  38: 53 */         for (int j = 0; j < 92; j += 2)
/*  39:    */         {
/*  40: 54 */           Image img = Load.invoke(path + sampleNo + "/" + filenames[j]);
/*  41:    */           
/*  42: 56 */           img = RGB2Gray.invoke(img);
/*  43:    */           
/*  44:    */ 
/*  45: 59 */           double[] feature = CircularGranulometry.invoke(img, Integer.valueOf(12), Integer.valueOf(1), Integer.valueOf(0));
/*  46: 61 */           for (int k = 0; k < feature.length; k++) {
/*  47: 62 */             pw1.print(feature[k] + ",");
/*  48:    */           }
/*  49: 64 */           pw1.println(i - 1);
/*  50:    */           
/*  51: 66 */           System.err.println("Sample : " + i + ", image : " + (j + 1));
/*  52:    */         }
/*  53: 69 */         for (int j = 1; j < 92; j += 2)
/*  54:    */         {
/*  55: 70 */           Image img = Load.invoke(path + sampleNo + "/" + filenames[j]);
/*  56:    */           
/*  57: 72 */           img = RGB2Gray.invoke(img);
/*  58:    */           
/*  59:    */ 
/*  60: 75 */           double[] feature = CircularGranulometry.invoke(img, Integer.valueOf(12), Integer.valueOf(1), Integer.valueOf(0));
/*  61: 77 */           for (int k = 0; k < feature.length; k++) {
/*  62: 78 */             pw2.print(feature[k] + ",");
/*  63:    */           }
/*  64: 80 */           pw2.println(i - 1);
/*  65:    */           
/*  66: 82 */           System.err.println("Sample : " + i + ", image : " + (j + 1));
/*  67:    */         }
/*  68:    */       }
/*  69: 85 */       pw1.close();
/*  70: 86 */       pw2.close();
/*  71:    */     }
/*  72:    */     catch (Exception e)
/*  73:    */     {
/*  74: 88 */       e.printStackTrace();
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   private static void printHeader(PrintWriter pw, int flength)
/*  79:    */   {
/*  80: 93 */     pw.println("@RELATION deneme");
/*  81: 95 */     for (int i = 1; i <= flength; i++) {
/*  82: 96 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/*  83:    */     }
/*  84: 98 */     pw.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60}");
/*  85:    */     
/*  86:    */ 
/*  87:    */ 
/*  88:102 */     pw.println("@DATA");
/*  89:    */   }
/*  90:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.Test46c
 * JD-Core Version:    0.7.0.1
 */