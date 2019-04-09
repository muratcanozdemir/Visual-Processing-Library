/*  1:   */ package vpt.algorithms.texture.outex;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ 
/*  9:   */ public class FeatureExtractor
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:18 */       BufferedReader br = new BufferedReader(new FileReader("test.arff"));
/* 16:19 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("test1.arff")));
/* 17:   */       
/* 18:21 */       String s1 = "";
/* 19:24 */       while ((s1 = br.readLine()) != null)
/* 20:   */       {
/* 21:25 */         String s2 = "";
/* 22:   */         
/* 23:   */ 
/* 24:28 */         String[] tokens = s1.split(",");
/* 25:30 */         for (int i = 12; i < 24; i++) {
/* 26:31 */           s2 = s2 + tokens[i] + ",";
/* 27:   */         }
/* 28:33 */         for (int i = 36; i < 48; i++) {
/* 29:34 */           s2 = s2 + tokens[i] + ",";
/* 30:   */         }
/* 31:36 */         s2 = s2 + tokens[48];
/* 32:   */         
/* 33:38 */         pw.println(s2);
/* 34:   */       }
/* 35:41 */       pw.close();
/* 36:42 */       br.close();
/* 37:   */     }
/* 38:   */     catch (Exception e)
/* 39:   */     {
/* 40:44 */       e.printStackTrace();
/* 41:   */     }
/* 42:   */   }
/* 43:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.FeatureExtractor
 * JD-Core Version:    0.7.0.1
 */