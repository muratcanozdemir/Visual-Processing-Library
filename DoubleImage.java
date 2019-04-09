/*   1:    */ package vpt;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ public class DoubleImage
/*   6:    */   extends Image
/*   7:    */ {
/*   8:    */   private double[] pixels;
/*   9:    */   public static final double byteToDouble = 0.00392156862745098D;
/*  10:    */   public static final double MAX_VALUE = 1.0D;
/*  11:    */   public static final double MIN_VALUE = 0.0D;
/*  12:    */   public static final double intToDouble = 2.328306437080797E-010D;
/*  13:    */   public static final double intToDoubleOffset = 0.5D;
/*  14:    */   
/*  15:    */   public DoubleImage(int xdim, int ydim)
/*  16:    */   {
/*  17: 34 */     this.xdim = xdim;
/*  18: 35 */     this.ydim = ydim;
/*  19: 36 */     this.cdim = 1;
/*  20:    */     
/*  21: 38 */     this.pixels = new double[xdim * ydim];
/*  22:    */   }
/*  23:    */   
/*  24:    */   public DoubleImage(int xdim, int ydim, int cdim)
/*  25:    */   {
/*  26: 42 */     this.xdim = xdim;
/*  27: 43 */     this.ydim = ydim;
/*  28: 44 */     this.cdim = cdim;
/*  29:    */     
/*  30: 46 */     this.pixels = new double[xdim * ydim * cdim];
/*  31:    */   }
/*  32:    */   
/*  33:    */   public DoubleImage(Image img, boolean deepCopy)
/*  34:    */   {
/*  35: 50 */     this.xdim = img.getXDim();
/*  36: 51 */     this.ydim = img.getYDim();
/*  37: 52 */     this.cdim = img.getCDim();
/*  38:    */     
/*  39: 54 */     this.pixels = new double[this.xdim * this.ydim * this.cdim];
/*  40: 56 */     if (deepCopy) {
/*  41: 57 */       for (int i = 0; i < this.pixels.length; i++) {
/*  42: 58 */         setDouble(i, img.getDouble(i));
/*  43:    */       }
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public DoubleImage(double[][] pixels)
/*  48:    */   {
/*  49: 63 */     this.xdim = pixels[0].length;
/*  50: 64 */     this.ydim = pixels.length;
/*  51: 65 */     this.cdim = 1;
/*  52:    */     
/*  53: 67 */     this.pixels = new double[this.xdim * this.ydim];
/*  54: 69 */     for (int x = 0; x < this.xdim; x++) {
/*  55: 70 */       for (int y = 0; y < this.ydim; y++) {
/*  56: 71 */         setXYDouble(x, y, pixels[y][x]);
/*  57:    */       }
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public int size()
/*  62:    */   {
/*  63: 77 */     return this.pixels.length;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Image newInstance(boolean deepCopy)
/*  67:    */   {
/*  68: 82 */     return new DoubleImage(this, deepCopy);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Image newInstance(int xdim, int ydim, int cdim)
/*  72:    */   {
/*  73: 87 */     return new DoubleImage(xdim, ydim, cdim);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void fill(double p)
/*  77:    */   {
/*  78: 91 */     Arrays.fill(this.pixels, p);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void fill(int p)
/*  82:    */   {
/*  83: 95 */     Arrays.fill(this.pixels, 0.00392156862745098D * p);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void fill(boolean p)
/*  87:    */   {
/*  88: 99 */     Arrays.fill(this.pixels, p ? 1.0D : 0.0D);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public boolean getBoolean(int i)
/*  92:    */   {
/*  93:103 */     return this.pixels[i] > 0.0D;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setBoolean(int i, boolean p)
/*  97:    */   {
/*  98:107 */     this.pixels[i] = (p ? 1.0D : 0.0D);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public int getInt(int i)
/* 102:    */   {
/* 103:111 */     return (int)(4294967295.0D * (this.pixels[i] - 0.5D));
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setInt(int i, int p)
/* 107:    */   {
/* 108:115 */     this.pixels[i] = (2.328306437080797E-010D * p + 0.5D);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int getByte(int i)
/* 112:    */   {
/* 113:119 */     return (int)Math.round(255.0D * this.pixels[i]);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void setByte(int i, int p)
/* 117:    */   {
/* 118:123 */     this.pixels[i] = (0.00392156862745098D * p);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public double getDouble(int i)
/* 122:    */   {
/* 123:127 */     return this.pixels[i];
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setDouble(int i, double p)
/* 127:    */   {
/* 128:131 */     this.pixels[i] = p;
/* 129:    */   }
/* 130:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.DoubleImage
 * JD-Core Version:    0.7.0.1
 */