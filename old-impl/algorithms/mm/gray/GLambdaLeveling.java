/*   1:    */ package vpt.algorithms.mm.gray;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.arithmetic.Equal;
/*   8:    */ import vpt.util.se.FlatSE;
/*   9:    */ 
/*  10:    */ public class GLambdaLeveling
/*  11:    */   extends Algorithm
/*  12:    */ {
/*  13: 19 */   public Image output = null;
/*  14: 21 */   public Integer lambda = null;
/*  15: 22 */   public Image marker = null;
/*  16: 23 */   public Image input = null;
/*  17: 24 */   public FlatSE se = null;
/*  18:    */   private int cdim;
/*  19:    */   private int xdim;
/*  20:    */   private int ydim;
/*  21:    */   
/*  22:    */   public GLambdaLeveling()
/*  23:    */   {
/*  24: 31 */     this.inputFields = "input,marker,se,lambda";
/*  25: 32 */     this.outputFields = "output";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 37 */     this.cdim = this.input.getCDim();
/*  32: 38 */     this.xdim = this.input.getXDim();
/*  33: 39 */     this.ydim = this.input.getYDim();
/*  34: 41 */     if ((this.lambda.intValue() < 0) || (this.lambda.intValue() > 254)) {
/*  35: 42 */       throw new GlobalException("The lambda value must be \\in [0,254]");
/*  36:    */     }
/*  37: 45 */     Image tmp = null;
/*  38:    */     do
/*  39:    */     {
/*  40: 48 */       tmp = this.marker;
/*  41: 49 */       this.marker = level(this.marker, this.input);
/*  42: 50 */     } while (!Equal.invoke(this.marker, tmp).booleanValue());
/*  43: 52 */     this.output = this.marker;
/*  44:    */   }
/*  45:    */   
/*  46:    */   private Image level(Image marker, Image input)
/*  47:    */   {
/*  48: 57 */     Image tmp = marker.newInstance(true);
/*  49: 59 */     for (int c = 0; c < this.cdim; c++) {
/*  50: 60 */       for (int x = 0; x < this.xdim; x++) {
/*  51: 61 */         for (int y = 0; y < this.ydim; y++)
/*  52:    */         {
/*  53: 62 */           int m = marker.getXYCByte(x, y, c);
/*  54: 63 */           int i = input.getXYCByte(x, y, c);
/*  55: 66 */           if (i > m + this.lambda.intValue()) {
/*  56: 67 */             tmp.setXYCByte(x, y, c, Math.min(i, getMax(marker, x, y, c, this.se.getCoords())));
/*  57: 70 */           } else if (m > i + this.lambda.intValue()) {
/*  58: 71 */             tmp.setXYCByte(x, y, c, Math.max(i, getMin(marker, x, y, c, this.se.getCoords())));
/*  59:    */           }
/*  60:    */         }
/*  61:    */       }
/*  62:    */     }
/*  63: 80 */     return tmp;
/*  64:    */   }
/*  65:    */   
/*  66:    */   private int getMin(Image img, int x, int y, int c, Point[] coords)
/*  67:    */   {
/*  68: 85 */     int min = 255;
/*  69: 87 */     for (int i = 0; i < coords.length; i++)
/*  70:    */     {
/*  71: 89 */       int _x = x - coords[i].x;
/*  72: 90 */       int _y = y - coords[i].y;
/*  73: 92 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/*  74:    */       {
/*  75: 95 */         int p = img.getXYCByte(_x, _y, c);
/*  76: 96 */         if (p < min) {
/*  77: 96 */           min = p;
/*  78:    */         }
/*  79:    */       }
/*  80:    */     }
/*  81:100 */     return min < 255 ? min : img.getXYCByte(x, y, c);
/*  82:    */   }
/*  83:    */   
/*  84:    */   private int getMax(Image img, int x, int y, int c, Point[] coords)
/*  85:    */   {
/*  86:105 */     int max = 0;
/*  87:107 */     for (int i = 0; i < coords.length; i++)
/*  88:    */     {
/*  89:109 */       int _x = x - coords[i].x;
/*  90:110 */       int _y = y - coords[i].y;
/*  91:112 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/*  92:    */       {
/*  93:115 */         int p = img.getXYCByte(_x, _y, c);
/*  94:116 */         if (p > max) {
/*  95:116 */           max = p;
/*  96:    */         }
/*  97:    */       }
/*  98:    */     }
/*  99:120 */     return max > 0 ? max : img.getXYCByte(x, y, c);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static Image invoke(Image image, Image marker, FlatSE se, Integer lambda)
/* 103:    */   {
/* 104:    */     try
/* 105:    */     {
/* 106:126 */       return (Image)new GLambdaLeveling().preprocess(new Object[] { image, marker, se, lambda });
/* 107:    */     }
/* 108:    */     catch (GlobalException e)
/* 109:    */     {
/* 110:128 */       e.printStackTrace();
/* 111:    */     }
/* 112:129 */     return null;
/* 113:    */   }
/* 114:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GLambdaLeveling
 * JD-Core Version:    0.7.0.1
 */