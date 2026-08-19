/*  1:   */ package vpt.algorithms.texture.outex;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ 
/*  9:   */ public class FeatureJoinerOutex12
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:19 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("train3.arff")));
/* 16:   */       
/* 17:21 */       BufferedReader br1 = new BufferedReader(new FileReader("train1.arff"));
/* 18:22 */       BufferedReader br2 = new BufferedReader(new FileReader("train2.arff"));
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
/* 32:37 */       pw = new PrintWriter(new BufferedWriter(new FileWriter("test3.arff")));
/* 33:   */       
/* 34:39 */       br1 = new BufferedReader(new FileReader("test1.arff"));
/* 35:40 */       br2 = new BufferedReader(new FileReader("test2.arff"));
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
/* 48:   */       
/* 49:55 */       pw = new PrintWriter(new BufferedWriter(new FileWriter("test3b.arff")));
/* 50:   */       
/* 51:57 */       br1 = new BufferedReader(new FileReader("test1b.arff"));
/* 52:58 */       br2 = new BufferedReader(new FileReader("test2b.arff"));
/* 53:   */       
/* 54:   */ 
/* 55:61 */       s1 = null;
/* 56:62 */       s2 = null;
/* 57:64 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 58:   */       {
/* 59:66 */         s1 = s1.substring(0, s1.lastIndexOf(','));
/* 60:67 */         pw.println(s1 + ',' + s2);
/* 61:   */       }
/* 62:69 */       pw.close();
/* 63:70 */       br1.close();
/* 64:71 */       br2.close();
/* 65:   */     }
/* 66:   */     catch (Exception e)
/* 67:   */     {
/* 68:74 */       e.printStackTrace();
/* 69:   */     }
/* 70:   */   }
/* 71:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.outex.FeatureJoinerOutex12
 * JD-Core Version:    0.7.0.1
 */