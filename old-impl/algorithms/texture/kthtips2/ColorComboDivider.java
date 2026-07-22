/*  1:   */ package vpt.algorithms.texture.kthtips2;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintStream;
/*  8:   */ import java.io.PrintWriter;
/*  9:   */ 
/* 10:   */ public class ColorComboDivider
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:   */     try
/* 15:   */     {
/* 16:14 */       BufferedReader br = new BufferedReader(new FileReader("tips2b-combo-12-1-rgb.dat"));
/* 17:   */       
/* 18:16 */       PrintWriter pw0 = new PrintWriter(new BufferedWriter(new FileWriter("volume-rgb.dat")));
/* 19:17 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("fouriervolume-rgb.dat")));
/* 20:18 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("moment-rgb.dat")));
/* 21:19 */       PrintWriter pw3 = new PrintWriter(new BufferedWriter(new FileWriter("fouriermoment-rgb.dat")));
/* 22:20 */       PrintWriter pw4 = new PrintWriter(new BufferedWriter(new FileWriter("locations-rgb.dat")));
/* 23:   */       
/* 24:22 */       String s = null;
/* 25:24 */       while ((s = br.readLine()) != null)
/* 26:   */       {
/* 27:25 */         String[] tokens = s.split(",");
/* 28:27 */         if (tokens.length != 361) {
/* 29:27 */           System.err.println("361 degil");
/* 30:   */         }
/* 31:29 */         for (int i = 0; i < 12; i++)
/* 32:   */         {
/* 33:30 */           pw0.print(tokens[(i * 10 + 0)] + "," + tokens[(i * 10 + 5)] + ",");
/* 34:31 */           pw1.print(tokens[(i * 10 + 1)] + "," + tokens[(i * 10 + 6)] + ",");
/* 35:32 */           pw2.print(tokens[(i * 10 + 2)] + "," + tokens[(i * 10 + 7)] + ",");
/* 36:33 */           pw3.print(tokens[(i * 10 + 3)] + "," + tokens[(i * 10 + 8)] + ",");
/* 37:34 */           pw4.print(tokens[(i * 10 + 4)] + "," + tokens[(i * 10 + 9)] + ",");
/* 38:   */           
/* 39:36 */           pw0.print(tokens[(i * 10 + 10)] + "," + tokens[(i * 10 + 15)] + ",");
/* 40:37 */           pw1.print(tokens[(i * 10 + 11)] + "," + tokens[(i * 10 + 16)] + ",");
/* 41:38 */           pw2.print(tokens[(i * 10 + 12)] + "," + tokens[(i * 10 + 17)] + ",");
/* 42:39 */           pw3.print(tokens[(i * 10 + 13)] + "," + tokens[(i * 10 + 18)] + ",");
/* 43:40 */           pw4.print(tokens[(i * 10 + 14)] + "," + tokens[(i * 10 + 19)] + ",");
/* 44:   */           
/* 45:42 */           pw0.print(tokens[(i * 10 + 20)] + "," + tokens[(i * 10 + 25)] + ",");
/* 46:43 */           pw1.print(tokens[(i * 10 + 21)] + "," + tokens[(i * 10 + 26)] + ",");
/* 47:44 */           pw2.print(tokens[(i * 10 + 22)] + "," + tokens[(i * 10 + 27)] + ",");
/* 48:45 */           pw3.print(tokens[(i * 10 + 23)] + "," + tokens[(i * 10 + 28)] + ",");
/* 49:46 */           pw4.print(tokens[(i * 10 + 24)] + "," + tokens[(i * 10 + 29)] + ",");
/* 50:   */         }
/* 51:48 */         pw0.println(tokens[360]);
/* 52:49 */         pw1.println(tokens[360]);
/* 53:50 */         pw2.println(tokens[360]);
/* 54:51 */         pw3.println(tokens[360]);
/* 55:52 */         pw4.println(tokens[360]);
/* 56:   */       }
/* 57:55 */       pw0.close();
/* 58:56 */       pw1.close();
/* 59:57 */       pw2.close();
/* 60:58 */       pw3.close();
/* 61:59 */       pw4.close();
/* 62:   */       
/* 63:61 */       br.close();
/* 64:   */     }
/* 65:   */     catch (Exception e)
/* 66:   */     {
/* 67:63 */       e.printStackTrace();
/* 68:   */     }
/* 69:   */   }
/* 70:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.ColorComboDivider
 * JD-Core Version:    0.7.0.1
 */