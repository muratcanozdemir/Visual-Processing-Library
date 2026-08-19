/*  1:   */ package vpt.algorithms.texture.curet;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintWriter;
/*  8:   */ 
/*  9:   */ public class FileFusion
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:19 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("data.dat")));
/* 16:   */       
/* 17:21 */       BufferedReader br1 = new BufferedReader(new FileReader("train3.arff"));
/* 18:22 */       BufferedReader br2 = new BufferedReader(new FileReader("test3.arff"));
/* 19:   */       
/* 20:   */ 
/* 21:   */ 
/* 22:   */ 
/* 23:27 */       String s1 = null;
/* 24:28 */       String s2 = null;
/* 25:30 */       while ((s1 = br1.readLine()) != null) {
/* 26:31 */         pw.println(s1);
/* 27:   */       }
/* 28:34 */       while ((s2 = br2.readLine()) != null) {
/* 29:35 */         pw.println(s2);
/* 30:   */       }
/* 31:38 */       pw.close();
/* 32:39 */       br1.close();
/* 33:40 */       br2.close();
/* 34:   */     }
/* 35:   */     catch (Exception e)
/* 36:   */     {
/* 37:42 */       e.printStackTrace();
/* 38:   */     }
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.curet.FileFusion
 * JD-Core Version:    0.7.0.1
 */