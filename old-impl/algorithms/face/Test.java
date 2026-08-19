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
/*  16:    */ import vpt.algorithms.histogram.Histogram;
/*  17:    */ import vpt.algorithms.io.Load;
/*  18:    */ 
/*  19:    */ public class Test
/*  20:    */ {
/*  21:    */   public static void main(String[] args)
/*  22:    */   {
/*  23: 32 */     BooleanImage mask = new BooleanImage(50, 50, 1);
/*  24:    */     try
/*  25:    */     {
/*  26: 36 */       BufferedReader br = new BufferedReader(new FileReader("/media/data/arge/veri_yedegi/face databases/mask_headcut.txt"));
/*  27:    */       
/*  28: 38 */       String s = null;
/*  29: 39 */       int line = 0;
/*  30: 41 */       while ((s = br.readLine()) != null)
/*  31:    */       {
/*  32: 42 */         String[] tokens = s.split(" ");
/*  33: 44 */         for (int i = 0; i < tokens.length; i++) {
/*  34: 45 */           if (tokens[i].equals("1")) {
/*  35: 46 */             mask.setXYBoolean(i, line, false);
/*  36:    */           } else {
/*  37: 48 */             mask.setXYBoolean(i, line, true);
/*  38:    */           }
/*  39:    */         }
/*  40: 51 */         line++;
/*  41:    */       }
/*  42: 54 */       br.close();
/*  43:    */     }
/*  44:    */     catch (Exception e)
/*  45:    */     {
/*  46: 56 */       e.printStackTrace();
/*  47:    */     }
/*  48: 60 */     String path = "/media/data/arge/veri_yedegi/face databases/single/";
/*  49: 61 */     int lineNo = 5;
/*  50: 62 */     int colNo = 5;
/*  51: 63 */     int featureVectorLength = 146 * lineNo * colNo;
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58: 70 */     File dirs = new File(path + "gallery");
/*  59: 71 */     String[] filenames = dirs.list();
/*  60: 72 */     Arrays.sort(filenames);
/*  61:    */     try
/*  62:    */     {
/*  63: 75 */       PrintWriter pwTr = new PrintWriter(new BufferedWriter(new FileWriter("train.arff")));
/*  64:    */       
/*  65: 77 */       printHeader(pwTr, featureVectorLength);
/*  66: 80 */       for (int i = 0; i < filenames.length; i++)
/*  67:    */       {
/*  68: 83 */         File dir = new File(path + "gallery/" + filenames[i]);
/*  69: 84 */         String[] filenames2 = dir.list();
/*  70: 86 */         if (filenames2.length > 1) {
/*  71: 86 */           System.err.println("More than one file???");
/*  72:    */         }
/*  73: 88 */         System.err.println("Processing train image " + path + "gallery/" + filenames[i] + "/" + filenames2[0]);
/*  74:    */         
/*  75: 90 */         Image img = Load.invoke(path + "gallery/" + filenames[i] + "/" + filenames2[0]);
/*  76: 91 */         img = RGB2Gray.invoke(img);
/*  77: 94 */         for (int l = 0; l < mask.size(); l++)
/*  78:    */         {
/*  79: 95 */           int p = mask.getByte(l);
/*  80: 97 */           if (p == 0) {
/*  81: 97 */             img.setByte(l, 255);
/*  82:    */           }
/*  83:    */         }
/*  84:102 */         for (int line = 0; line < lineNo; line++) {
/*  85:103 */           for (int col = 0; col < colNo; col++)
/*  86:    */           {
/*  87:105 */             Image subImg = Crop.invoke(img, new Point(line * 10, col * 10), new Point((line + 1) * 10 - 1, (col + 1) * 10 - 1));
/*  88:    */             
/*  89:    */ 
/*  90:    */ 
/*  91:109 */             double[] feature = Histogram.invoke(subImg, Boolean.valueOf(true));
/*  92:111 */             for (int j = 0; j < feature.length; j++) {
/*  93:112 */               pwTr.print(feature[j] + ",");
/*  94:    */             }
/*  95:    */           }
/*  96:    */         }
/*  97:116 */         pwTr.println(filenames[i]);
/*  98:    */       }
/*  99:119 */       pwTr.close();
/* 100:    */       
/* 101:121 */       PrintWriter pwTs = new PrintWriter(new BufferedWriter(new FileWriter("test.arff")));
/* 102:    */       
/* 103:123 */       printHeader(pwTs, featureVectorLength);
/* 104:    */       
/* 105:125 */       dirs = new File(path + "probe");
/* 106:126 */       filenames = dirs.list();
/* 107:127 */       Arrays.sort(filenames);
/* 108:130 */       for (int i = 0; i < filenames.length; i++)
/* 109:    */       {
/* 110:133 */         File dir = new File(path + "probe/" + filenames[i]);
/* 111:134 */         String[] filenames2 = dir.list();
/* 112:136 */         for (int j = 0; j < filenames2.length; j++)
/* 113:    */         {
/* 114:138 */           System.err.println("Processing test image " + path + "probe/" + filenames[i] + "/" + filenames2[j]);
/* 115:139 */           Image img = Load.invoke(path + "probe/" + filenames[i] + "/" + filenames2[j]);
/* 116:140 */           img = RGB2Gray.invoke(img);
/* 117:143 */           for (int l = 0; l < mask.size(); l++)
/* 118:    */           {
/* 119:144 */             int p = mask.getByte(l);
/* 120:146 */             if (p == 0) {
/* 121:146 */               img.setByte(l, 255);
/* 122:    */             }
/* 123:    */           }
/* 124:151 */           for (int line = 0; line < lineNo; line++) {
/* 125:152 */             for (int col = 0; col < colNo; col++)
/* 126:    */             {
/* 127:154 */               Image subImg = Crop.invoke(img, new Point(line * 10, col * 10), new Point((line + 1) * 10 - 1, (col + 1) * 10 - 1));
/* 128:    */               
/* 129:    */ 
/* 130:    */ 
/* 131:158 */               double[] feature = Histogram.invoke(subImg, Boolean.valueOf(true));
/* 132:160 */               for (int k = 0; k < feature.length; k++) {
/* 133:161 */                 pwTs.print(feature[k] + ",");
/* 134:    */               }
/* 135:    */             }
/* 136:    */           }
/* 137:165 */           pwTs.println(filenames[i]);
/* 138:    */         }
/* 139:    */       }
/* 140:168 */       pwTs.close();
/* 141:    */     }
/* 142:    */     catch (Exception e)
/* 143:    */     {
/* 144:170 */       e.printStackTrace();
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   private static void printHeader(PrintWriter pw, int flength)
/* 149:    */   {
/* 150:175 */     pw.println("@RELATION deneme");
/* 151:177 */     for (int i = 1; i <= flength; i++) {
/* 152:178 */       pw.println("@ATTRIBUTE o" + i + "\tREAL");
/* 153:    */     }
/* 154:180 */     pw.println("@ATTRIBUTE o \t{02463,04200,04201,04202,04203,04204,04205,04206,04207,04208,04209,04210,04211,04212,04213,04214,04215,04216,04217,04218,04219,04220,04221,04222,04223,04224,04225,04226,04227,04228,04229,04230,04231,04232,04233,04234,04235,04236,04237,04238,04239,04240,04241,04242,04243,04244,04245,04246,04247,04248,04249,04250,04251,04252,04253,04254,04255,04256,04257,04258,04259,04260}");
/* 155:    */     
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:187 */     pw.println("@DATA");
/* 162:    */   }
/* 163:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.face.Test
 * JD-Core Version:    0.7.0.1
 */