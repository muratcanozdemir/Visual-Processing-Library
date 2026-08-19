/*   1:    */ package vpt.algorithms.texture.outex;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileReader;
/*   7:    */ import java.io.FileWriter;
/*   8:    */ import java.io.PrintWriter;
/*   9:    */ 
/*  10:    */ public class FeatureDirJoiner12
/*  11:    */ {
/*  12:    */   public static void main(String[] args)
/*  13:    */   {
/*  14:    */     try
/*  15:    */     {
/*  16: 20 */       String dir1 = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/outex14/test117";
/*  17: 21 */       String dir2 = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/outex14/test119";
/*  18:    */       
/*  19: 23 */       String outputDir = "test109";
/*  20: 24 */       String output = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/outex14/" + outputDir;
/*  21:    */       
/*  22:    */ 
/*  23: 27 */       new File(output).mkdir();
/*  24:    */       
/*  25: 29 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output + "/train1.arff")));
/*  26:    */       
/*  27: 31 */       BufferedReader br1 = new BufferedReader(new FileReader(dir1 + "/train1.arff"));
/*  28: 32 */       BufferedReader br2 = new BufferedReader(new FileReader(dir2 + "/train1.arff"));
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
/*  79: 84 */       pw = new PrintWriter(new BufferedWriter(new FileWriter(output + "/test1a.arff")));
/*  80:    */       
/*  81: 86 */       printHeader(pw, dimensions, dimDescription);
/*  82:    */       
/*  83: 88 */       br1 = new BufferedReader(new FileReader(dir1 + "/test1a.arff"));
/*  84: 89 */       br2 = new BufferedReader(new FileReader(dir2 + "/test1a.arff"));
/*  85:    */       
/*  86:    */ 
/*  87: 92 */       s1 = null;
/*  88: 93 */       s2 = null;
/*  89: 96 */       while ((s = br1.readLine()) != null) {
/*  90: 97 */         if (s.startsWith("@DATA")) {
/*  91:    */           break;
/*  92:    */         }
/*  93:    */       }
/*  94:101 */       while ((s = br2.readLine()) != null) {
/*  95:102 */         if (s.startsWith("@DATA")) {
/*  96:    */           break;
/*  97:    */         }
/*  98:    */       }
/*  99:106 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 100:    */       {
/* 101:108 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/* 102:109 */         pw.println(s1 + ',' + s2);
/* 103:    */       }
/* 104:111 */       pw.close();
/* 105:112 */       br1.close();
/* 106:113 */       br2.close();
/* 107:    */       
/* 108:    */ 
/* 109:116 */       pw = new PrintWriter(new BufferedWriter(new FileWriter(output + "/test1b.arff")));
/* 110:    */       
/* 111:118 */       printHeader(pw, dimensions, dimDescription);
/* 112:    */       
/* 113:120 */       br1 = new BufferedReader(new FileReader(dir1 + "/test1b.arff"));
/* 114:121 */       br2 = new BufferedReader(new FileReader(dir2 + "/test1b.arff"));
/* 115:    */       
/* 116:    */ 
/* 117:124 */       s1 = null;
/* 118:125 */       s2 = null;
/* 119:128 */       while ((s = br1.readLine()) != null) {
/* 120:129 */         if (s.startsWith("@DATA")) {
/* 121:    */           break;
/* 122:    */         }
/* 123:    */       }
/* 124:133 */       while ((s = br2.readLine()) != null) {
/* 125:134 */         if (s.startsWith("@DATA")) {
/* 126:    */           break;
/* 127:    */         }
/* 128:    */       }
/* 129:138 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 130:    */       {
/* 131:140 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/* 132:141 */         pw.println(s1 + ',' + s2);
/* 133:    */       }
/* 134:143 */       pw.close();
/* 135:144 */       br1.close();
/* 136:145 */       br2.close();
/* 137:    */     }
/* 138:    */     catch (Exception e)
/* 139:    */     {
/* 140:147 */       e.printStackTrace();
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   private static void printHeader(PrintWriter pw, int dimensions, String description)
/* 145:    */   {
/* 146:152 */     pw.println("@RELATION deneme");
/* 147:154 */     for (int i = 1; i <= dimensions; i++) {
/* 148:155 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 149:    */     }
/* 150:157 */     pw.println(description);
/* 151:    */     
/* 152:159 */     pw.println("@DATA");
/* 153:    */   }
/* 154:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.FeatureDirJoiner12
 * JD-Core Version:    0.7.0.1
 */