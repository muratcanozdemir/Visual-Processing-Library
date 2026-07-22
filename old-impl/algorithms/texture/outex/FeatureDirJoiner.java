/*   1:    */ package vpt.algorithms.texture.outex;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileReader;
/*   7:    */ import java.io.FileWriter;
/*   8:    */ import java.io.PrintWriter;
/*   9:    */ 
/*  10:    */ public class FeatureDirJoiner
/*  11:    */ {
/*  12:    */   public static void main(String[] args)
/*  13:    */   {
/*  14:    */     try
/*  15:    */     {
/*  16: 20 */       String dir1 = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/outex10/test176";
/*  17: 21 */       String dir2 = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/outex10/test179";
/*  18:    */       
/*  19: 23 */       String outputDir = "test180";
/*  20: 24 */       String output = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/outex10/" + outputDir;
/*  21:    */       
/*  22:    */ 
/*  23: 27 */       new File(output).mkdir();
/*  24:    */       
/*  25: 29 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output + "/train.arff")));
/*  26:    */       
/*  27: 31 */       BufferedReader br1 = new BufferedReader(new FileReader(dir1 + "/train.arff"));
/*  28: 32 */       BufferedReader br2 = new BufferedReader(new FileReader(dir2 + "/train.arff"));
/*  29:    */       
/*  30:    */ 
/*  31: 35 */       int dim1 = 0;
/*  32:    */       
/*  33: 37 */       String dimDescription = null;
/*  34:    */       
/*  35: 39 */       String s = null;
/*  36: 41 */       while ((s = br1.readLine()) != null)
/*  37:    */       {
/*  38: 42 */         dim1++;
/*  39: 43 */         if (s.startsWith("@ATTRIBUTE o "))
/*  40:    */         {
/*  41: 44 */           dimDescription = s;
/*  42: 45 */           break;
/*  43:    */         }
/*  44:    */       }
/*  45: 49 */       dim1 -= 2;
/*  46:    */       
/*  47:    */ 
/*  48: 52 */       int dim2 = 0;
/*  49: 54 */       while ((s = br2.readLine()) != null)
/*  50:    */       {
/*  51: 55 */         dim2++;
/*  52: 56 */         if (s.startsWith("@ATTRIBUTE o ")) {
/*  53:    */           break;
/*  54:    */         }
/*  55:    */       }
/*  56: 60 */       dim2 -= 2;
/*  57:    */       
/*  58: 62 */       int dimensions = dim1 + dim2;
/*  59:    */       
/*  60: 64 */       printHeader(pw, dimensions, dimDescription);
/*  61:    */       
/*  62:    */ 
/*  63: 67 */       String s1 = null;
/*  64: 68 */       String s2 = null;
/*  65:    */       
/*  66:    */ 
/*  67: 71 */       br1.readLine();
/*  68: 72 */       br2.readLine();
/*  69: 74 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/*  70:    */       {
/*  71: 76 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/*  72: 77 */         pw.println(s1 + ',' + s2);
/*  73:    */       }
/*  74: 79 */       pw.close();
/*  75: 80 */       br1.close();
/*  76: 81 */       br2.close();
/*  77:    */       
/*  78:    */ 
/*  79:    */ 
/*  80: 85 */       pw = new PrintWriter(new BufferedWriter(new FileWriter(output + "/test.arff")));
/*  81:    */       
/*  82: 87 */       printHeader(pw, dimensions, dimDescription);
/*  83:    */       
/*  84: 89 */       br1 = new BufferedReader(new FileReader(dir1 + "/test.arff"));
/*  85: 90 */       br2 = new BufferedReader(new FileReader(dir2 + "/test.arff"));
/*  86:    */       
/*  87:    */ 
/*  88: 93 */       s1 = null;
/*  89: 94 */       s2 = null;
/*  90: 97 */       while ((s = br1.readLine()) != null) {
/*  91: 98 */         if (s.startsWith("@DATA")) {
/*  92:    */           break;
/*  93:    */         }
/*  94:    */       }
/*  95:102 */       while ((s = br2.readLine()) != null) {
/*  96:103 */         if (s.startsWith("@DATA")) {
/*  97:    */           break;
/*  98:    */         }
/*  99:    */       }
/* 100:107 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 101:    */       {
/* 102:109 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/* 103:    */         
/* 104:111 */         pw.println(s1 + ',' + s2);
/* 105:    */       }
/* 106:113 */       pw.close();
/* 107:114 */       br1.close();
/* 108:115 */       br2.close();
/* 109:    */     }
/* 110:    */     catch (Exception e)
/* 111:    */     {
/* 112:117 */       e.printStackTrace();
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   private static void printHeader(PrintWriter pw, int dimensions, String description)
/* 117:    */   {
/* 118:122 */     pw.println("@RELATION deneme");
/* 119:124 */     for (int i = 1; i <= dimensions; i++) {
/* 120:125 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 121:    */     }
/* 122:127 */     pw.println(description);
/* 123:    */     
/* 124:129 */     pw.println("@DATA");
/* 125:    */   }
/* 126:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.FeatureDirJoiner
 * JD-Core Version:    0.7.0.1
 */