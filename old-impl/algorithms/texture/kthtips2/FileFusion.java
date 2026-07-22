/*  1:   */ package vpt.algorithms.texture.kthtips2;
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
/* 15:14 */       String file1 = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/kth-tips2b/test146.dat";
/* 16:15 */       String file2 = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/kth-tips2b/test149.dat";
/* 17:16 */       String output = "/home/yoktish/workspace/My DIP Toolbox/valuation tests/kth-tips2b/test150.dat";
/* 18:   */       
/* 19:18 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output)));
/* 20:   */       
/* 21:20 */       BufferedReader br1 = new BufferedReader(new FileReader(file1));
/* 22:21 */       BufferedReader br2 = new BufferedReader(new FileReader(file2));
/* 23:   */       
/* 24:   */ 
/* 25:24 */       String s1 = null;
/* 26:25 */       String s2 = null;
/* 27:27 */       while (((s1 = br1.readLine()) != null) && ((s2 = br2.readLine()) != null))
/* 28:   */       {
/* 29:28 */         s1 = s1.substring(0, s1.lastIndexOf(',') + 1);
/* 30:29 */         pw.println(s1 + s2);
/* 31:   */       }
/* 32:32 */       pw.close();
/* 33:33 */       br1.close();
/* 34:34 */       br2.close();
/* 35:   */     }
/* 36:   */     catch (Exception e)
/* 37:   */     {
/* 38:36 */       e.printStackTrace();
/* 39:   */     }
/* 40:   */   }
/* 41:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.FileFusion
 * JD-Core Version:    0.7.0.1
 */