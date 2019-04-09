/*   1:    */ package vpt;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ 
/*   6:    */ public class IntegerImage
/*   7:    */   extends Image
/*   8:    */ {
/*   9:    */   private int[] pixels;
/*  10:    */   public static final int MAX_VALUE = 2147483647;
/*  11:    */   public static final int MIN_VALUE = -2147483648;
/*  12:    */   public static final double doubleToInt = 4294967295.0D;
/*  13:    */   
/*  14:    */   public IntegerImage(int xdim, int ydim)
/*  15:    */   {
/*  16: 21 */     this.xdim = xdim;
/*  17: 22 */     this.ydim = ydim;
/*  18: 23 */     this.cdim = 1;
/*  19:    */     
/*  20: 25 */     this.pixels = new int[xdim * ydim];
/*  21:    */   }
/*  22:    */   
/*  23:    */   public int[] getPixels()
/*  24:    */   {
/*  25: 29 */     return this.pixels;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public IntegerImage(int xdim, int ydim, int cdim)
/*  29:    */   {
/*  30: 33 */     this.xdim = xdim;
/*  31: 34 */     this.ydim = ydim;
/*  32: 35 */     this.cdim = cdim;
/*  33:    */     
/*  34: 37 */     this.pixels = new int[xdim * ydim * cdim];
/*  35:    */   }
/*  36:    */   
/*  37:    */   public IntegerImage(Image img, boolean deepCopy)
/*  38:    */   {
/*  39: 41 */     this.xdim = img.getXDim();
/*  40: 42 */     this.ydim = img.getYDim();
/*  41: 43 */     this.cdim = img.getCDim();
/*  42:    */     
/*  43: 45 */     this.pixels = new int[this.xdim * this.ydim * this.cdim];
/*  44: 47 */     if (deepCopy) {
/*  45: 48 */       for (int i = 0; i < this.pixels.length; i++) {
/*  46: 49 */         setInt(i, img.getInt(i));
/*  47:    */       }
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public Image newInstance(boolean deepCopy)
/*  52:    */   {
/*  53: 55 */     return new IntegerImage(this, deepCopy);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Image newInstance(int xdim, int ydim, int cdim)
/*  57:    */   {
/*  58: 60 */     return new IntegerImage(xdim, ydim, cdim);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void fill(double p)
/*  62:    */   {
/*  63: 65 */     System.err.println("Not yet implemented!!!");
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void fill(int p)
/*  67:    */   {
/*  68: 69 */     Arrays.fill(this.pixels, p);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void fill(boolean p)
/*  72:    */   {
/*  73: 73 */     Arrays.fill(this.pixels, p ? 2147483647 : -2147483648);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean getBoolean(int i)
/*  77:    */   {
/*  78: 77 */     return this.pixels[i] > -2147483648;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setBoolean(int i, boolean p)
/*  82:    */   {
/*  83: 81 */     this.pixels[i] = (p ? 2147483647 : -2147483648);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int getByte(int i)
/*  87:    */   {
/*  88: 85 */     return (this.pixels[i] >> 24) - -128;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setByte(int i, int p)
/*  92:    */   {
/*  93: 89 */     this.pixels[i] = (p + -128 << 24);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public int getInt(int i)
/*  97:    */   {
/*  98: 93 */     return this.pixels[i];
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setInt(int i, int p)
/* 102:    */   {
/* 103: 97 */     this.pixels[i] = p;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public double getDouble(int i)
/* 107:    */   {
/* 108:101 */     return this.pixels[i] * 2.328306437080797E-010D + 0.5D;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setDouble(int i, double p)
/* 112:    */   {
/* 113:105 */     this.pixels[i] = ((int)((p - 0.5D) * 4294967295.0D));
/* 114:    */   }
/* 115:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.IntegerImage
 * JD-Core Version:    0.7.0.1
 */