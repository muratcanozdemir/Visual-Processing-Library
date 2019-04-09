/*  1:   */ package vpt.algorithms.hough;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.mm.gray.GExternGradient;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class HoughTransformLine
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:29 */   public Image output = null;
/* 15:30 */   public Image input = null;
/* 16:   */   
/* 17:   */   public HoughTransformLine()
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
/* 30:44 */     Image edges = GExternGradient.invoke(this.input, FlatSE.square(3));
/* 31:46 */     for (int c = 0; c < edges.getCDim(); c++) {
/* 32:47 */       for (int x = 0; x < edges.getXDim(); x++) {
/* 33:48 */         for (int y = 0; y < edges.getYDim(); y++)
/* 34:   */         {
/* 35:49 */           int p = edges.getXYCByte(x, y, c);
/* 36:51 */           if (p != 0) {
/* 37:53 */             for (double phi = -1.570796326794897D; phi < 1.570796326794897D; phi += 0.0174532925199433D)
/* 38:   */             {
/* 39:55 */               double phi2 = phi;
/* 40:56 */               if (phi2 < 0.0D) {
/* 41:56 */                 phi2 += 3.141592653589793D;
/* 42:   */               }
/* 43:58 */               double r = x * Math.cos(phi2) + y * Math.sin(phi2);
/* 44:   */               
/* 45:60 */               double r2 = r;
/* 46:   */               
/* 47:62 */               phi2 = Tools.radian2degree(phi2);
/* 48:63 */               phi2 = Math.min(phi2, this.output.getXDim() - 1);
/* 49:64 */               this.output.setXYCByte((int)phi2, (int)r2 + this.input.getDiagonal(), c, Math.min(this.output.getXYCByte((int)phi2, (int)r2 + this.input.getDiagonal(), c) + 1, 255));
/* 50:   */             }
/* 51:   */           }
/* 52:   */         }
/* 53:   */       }
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:   */   public static Image invoke(Image image)
/* 58:   */   {
/* 59:   */     try
/* 60:   */     {
/* 61:75 */       return (Image)new HoughTransformLine().preprocess(new Object[] { image });
/* 62:   */     }
/* 63:   */     catch (GlobalException e)
/* 64:   */     {
/* 65:77 */       e.printStackTrace();
/* 66:   */     }
/* 67:78 */     return null;
/* 68:   */   }
/* 69:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.hough.HoughTransformLine
 * JD-Core Version:    0.7.0.1
 */