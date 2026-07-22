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
/*  12:    */ import vpt.algorithms.statistical.Normalize;
/*  13:    */ import vpt.algorithms.texture.MorphologicalCovariance;
/*  14:    */ 
/*  15:    */ public class Test46d
/*  16:    */ {
/*  17:    */   public static void main(String[] args)
/*  18:    */   {
/*  19: 30 */     String path = "/home/yoktish/workspace/Curet/sample";
/*  20: 31 */     int featureVectorLength = 24;
/*  21:    */     try
/*  22:    */     {
/*  23: 34 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("train.arff")));
/*  24:    */       
/*  25: 36 */       pw1.println("@RELATION deneme");
/*  26: 37 */       for (int i = 1; i <= featureVectorLength; i++) {
/*  27: 38 */         pw1.println("@ATTRIBUTE o" + i + "\tREAL");
/*  28:    */       }
/*  29: 40 */       pw1.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60}");
/*  30:    */       
/*  31:    */ 
/*  32: 43 */       pw1.println("@DATA");
/*  33:    */       
/*  34: 45 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("test.arff")));
/*  35:    */       
/*  36: 47 */       pw2.println("@RELATION deneme");
/*  37: 48 */       for (int i = 1; i <= featureVectorLength; i++) {
/*  38: 49 */         pw2.println("@ATTRIBUTE o" + i + "\tREAL");
/*  39:    */       }
/*  40: 51 */       pw2.println("@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60}");
/*  41:    */       
/*  42:    */ 
/*  43: 54 */       pw2.println("@DATA");
/*  44: 56 */       for (int i = 1; i <= 61; i++)
/*  45:    */       {
/*  46: 57 */         String sampleNo = "";
/*  47: 58 */         if (i < 10) {
/*  48: 58 */           sampleNo = "0" + i;
/*  49:    */         } else {
/*  50: 59 */           sampleNo = i;
/*  51:    */         }
/*  52: 61 */         File dir = new File(path + sampleNo);
/*  53: 62 */         String[] filenames = dir.list();
/*  54: 63 */         Arrays.sort(filenames);
/*  55: 65 */         for (int j = 0; j < 92; j += 2)
/*  56:    */         {
/*  57: 66 */           Image img = Load.invoke(path + sampleNo + "/" + filenames[j]);
/*  58:    */           
/*  59: 68 */           img = RGB2Gray.invoke(img);
/*  60:    */           
/*  61: 70 */           img = Normalize.invoke(img, Double.valueOf(0.0D), Double.valueOf(1.0D));
/*  62:    */           
/*  63: 72 */           double[] feature = MorphologicalCovariance.invoke(img, Integer.valueOf(12));
/*  64: 74 */           for (int k = 0; k < feature.length; k++) {
/*  65: 75 */             pw1.print(feature[k] + ",");
/*  66:    */           }
/*  67: 77 */           pw1.println(i - 1);
/*  68:    */           
/*  69: 79 */           System.err.println("Sample : " + i + ", image : " + (j + 1));
/*  70:    */         }
/*  71: 82 */         for (int j = 1; j < 92; j += 2)
/*  72:    */         {
/*  73: 83 */           Image img = Load.invoke(path + sampleNo + "/" + filenames[j]);
/*  74:    */           
/*  75: 85 */           img = RGB2Gray.invoke(img);
/*  76:    */           
/*  77: 87 */           img = Normalize.invoke(img, Double.valueOf(0.0D), Double.valueOf(1.0D));
/*  78:    */           
/*  79: 89 */           double[] feature = MorphologicalCovariance.invoke(img, Integer.valueOf(12));
/*  80: 91 */           for (int k = 0; k < feature.length; k++) {
/*  81: 92 */             pw2.print(feature[k] + ",");
/*  82:    */           }
/*  83: 94 */           pw2.println(i - 1);
/*  84:    */           
/*  85: 96 */           System.err.println("Sample : " + i + ", image : " + (j + 1));
/*  86:    */         }
/*  87:    */       }
/*  88: 99 */       pw1.close();
/*  89:100 */       pw2.close();
/*  90:    */     }
/*  91:    */     catch (Exception e)
/*  92:    */     {
/*  93:102 */       e.printStackTrace();
/*  94:    */     }
/*  95:    */   }
/*  96:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.Test46d
 * JD-Core Version:    0.7.0.1
 */