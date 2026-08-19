/*   1:    */ package vpt.algorithms.mm.multi;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.arithmetic.Equal;
/*   8:    */ import vpt.util.Tools;
/*   9:    */ import vpt.util.ordering.AlgebraicOrdering;
/*  10:    */ import vpt.util.se.FlatSE;
/*  11:    */ 
/*  12:    */ public class MLambdaLeveling
/*  13:    */   extends Algorithm
/*  14:    */ {
/*  15: 21 */   public Image output = null;
/*  16: 23 */   public Integer lambda = null;
/*  17: 24 */   public Image marker = null;
/*  18: 25 */   public Image input = null;
/*  19: 26 */   public FlatSE se = null;
/*  20: 27 */   public AlgebraicOrdering or = null;
/*  21:    */   private int xdim;
/*  22:    */   private int ydim;
/*  23: 31 */   private double[][] pixelArray = null;
/*  24:    */   
/*  25:    */   public MLambdaLeveling()
/*  26:    */   {
/*  27: 34 */     this.inputFields = "input,marker,se,or,lambda";
/*  28: 35 */     this.outputFields = "output";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void execute()
/*  32:    */     throws GlobalException
/*  33:    */   {
/*  34: 40 */     this.xdim = this.input.getXDim();
/*  35: 41 */     this.ydim = this.input.getYDim();
/*  36: 43 */     if ((this.lambda.intValue() < 0) || (this.lambda.intValue() > 254)) {
/*  37: 44 */       throw new GlobalException("The lambda value must be \\in [0,254]");
/*  38:    */     }
/*  39: 47 */     Image tmp = null;
/*  40: 48 */     this.pixelArray = new double[this.se.getCoords().length][];
/*  41:    */     do
/*  42:    */     {
/*  43: 51 */       tmp = this.marker;
/*  44: 52 */       this.marker = level(this.marker, this.input);
/*  45: 53 */     } while (!Equal.invoke(this.marker, tmp).booleanValue());
/*  46: 55 */     this.output = this.marker;
/*  47:    */   }
/*  48:    */   
/*  49:    */   private Image level(Image marker, Image input)
/*  50:    */   {
/*  51: 60 */     Image tmp = marker.newInstance(true);
/*  52: 62 */     for (int x = 0; x < this.xdim; x++) {
/*  53: 63 */       for (int y = 0; y < this.ydim; y++)
/*  54:    */       {
/*  55: 64 */         double[] m = marker.getVXYDouble(x, y);
/*  56: 65 */         double[] i = input.getVXYDouble(x, y);
/*  57: 68 */         if (this.or.compare(i, Tools.VAddC(m, this.lambda.intValue() * 0.00392156862745098D)) > 0) {
/*  58: 69 */           tmp.setVXYDouble(x, y, this.or.min(i, getMax(marker, x, y, this.se.getCoords())));
/*  59: 72 */         } else if (this.or.compare(m, Tools.VAddC(i, this.lambda.intValue() * 0.00392156862745098D)) > 0) {
/*  60: 73 */           tmp.setVXYDouble(x, y, this.or.max(i, getMin(marker, x, y, this.se.getCoords())));
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64: 81 */     return tmp;
/*  65:    */   }
/*  66:    */   
/*  67:    */   private double[] getMin(Image img, int x, int y, Point[] coords)
/*  68:    */   {
/*  69: 86 */     int index = 0;
/*  70: 88 */     for (int i = 0; i < coords.length; i++)
/*  71:    */     {
/*  72: 90 */       int _x = x + coords[i].x;
/*  73: 91 */       int _y = y + coords[i].y;
/*  74: 93 */       if (_x < 0) {
/*  75: 93 */         _x = 0;
/*  76:    */       }
/*  77: 94 */       if (_y < 0) {
/*  78: 94 */         _y = 0;
/*  79:    */       }
/*  80: 95 */       if (_x >= this.xdim) {
/*  81: 95 */         _x = this.xdim - 1;
/*  82:    */       }
/*  83: 96 */       if (_y >= this.ydim) {
/*  84: 96 */         _y = this.ydim - 1;
/*  85:    */       }
/*  86: 98 */       this.pixelArray[(index++)] = img.getVXYDouble(_x, _y);
/*  87:    */     }
/*  88:102 */     return index > 0 ? this.or.min(this.pixelArray) : img.getVXYDouble(x, y);
/*  89:    */   }
/*  90:    */   
/*  91:    */   private double[] getMax(Image img, int x, int y, Point[] coords)
/*  92:    */   {
/*  93:106 */     int index = 0;
/*  94:108 */     for (int i = 0; i < coords.length; i++)
/*  95:    */     {
/*  96:111 */       int _x = x - coords[i].x;
/*  97:112 */       int _y = y - coords[i].y;
/*  98:114 */       if (_x < 0) {
/*  99:114 */         _x = 0;
/* 100:    */       }
/* 101:115 */       if (_y < 0) {
/* 102:115 */         _y = 0;
/* 103:    */       }
/* 104:116 */       if (_x >= this.xdim) {
/* 105:116 */         _x = this.xdim - 1;
/* 106:    */       }
/* 107:117 */       if (_y >= this.ydim) {
/* 108:117 */         _y = this.ydim - 1;
/* 109:    */       }
/* 110:119 */       this.pixelArray[(index++)] = img.getVXYDouble(_x, _y);
/* 111:    */     }
/* 112:123 */     return index > 0 ? this.or.max(this.pixelArray) : img.getVXYDouble(x, y);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static Image invoke(Image image, Image marker, FlatSE se, AlgebraicOrdering or, Integer lambda)
/* 116:    */   {
/* 117:    */     try
/* 118:    */     {
/* 119:129 */       return (Image)new MLambdaLeveling().preprocess(new Object[] { image, marker, se, or, lambda });
/* 120:    */     }
/* 121:    */     catch (GlobalException e)
/* 122:    */     {
/* 123:131 */       e.printStackTrace();
/* 124:    */     }
/* 125:132 */     return null;
/* 126:    */   }
/* 127:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MLambdaLeveling
 * JD-Core Version:    0.7.0.1
 */