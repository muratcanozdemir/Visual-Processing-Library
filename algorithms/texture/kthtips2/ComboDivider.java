/*  1:   */ package vpt.algorithms.texture.kthtips2;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintStream;
/*  8:   */ import java.io.PrintWriter;
/*  9:   */ 
/* 10:   */ public class ComboDivider
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:   */     try
/* 15:   */     {
/* 16:14 */       BufferedReader br = new BufferedReader(new FileReader("tips2b-combo-12-2.dat"));
/* 17:   */       
/* 18:16 */       PrintWriter pw0 = new PrintWriter(new BufferedWriter(new FileWriter("volume.dat")));
/* 19:17 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("fouriervolume.dat")));
/* 20:18 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("moment.dat")));
/* 21:19 */       PrintWriter pw3 = new PrintWriter(new BufferedWriter(new FileWriter("fouriermoment.dat")));
/* 22:20 */       PrintWriter pw4 = new PrintWriter(new BufferedWriter(new FileWriter("locations.dat")));
/* 23:   */       
/* 24:22 */       String s = null;
/* 25:24 */       while ((s = br.readLine()) != null)
/* 26:   */       {
/* 27:25 */         String[] tokens = s.split(",");
/* 28:27 */         if (tokens.length != 121) {
/* 29:27 */           System.err.println("121 degil");
/* 30:   */         }
/* 31:29 */         for (int i = 0; i < 12; i++)
/* 32:   */         {
/* 33:30 */           pw0.print(tokens[(i * 10 + 0)] + "," + tokens[(i * 10 + 5)] + ",");
/* 34:31 */           pw1.print(tokens[(i * 10 + 1)] + "," + tokens[(i * 10 + 6)] + ",");
/* 35:32 */           pw2.print(tokens[(i * 10 + 2)] + "," + tokens[(i * 10 + 7)] + ",");
/* 36:33 */           pw3.print(tokens[(i * 10 + 3)] + "," + tokens[(i * 10 + 8)] + ",");
/* 37:34 */           pw4.print(tokens[(i * 10 + 4)] + "," + tokens[(i * 10 + 9)] + ",");
/* 38:   */         }
/* 39:36 */         pw0.println(tokens[120]);
/* 40:37 */         pw1.println(tokens[120]);
/* 41:38 */         pw2.println(tokens[120]);
/* 42:39 */         pw3.println(tokens[120]);
/* 43:40 */         pw4.println(tokens[120]);
/* 44:   */       }
/* 45:43 */       pw0.close();
/* 46:44 */       pw1.close();
/* 47:45 */       pw2.close();
/* 48:46 */       pw3.close();
/* 49:47 */       pw4.close();
/* 50:   */       
/* 51:49 */       br.close();
/* 52:   */     }
/* 53:   */     catch (Exception e)
/* 54:   */     {
/* 55:51 */       e.printStackTrace();
/* 56:   */     }
/* 57:   */   }
/* 58:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.ComboDivider
 * JD-Core Version:    0.7.0.1
 */