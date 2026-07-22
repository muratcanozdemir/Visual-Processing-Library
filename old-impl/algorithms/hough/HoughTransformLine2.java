/*  1:   */ package vpt.algorithms.hough;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.display.Display2D;
/*  8:   */ import vpt.algorithms.linear.Sobel;
/*  9:   */ import vpt.util.Tools;
/* 10:   */ 
/* 11:   */ public class HoughTransformLine2
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:29 */   public Image output = null;
/* 15:30 */   public Image input = null;
/* 16:   */   
/* 17:   */   public HoughTransformLine2()
/* 18:   */   {
/* 19:33 */     this.inputFields = "input,";
/* 20:34 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:40 */     this.output = new ByteImage(180, 2 * this.input.getDiagonal(), this.input.getCDim());
/* 27:41 */     this.output.fill(0);
/* 28:   */     
/* 29:   */ 
/* 30:44 */     Image edges = Sobel.invoke(this.input, Integer.valueOf(2), Boolean.valueOf(true));
/* 31:45 */     Display2D.invoke(edges, "", Boolean.valueOf(false));
/* 32:   */     
/* 33:   */ 
/* 34:48 */     Image theta = Sobel.invoke(this.input, Integer.valueOf(3), Boolean.valueOf(false));
/* 35:50 */     for (int c = 0; c < edges.getCDim(); c++) {
/* 36:51 */       for (int x = 0; x < edges.getXDim(); x++) {
/* 37:52 */         for (int y = 0; y < edges.getYDim(); y++)
/* 38:   */         {
/* 39:53 */           int p = edges.getXYCByte(x, y, c);
/* 40:55 */           if (p != 0)
/* 41:   */           {
/* 42:57 */             double phi = theta.getXYCDouble(x, y, c);
/* 43:   */             
/* 44:59 */             double phi2 = phi;
/* 45:60 */             if (phi2 < 0.0D) {
/* 46:60 */               phi2 += 3.141592653589793D;
/* 47:   */             }
/* 48:62 */             double r = x * Math.cos(phi2) + y * Math.sin(phi2);
/* 49:   */             
/* 50:64 */             double r2 = r;
/* 51:   */             
/* 52:   */ 
/* 53:   */ 
/* 54:68 */             phi2 = Tools.radian2degree(phi2);
/* 55:69 */             phi2 = Math.min(phi2, this.output.getXDim() - 1);
/* 56:   */             
/* 57:71 */             this.output.setXYCByte((int)phi2, (int)r2 + this.input.getDiagonal(), c, Math.min(this.output.getXYCByte((int)phi2, (int)r2 + this.input.getDiagonal(), c) + 1, 255));
/* 58:   */           }
/* 59:   */         }
/* 60:   */       }
/* 61:   */     }
/* 62:   */   }
/* 63:   */   
/* 64:   */   public static Image invoke(Image image)
/* 65:   */   {
/* 66:   */     try
/* 67:   */     {
/* 68:81 */       return (Image)new HoughTransformLine2().preprocess(new Object[] { image });
/* 69:   */     }
/* 70:   */     catch (GlobalException e)
/* 71:   */     {
/* 72:83 */       e.printStackTrace();
/* 73:   */     }
/* 74:84 */     return null;
/* 75:   */   }
/* 76:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.hough.HoughTransformLine2
 * JD-Core Version:    0.7.0.1
 */