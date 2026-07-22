/*  1:   */ package vpt.algorithms.texture.kthtips2;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.BufferedWriter;
/*  5:   */ import java.io.FileReader;
/*  6:   */ import java.io.FileWriter;
/*  7:   */ import java.io.PrintStream;
/*  8:   */ import java.io.PrintWriter;
/*  9:   */ 
/* 10:   */ public class ECOVDivider
/* 11:   */ {
/* 12:   */   public static void main(String[] args)
/* 13:   */   {
/* 14:   */     try
/* 15:   */     {
/* 16:14 */       BufferedReader br = new BufferedReader(new FileReader("tips2b-satweighted2.dat"));
/* 17:   */       
/* 18:16 */       PrintWriter pw0 = new PrintWriter(new BufferedWriter(new FileWriter("histoED.dat")));
/* 19:17 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter("histoOCD.dat")));
/* 20:18 */       PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter("locHisto.dat")));
/* 21:19 */       PrintWriter pw3 = new PrintWriter(new BufferedWriter(new FileWriter("ritMaxMin.dat")));
/* 22:20 */       PrintWriter pw4 = new PrintWriter(new BufferedWriter(new FileWriter("ritMinMin.dat")));
/* 23:21 */       PrintWriter pw5 = new PrintWriter(new BufferedWriter(new FileWriter("ritMed.dat")));
/* 24:22 */       PrintWriter pw6 = new PrintWriter(new BufferedWriter(new FileWriter("ritVar.dat")));
/* 25:23 */       PrintWriter pw7 = new PrintWriter(new BufferedWriter(new FileWriter("ritAngleDiff.dat")));
/* 26:   */       
/* 27:25 */       String s = null;
/* 28:27 */       while ((s = br.readLine()) != null)
/* 29:   */       {
/* 30:28 */         String[] tokens = s.split(",");
/* 31:30 */         if (tokens.length != 219) {
/* 32:30 */           System.err.println("219 degil");
/* 33:   */         }
/* 34:33 */         for (int i = 0; i < 24; i++) {
/* 35:34 */           pw0.print(tokens[i] + ",");
/* 36:   */         }
/* 37:35 */         pw0.println(tokens['Ú']);
/* 38:38 */         for (int i = 24; i < 72; i++) {
/* 39:39 */           pw1.print(tokens[i] + ",");
/* 40:   */         }
/* 41:40 */         pw1.println(tokens['Ú']);
/* 42:43 */         for (int i = 72; i < 98; i++) {
/* 43:44 */           pw2.print(tokens[i] + ",");
/* 44:   */         }
/* 45:45 */         pw2.println(tokens['Ú']);
/* 46:48 */         for (int i = 98; i < 122; i++) {
/* 47:49 */           pw3.print(tokens[i] + ",");
/* 48:   */         }
/* 49:50 */         pw3.println(tokens['Ú']);
/* 50:53 */         for (int i = 122; i < 146; i++) {
/* 51:54 */           pw4.print(tokens[i] + ",");
/* 52:   */         }
/* 53:55 */         pw4.println(tokens['Ú']);
/* 54:58 */         for (int i = 146; i < 170; i++) {
/* 55:59 */           pw5.print(tokens[i] + ",");
/* 56:   */         }
/* 57:60 */         pw5.println(tokens['Ú']);
/* 58:63 */         for (int i = 170; i < 194; i++) {
/* 59:64 */           pw6.print(tokens[i] + ",");
/* 60:   */         }
/* 61:65 */         pw6.println(tokens['Ú']);
/* 62:68 */         for (int i = 194; i < 218; i++) {
/* 63:69 */           pw7.print(tokens[i] + ",");
/* 64:   */         }
/* 65:70 */         pw7.println(tokens['Ú']);
/* 66:   */       }
/* 67:74 */       pw0.close();
/* 68:75 */       pw1.close();
/* 69:76 */       pw2.close();
/* 70:77 */       pw3.close();
/* 71:78 */       pw4.close();
/* 72:79 */       pw5.close();
/* 73:80 */       pw6.close();
/* 74:81 */       pw7.close();
/* 75:   */       
/* 76:83 */       br.close();
/* 77:   */     }
/* 78:   */     catch (Exception e)
/* 79:   */     {
/* 80:85 */       e.printStackTrace();
/* 81:   */     }
/* 82:   */   }
/* 83:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.kthtips2.ECOVDivider
 * JD-Core Version:    0.7.0.1
 */