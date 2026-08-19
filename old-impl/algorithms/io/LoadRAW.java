/*  1:   */ package vpt.algorithms.io;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.FileReader;
/*  5:   */ import java.util.ArrayList;
/*  6:   */ import vpt.Algorithm;
/*  7:   */ import vpt.ByteImage;
/*  8:   */ import vpt.GlobalException;
/*  9:   */ import vpt.Image;
/* 10:   */ 
/* 11:   */ public class LoadRAW
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:26 */   public Image output = null;
/* 15:27 */   public String path = null;
/* 16:   */   
/* 17:   */   public LoadRAW()
/* 18:   */   {
/* 19:32 */     this.inputFields = "path";
/* 20:33 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:39 */     ArrayList<ArrayList<Integer>> pixels = new ArrayList();
/* 27:   */     try
/* 28:   */     {
/* 29:42 */       BufferedReader br = new BufferedReader(new FileReader(this.path));
/* 30:   */       
/* 31:44 */       String s = null;
/* 32:45 */       ArrayList<Integer> pixel = null;
/* 33:48 */       while ((s = br.readLine()) != null)
/* 34:   */       {
/* 35:50 */         String[] tokens = s.split(",");
/* 36:   */         
/* 37:52 */         pixel = new ArrayList();
/* 38:54 */         for (String token : tokens) {
/* 39:55 */           pixel.add(Integer.valueOf(Integer.parseInt(token)));
/* 40:   */         }
/* 41:57 */         pixels.add(pixel);
/* 42:   */       }
/* 43:59 */       br.close();
/* 44:   */     }
/* 45:   */     catch (Exception e)
/* 46:   */     {
/* 47:62 */       e.printStackTrace();
/* 48:   */     }
/* 49:66 */     int height = pixels.size();
/* 50:67 */     int width = ((ArrayList)pixels.get(0)).size();
/* 51:   */     
/* 52:69 */     this.output = new ByteImage(width, height);
/* 53:70 */     this.output.fill(0);
/* 54:72 */     for (int y = 0; y < height; y++)
/* 55:   */     {
/* 56:73 */       ArrayList<Integer> line = (ArrayList)pixels.get(y);
/* 57:75 */       for (int x = 0; x < width; x++) {
/* 58:76 */         this.output.setXYByte(x, y, ((Integer)line.get(x)).intValue());
/* 59:   */       }
/* 60:   */     }
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static Image invoke(String path)
/* 64:   */   {
/* 65:   */     try
/* 66:   */     {
/* 67:92 */       return (Image)new LoadRAW().preprocess(new Object[] { path });
/* 68:   */     }
/* 69:   */     catch (GlobalException e)
/* 70:   */     {
/* 71:95 */       e.printStackTrace();
/* 72:   */     }
/* 73:96 */     return null;
/* 74:   */   }
/* 75:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.io.LoadRAW
 * JD-Core Version:    0.7.0.1
 */