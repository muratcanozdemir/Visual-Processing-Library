/*  1:   */ package vpt.algorithms.segmentation.berkeley;
/*  2:   */ 
/*  3:   */ import java.io.BufferedReader;
/*  4:   */ import java.io.FileReader;
/*  5:   */ import vpt.Algorithm;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.IntegerImage;
/*  9:   */ 
/* 10:   */ public class Seg2Image
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:18 */   public String path = null;
/* 14:19 */   public Image output = null;
/* 15:   */   
/* 16:   */   public Seg2Image()
/* 17:   */   {
/* 18:22 */     this.inputFields = "path";
/* 19:23 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:   */     try
/* 26:   */     {
/* 27:29 */       BufferedReader file = new BufferedReader(new FileReader(this.path));
/* 28:   */       
/* 29:31 */       String s = null;
/* 30:   */       
/* 31:33 */       int width = -1;int height = -1;
/* 32:35 */       while ((s = file.readLine()) != null)
/* 33:   */       {
/* 34:37 */         String[] tags = s.split(" ");
/* 35:39 */         if (tags[0].equals("width")) {
/* 36:40 */           width = Integer.parseInt(tags[1]);
/* 37:41 */         } else if (tags[0].equals("height")) {
/* 38:42 */           height = Integer.parseInt(tags[1]);
/* 39:   */         } else {
/* 40:43 */           if (tags[0].equals("data")) {
/* 41:   */             break;
/* 42:   */           }
/* 43:   */         }
/* 44:   */       }
/* 45:48 */       if ((width == -1) || (height == -1)) {
/* 46:48 */         throw new GlobalException("Could not find either width or height");
/* 47:   */       }
/* 48:49 */       this.output = new IntegerImage(width, height, 1);
/* 49:   */       
/* 50:51 */       this.output.fill(0);
/* 51:   */       
/* 52:53 */       int cnt = 0;
/* 53:   */       int col2;
/* 54:   */       int x;
/* 55:56 */       for (; (s = file.readLine()) != null; x <= col2)
/* 56:   */       {
/* 57:58 */         String[] tags = s.split(" ");
/* 58:   */         
/* 59:   */ 
/* 60:61 */         int segmentId = Integer.parseInt(tags[0]);
/* 61:62 */         int row = Integer.parseInt(tags[1]);
/* 62:63 */         int col1 = Integer.parseInt(tags[2]);
/* 63:64 */         col2 = Integer.parseInt(tags[3]);
/* 64:   */         
/* 65:66 */         x = col1; continue;
/* 66:67 */         this.output.setXYInt(x, row, segmentId);
/* 67:68 */         cnt++;x++;
/* 68:   */       }
/* 69:72 */       if (cnt != width * height) {
/* 70:72 */         throw new GlobalException("Missing pixel value(s)!");
/* 71:   */       }
/* 72:   */     }
/* 73:   */     catch (Exception e)
/* 74:   */     {
/* 75:75 */       throw new GlobalException("Something wrong has happened...");
/* 76:   */     }
/* 77:   */   }
/* 78:   */   
/* 79:   */   public static Image invoke(String path)
/* 80:   */   {
/* 81:   */     try
/* 82:   */     {
/* 83:82 */       return (Image)new Seg2Image().preprocess(new Object[] { path });
/* 84:   */     }
/* 85:   */     catch (GlobalException e)
/* 86:   */     {
/* 87:84 */       e.printStackTrace();
/* 88:   */     }
/* 89:85 */     return null;
/* 90:   */   }
/* 91:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.segmentation.berkeley.Seg2Image
 * JD-Core Version:    0.7.0.1
 */