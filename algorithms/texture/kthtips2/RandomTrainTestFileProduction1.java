/*  1:   */ package vpt.algorithms.texture.kthtips2;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ import java.util.Random;
/*  9:   */ 
/* 10:   */ public class RandomTrainTestFileProduction1
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:   */     try
/* 15:   */     {
/* 16:14 */       BufferedReader data = new BufferedReader(new FileReader("/home/yoktish/workspace/My DIP Toolbox/valuation tests/kth-tips2b/test135.dat"));
/* 17:   */       
/* 18:16 */       int howManyTimes = 100;
/* 19:17 */       int classCount = 11;
/* 20:18 */       Random r = new Random();
/* 21:   */       
/* 22:20 */       String header = "@RELATION deneme\n";
/* 23:22 */       for (int i = 1; i <= 192; i++) {
/* 24:23 */         header = header + "@ATTRIBUTE o" + i + "\tREAL" + "\n";
/* 25:   */       }
/* 26:25 */       header = header + "@ATTRIBUTE o \t{0,1,2,3,4,5,6,7,8,9,10}\n@DATA";
/* 27:   */       
/* 28:27 */       String[][][] allSamples = new String[classCount][4][108];
/* 29:30 */       for (int j = 0; j < classCount; j++) {
/* 30:31 */         for (int k = 0; k < 4; k++) {
/* 31:32 */           for (int l = 0; l < 108; l++) {
/* 32:33 */             allSamples[j][k][l] = data.readLine();
/* 33:   */           }
/* 34:   */         }
/* 35:   */       }
/* 36:38 */       for (int i = 1; i <= howManyTimes; i++)
/* 37:   */       {
/* 38:39 */         PrintWriter pwTr = new PrintWriter(new BufferedWriter(new FileWriter("train" + i + ".arff")));
/* 39:40 */         PrintWriter pwTs = new PrintWriter(new BufferedWriter(new FileWriter("test" + i + ".arff")));
/* 40:   */         
/* 41:42 */         pwTr.println(header);
/* 42:43 */         pwTs.println(header);
/* 43:47 */         for (int k = 0; k < classCount; k++)
/* 44:   */         {
/* 45:50 */           int index = r.nextInt(4);
/* 46:52 */           for (int l = 0; l < 4; l++) {
/* 47:53 */             if (l == index) {
/* 48:54 */               for (int j = 0; j < 108; j++) {
/* 49:55 */                 pwTr.println(allSamples[k][l][j]);
/* 50:   */               }
/* 51:   */             } else {
/* 52:57 */               for (int j = 0; j < 108; j++) {
/* 53:58 */                 pwTs.println(allSamples[k][l][j]);
/* 54:   */               }
/* 55:   */             }
/* 56:   */           }
/* 57:   */         }
/* 58:63 */         pwTr.close();
/* 59:64 */         pwTs.close();
/* 60:   */       }
/* 61:67 */       data.close();
/* 62:   */     }
/* 63:   */     catch (Exception e)
/* 64:   */     {
/* 65:69 */       e.printStackTrace();
/* 66:   */     }
/* 67:   */   }
/* 68:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.RandomTrainTestFileProduction1
 * JD-Core Version:    0.7.0.1
 */