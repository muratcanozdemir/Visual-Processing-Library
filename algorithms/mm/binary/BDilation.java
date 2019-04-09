/*  1:   */ package vpt.algorithms.mm.binary;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class BDilation
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:11 */   public Image output = null;
/* 13:12 */   public Image input = null;
/* 14:13 */   public FlatSE se = null;
/* 15:   */   private int xdim;
/* 16:   */   private int ydim;
/* 17:   */   private int cdim;
/* 18:   */   
/* 19:   */   public BDilation()
/* 20:   */   {
/* 21:20 */     this.inputFields = "input,se";
/* 22:21 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:25 */     this.output = this.input.newInstance(false);
/* 29:   */     
/* 30:27 */     this.xdim = this.output.getXDim();
/* 31:28 */     this.ydim = this.output.getYDim();
/* 32:29 */     this.cdim = this.output.getCDim();
/* 33:32 */     if (!this.se.BOX)
/* 34:   */     {
/* 35:33 */       for (int x = 0; x < this.xdim; x++) {
/* 36:34 */         for (int y = 0; y < this.ydim; y++) {
/* 37:35 */           for (int c = 0; c < this.cdim; c++) {
/* 38:36 */             this.output.setXYCBoolean(x, y, c, getMax(this.input, x, y, c, this.se.getCoords()));
/* 39:   */           }
/* 40:   */         }
/* 41:   */       }
/* 42:   */     }
/* 43:   */     else
/* 44:   */     {
/* 45:39 */       FlatSE hline = this.se.getHLine();
/* 46:40 */       FlatSE vline = this.se.getVLine();
/* 47:41 */       Image tmp = this.input.newInstance(false);
/* 48:43 */       for (int x = 0; x < this.xdim; x++) {
/* 49:44 */         for (int y = 0; y < this.ydim; y++) {
/* 50:45 */           for (int c = 0; c < this.cdim; c++) {
/* 51:46 */             tmp.setXYCBoolean(x, y, c, getMax(this.input, x, y, c, hline.getCoords()));
/* 52:   */           }
/* 53:   */         }
/* 54:   */       }
/* 55:48 */       for (int x = 0; x < this.xdim; x++) {
/* 56:49 */         for (int y = 0; y < this.ydim; y++) {
/* 57:50 */           for (int c = 0; c < this.cdim; c++) {
/* 58:51 */             this.output.setXYCBoolean(x, y, c, getMax(tmp, x, y, c, vline.getCoords()));
/* 59:   */           }
/* 60:   */         }
/* 61:   */       }
/* 62:   */     }
/* 63:   */   }
/* 64:   */   
/* 65:   */   private boolean getMax(Image img, int x, int y, int c, Point[] coords)
/* 66:   */   {
/* 67:58 */     boolean max = false;
/* 68:59 */     boolean flag = false;
/* 69:61 */     for (int i = 0; i < coords.length; i++)
/* 70:   */     {
/* 71:64 */       int _x = x - coords[i].x;
/* 72:65 */       int _y = y - coords[i].y;
/* 73:67 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/* 74:   */       {
/* 75:71 */         flag = true;
/* 76:72 */         boolean p = img.getXYCBoolean(_x, _y, c);
/* 77:74 */         if (p)
/* 78:   */         {
/* 79:75 */           max = true;
/* 80:76 */           break;
/* 81:   */         }
/* 82:   */       }
/* 83:   */     }
/* 84:81 */     return flag ? max : img.getXYCBoolean(x, y, c);
/* 85:   */   }
/* 86:   */   
/* 87:   */   public static Image invoke(Image image, FlatSE se)
/* 88:   */   {
/* 89:   */     try
/* 90:   */     {
/* 91:87 */       return (Image)new BDilation().preprocess(new Object[] { image, se });
/* 92:   */     }
/* 93:   */     catch (GlobalException e)
/* 94:   */     {
/* 95:89 */       e.printStackTrace();
/* 96:   */     }
/* 97:90 */     return null;
/* 98:   */   }
/* 99:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.BDilation
 * JD-Core Version:    0.7.0.1
 */