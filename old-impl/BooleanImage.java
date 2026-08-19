/*  1:   */ package vpt;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ 
/*  5:   */ public class BooleanImage
/*  6:   */   extends Image
/*  7:   */ {
/*  8:   */   private boolean[] pixels;
/*  9:   */   
/* 10:   */   public BooleanImage(int xdim, int ydim)
/* 11:   */   {
/* 12:15 */     this.xdim = xdim;
/* 13:16 */     this.ydim = ydim;
/* 14:17 */     this.cdim = 1;
/* 15:   */     
/* 16:19 */     this.pixels = new boolean[xdim * ydim];
/* 17:   */   }
/* 18:   */   
/* 19:   */   public BooleanImage(int xdim, int ydim, int cdim)
/* 20:   */   {
/* 21:23 */     this.xdim = xdim;
/* 22:24 */     this.ydim = ydim;
/* 23:25 */     this.cdim = cdim;
/* 24:   */     
/* 25:27 */     this.pixels = new boolean[xdim * ydim * cdim];
/* 26:   */   }
/* 27:   */   
/* 28:   */   public BooleanImage(Image img, boolean deepCopy)
/* 29:   */   {
/* 30:31 */     this.xdim = img.getXDim();
/* 31:32 */     this.ydim = img.getYDim();
/* 32:33 */     this.cdim = img.getCDim();
/* 33:   */     
/* 34:35 */     this.pixels = new boolean[this.xdim * this.ydim * this.cdim];
/* 35:37 */     if (deepCopy) {
/* 36:38 */       for (int i = 0; i < this.pixels.length; i++) {
/* 37:39 */         setBoolean(i, img.getBoolean(i));
/* 38:   */       }
/* 39:   */     }
/* 40:   */   }
/* 41:   */   
/* 42:   */   public Image newInstance(boolean deepCopy)
/* 43:   */   {
/* 44:46 */     return new BooleanImage(this, deepCopy);
/* 45:   */   }
/* 46:   */   
/* 47:   */   public Image newInstance(int xdim, int ydim, int cdim)
/* 48:   */   {
/* 49:51 */     return new BooleanImage(xdim, ydim, cdim);
/* 50:   */   }
/* 51:   */   
/* 52:   */   public int size()
/* 53:   */   {
/* 54:55 */     return this.pixels.length;
/* 55:   */   }
/* 56:   */   
/* 57:   */   public void fill(double p)
/* 58:   */   {
/* 59:59 */     Arrays.fill(this.pixels, p > 0.0D);
/* 60:   */   }
/* 61:   */   
/* 62:   */   public void fill(int p)
/* 63:   */   {
/* 64:63 */     Arrays.fill(this.pixels, p > 0);
/* 65:   */   }
/* 66:   */   
/* 67:   */   public void fill(boolean p)
/* 68:   */   {
/* 69:67 */     Arrays.fill(this.pixels, p);
/* 70:   */   }
/* 71:   */   
/* 72:   */   public boolean getBoolean(int i)
/* 73:   */   {
/* 74:71 */     return this.pixels[i];
/* 75:   */   }
/* 76:   */   
/* 77:   */   public void setBoolean(int i, boolean p)
/* 78:   */   {
/* 79:75 */     this.pixels[i] = p;
/* 80:   */   }
/* 81:   */   
/* 82:   */   public int getInt(int i)
/* 83:   */   {
/* 84:79 */     return this.pixels[i] == false ? 2147483647 : -2147483648;
/* 85:   */   }
/* 86:   */   
/* 87:   */   public void setInt(int i, int p)
/* 88:   */   {
/* 89:83 */     this.pixels[i] = (boolean) (p != -2147483648 ? 1 : false);
/* 90:   */   }
/* 91:   */   
/* 92:   */   public int getByte(int i)
/* 93:   */   {
/* 94:87 */     return this.pixels[i] == false ? 255 : 0;
/* 95:   */   }
/* 96:   */   
/* 97:   */   public void setByte(int i, int p)
/* 98:   */   {
/* 99:91 */     this.pixels[i] = (boolean) (p > 0 ? 1 : false);
/* :0:   */   }
/* :1:   */   
/* :2:   */   public double getDouble(int i)
/* :3:   */   {
/* :4:95 */     return this.pixels[i] == false ? 1.0D : 0.0D;
/* :5:   */   }
/* :6:   */   
/* :7:   */   public void setDouble(int i, double p)
/* :8:   */   {
/* :9:99 */     this.pixels[i] = (boolean) (p > 0.0D ? 1 : false);
/* ;0:   */   }
/* ;1:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.BooleanImage
 * JD-Core Version:    0.7.0.1
 */