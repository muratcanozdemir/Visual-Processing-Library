/*   1:    */ package vpt;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ public class ByteImage
/*   6:    */   extends Image
/*   7:    */ {
/*   8:    */   private byte[] pixels;
/*   9:    */   public static final double doubleToByte = 255.0D;
/*  10:    */   public static final int MAX_VALUE = 255;
/*  11:    */   public static final int MIN_VALUE = 0;
/*  12:    */   
/*  13:    */   public ByteImage(int xdim, int ydim)
/*  14:    */   {
/*  15: 26 */     this.xdim = xdim;
/*  16: 27 */     this.ydim = ydim;
/*  17: 28 */     this.cdim = 1;
/*  18:    */     
/*  19: 30 */     this.pixels = new byte[xdim * ydim];
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ByteImage(int xdim, int ydim, int cdim)
/*  23:    */   {
/*  24: 34 */     this.xdim = xdim;
/*  25: 35 */     this.ydim = ydim;
/*  26: 36 */     this.cdim = cdim;
/*  27:    */     
/*  28: 38 */     this.pixels = new byte[xdim * ydim * cdim];
/*  29:    */   }
/*  30:    */   
/*  31:    */   public ByteImage(Image img, boolean deepCopy)
/*  32:    */   {
/*  33: 42 */     this.xdim = img.getXDim();
/*  34: 43 */     this.ydim = img.getYDim();
/*  35: 44 */     this.cdim = img.getCDim();
/*  36:    */     
/*  37: 46 */     this.pixels = new byte[this.xdim * this.ydim * this.cdim];
/*  38: 48 */     if (deepCopy) {
/*  39: 49 */       for (int i = 0; i < this.pixels.length; i++) {
/*  40: 50 */         setByte(i, img.getByte(i));
/*  41:    */       }
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Image newInstance(boolean deepCopy)
/*  46:    */   {
/*  47: 56 */     return new ByteImage(this, deepCopy);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Image newInstance(int xdim, int ydim, int cdim)
/*  51:    */   {
/*  52: 61 */     return new ByteImage(xdim, ydim, cdim);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void fill(double p)
/*  56:    */   {
/*  57: 65 */     Arrays.fill(this.pixels, (byte)(int)Math.round(255.0D * p + -128.0D));
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void fill(int p)
/*  61:    */   {
/*  62: 69 */     Arrays.fill(this.pixels, (byte)(p + -128));
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void fill(boolean p)
/*  66:    */   {
/*  67: 73 */     Arrays.fill(this.pixels, (byte)(p ? 127 : -128));
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean getBoolean(int i)
/*  71:    */   {
/*  72: 77 */     return this.pixels[i] > -128;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setBoolean(int i, boolean p)
/*  76:    */   {
/*  77: 81 */     this.pixels[i] = (byte) (p ? 127 : -128);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public int getByte(int i)
/*  81:    */   {
/*  82: 85 */     return this.pixels[i] - -128;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setByte(int i, int p)
/*  86:    */   {
/*  87: 89 */     this.pixels[i] = ((byte)(p + -128));
/*  88:    */   }
/*  89:    */   
/*  90:    */   public int getInt(int i)
/*  91:    */   {
/*  92: 93 */     return this.pixels[i] << 24;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setInt(int i, int p)
/*  96:    */   {
/*  97: 97 */     this.pixels[i] = ((byte)(p >> 24));
/*  98:    */   }
/*  99:    */   
/* 100:    */   public double getDouble(int i)
/* 101:    */   {
/* 102:101 */     return 0.00392156862745098D * (this.pixels[i] - -128);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setDouble(int i, double p)
/* 106:    */   {
/* 107:105 */     this.pixels[i] = ((byte)(int)Math.round(255.0D * p + -128.0D));
/* 108:    */   }
/* 109:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.ByteImage
 * JD-Core Version:    0.7.0.1
 */