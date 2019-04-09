/*  1:   */ package vpt.algorithms.texture.outex;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ 
/*  9:   */ public class FeatureJoiner
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:19 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("train27.arff")));
/* 16:   */       
/* 17:21 */       BufferedReader br1 = new BufferedReader(new FileReader("train20.arff"));
/* 18:22 */       BufferedReader br2 = new BufferedReader(new FileReader("train21.arff"));
/* 19:   */       
/* 20:   */ 
/* 21:25 */       String s1 = null;
/* 22:26 */       String s2 = null;
/* 23:28 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 24:   */       {
/* 25:30 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/* 26:31 */         pw.println(s1 + ',' + s2);
/* 27:   */       }
/* 28:33 */       pw.close();
/* 29:34 */       br1.close();
/* 30:35 */       br2.close();
/* 31:   */       
/* 32:37 */       pw = new PrintWriter(new BufferedWriter(new FileWriter("test152.arff")));
/* 33:   */       
/* 34:39 */       br1 = new BufferedReader(new FileReader("test146.arff"));
/* 35:40 */       br2 = new BufferedReader(new FileReader("test147.arff"));
/* 36:   */       
/* 37:   */ 
/* 38:43 */       s1 = null;
/* 39:44 */       s2 = null;
/* 40:46 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 41:   */       {
/* 42:48 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/* 43:49 */         pw.println(s1 + ',' + s2);
/* 44:   */       }
/* 45:51 */       pw.close();
/* 46:52 */       br1.close();
/* 47:53 */       br2.close();
/* 48:   */     }
/* 49:   */     catch (Exception e)
/* 50:   */     {
/* 51:55 */       e.printStackTrace();
/* 52:   */     }
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.FeatureJoiner
 * JD-Core Version:    0.7.0.1
 */