/*   1:    */ package vpt.algorithms.face;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.BufferedReader;
/*   5:    */ import java.io.BufferedWriter;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.FileReader;
/*   8:    */ import java.io.FileWriter;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.io.PrintWriter;
/*  11:    */ import java.util.Arrays;
/*  12:    */ import vpt.BooleanImage;
/*  13:    */ import vpt.Image;
/*  14:    */ import vpt.algorithms.conversion.RGB2Gray;
/*  15:    */ import vpt.algorithms.geometric.Crop;
/*  16:    */ import vpt.algorithms.io.Load;
/*  17:    */ import vpt.algorithms.texture.ExtendedMorphologicalCovariance;
/*  18:    */ 
/*  19:    */ public class TestFRGC
/*  20:    */ {
/*  21:    */   public static void main(String[] args)
/*  22:    */   {
/*  23: 31 */     BooleanImage mask = new BooleanImage(50, 50, 1);
/*  24:    */     try
/*  25:    */     {
/*  26: 35 */       BufferedReader br = new BufferedReader(new FileReader("/media/data/arge/veri_yedegi/face databases/mask_headcut.txt"));
/*  27:    */       
/*  28: 37 */       String s = null;
/*  29: 38 */       int line = 0;
/*  30: 40 */       while ((s = br.readLine()) != null)
/*  31:    */       {
/*  32: 41 */         String[] tokens = s.split(" ");
/*  33: 43 */         for (int i = 0; i < tokens.length; i++) {
/*  34: 44 */           if (tokens[i].equals("1")) {
/*  35: 45 */             mask.setXYBoolean(i, line, false);
/*  36:    */           } else {
/*  37: 47 */             mask.setXYBoolean(i, line, true);
/*  38:    */           }
/*  39:    */         }
/*  40: 50 */         line++;
/*  41:    */       }
/*  42: 53 */       br.close();
/*  43:    */     }
/*  44:    */     catch (Exception e)
/*  45:    */     {
/*  46: 55 */       e.printStackTrace();
/*  47:    */     }
/*  48: 59 */     String path = "/media/data/arge/veri_yedegi/face databases/frgc_subset/";
/*  49: 60 */     int lineNo = 5;
/*  50: 61 */     int colNo = 5;
/*  51: 62 */     int featureVectorLength = 128 * lineNo * colNo;
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58: 69 */     File dirs = new File(path + "gallery");
/*  59: 70 */     String[] filenames = dirs.list();
/*  60: 71 */     Arrays.sort(filenames);
/*  61:    */     try
/*  62:    */     {
/*  63: 74 */       PrintWriter pwTr = new PrintWriter(new BufferedWriter(new FileWriter("train.arff")));
/*  64:    */       
/*  65: 76 */       printHeader(pwTr, featureVectorLength);
/*  66: 79 */       for (int i = 0; i < filenames.length; i++)
/*  67:    */       {
/*  68: 82 */         File dir = new File(path + "gallery/" + filenames[i]);
/*  69: 83 */         String[] filenames2 = dir.list();
/*  70: 85 */         if (filenames2.length > 1) {
/*  71: 85 */           System.err.println("More than one file???");
/*  72:    */         }
/*  73: 87 */         System.err.println("Processing train image " + path + "gallery/" + filenames[i] + "/" + filenames2[0]);
/*  74:    */         
/*  75: 89 */         Image img = Load.invoke(path + "gallery/" + filenames[i] + "/" + filenames2[0]);
/*  76: 90 */         img = RGB2Gray.invoke(img);
/*  77: 93 */         for (int l = 0; l < mask.size(); l++)
/*  78:    */         {
/*  79: 94 */           int p = mask.getByte(l);
/*  80: 96 */           if (p == 0) {
/*  81: 96 */             img.setByte(l, 255);
/*  82:    */           }
/*  83:    */         }
/*  84:101 */         for (int line = 0; line < lineNo; line++) {
/*  85:102 */           for (int col = 0; col < colNo; col++)
/*  86:    */           {
/*  87:104 */             Image subImg = Crop.invoke(img, new Point(line * 10, col * 10), new Point((line + 1) * 10 - 1, (col + 1) * 10 - 1));
/*  88:    */             
/*  89:106 */             double[] feature = ExtendedMorphologicalCovariance.invoke(subImg, Integer.valueOf(7));
/*  90:109 */             for (int j = 0; j < feature.length; j++) {
/*  91:110 */               pwTr.print(feature[j] + ",");
/*  92:    */             }
/*  93:    */           }
/*  94:    */         }
/*  95:114 */         pwTr.println(filenames[i]);
/*  96:    */       }
/*  97:117 */       pwTr.close();
/*  98:    */       
/*  99:119 */       PrintWriter pwTs = new PrintWriter(new BufferedWriter(new FileWriter("test.arff")));
/* 100:    */       
/* 101:121 */       printHeader(pwTs, featureVectorLength);
/* 102:    */       
/* 103:123 */       dirs = new File(path + "probe");
/* 104:124 */       filenames = dirs.list();
/* 105:125 */       Arrays.sort(filenames);
/* 106:128 */       for (int i = 0; i < filenames.length; i++)
/* 107:    */       {
/* 108:131 */         File dir = new File(path + "probe/" + filenames[i]);
/* 109:132 */         String[] filenames2 = dir.list();
/* 110:134 */         for (int j = 0; j < filenames2.length; j++)
/* 111:    */         {
/* 112:136 */           System.err.println("Processing test image " + path + "probe/" + filenames[i] + "/" + filenames2[j]);
/* 113:137 */           Image img = Load.invoke(path + "probe/" + filenames[i] + "/" + filenames2[j]);
/* 114:138 */           img = RGB2Gray.invoke(img);
/* 115:141 */           for (int l = 0; l < mask.size(); l++)
/* 116:    */           {
/* 117:142 */             int p = mask.getByte(l);
/* 118:144 */             if (p == 0) {
/* 119:144 */               img.setByte(l, 255);
/* 120:    */             }
/* 121:    */           }
/* 122:149 */           for (int line = 0; line < lineNo; line++) {
/* 123:150 */             for (int col = 0; col < colNo; col++)
/* 124:    */             {
/* 125:152 */               Image subImg = Crop.invoke(img, new Point(line * 10, col * 10), new Point((line + 1) * 10 - 1, (col + 1) * 10 - 1));
/* 126:    */               
/* 127:154 */               double[] feature = ExtendedMorphologicalCovariance.invoke(subImg, Integer.valueOf(7));
/* 128:157 */               for (int k = 0; k < feature.length; k++) {
/* 129:158 */                 pwTs.print(feature[k] + ",");
/* 130:    */               }
/* 131:    */             }
/* 132:    */           }
/* 133:162 */           pwTs.println(filenames[i]);
/* 134:    */         }
/* 135:    */       }
/* 136:165 */       pwTs.close();
/* 137:    */     }
/* 138:    */     catch (Exception e)
/* 139:    */     {
/* 140:167 */       e.printStackTrace();
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   private static void printHeader(PrintWriter pw, int flength)
/* 145:    */   {
/* 146:172 */     pw.println("@RELATION deneme");
/* 147:174 */     for (int i = 1; i <= flength; i++) {
/* 148:175 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 149:    */     }
/* 150:177 */     pw.println("@ATTRIBUTE o \t{02463,04200,04201,04202,04203,04204,04205,04206,04207,04208,04209,04210,04211,04212,04213,04214,04215,04216,04217,04218,04219,04220,04221,04222,04223,04224,04225,04226,04227,04228,04229,04230,04231,04232,04233,04234,04235,04236,04237,04238,04239,04240,04241,04242,04243,04244,04245,04246,04247,04248,04249,04250,04251,04252,04254,04255,04256,04258,04259,04260}");
/* 151:    */     
/* 152:    */ 
/* 153:    */ 
/* 154:    */ 
/* 155:    */ 
/* 156:    */ 
/* 157:184 */     pw.println("@DATA");
/* 158:    */   }
/* 159:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.face.TestFRGC
 * JD-Core Version:    0.7.0.1
 */