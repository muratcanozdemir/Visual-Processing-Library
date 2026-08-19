/*   1:    */ package vpt.algorithms.texture;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.util.se.FlatSE;
/*   9:    */ 
/*  10:    */ public class OrientationMap
/*  11:    */   extends Algorithm
/*  12:    */ {
/*  13: 20 */   public Image output = null;
/*  14: 21 */   public Image input = null;
/*  15: 22 */   public Integer maskSize = null;
/*  16:    */   private int xdim;
/*  17:    */   private int ydim;
/*  18: 27 */   private double max = -1.797693134862316E+308D;
/*  19: 28 */   private double min = 1.7976931348623157E+308D;
/*  20:    */   
/*  21:    */   public OrientationMap()
/*  22:    */   {
/*  23: 31 */     this.inputFields = "input, maskSize";
/*  24: 32 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 36 */     if (this.input.getCDim() != 1) {
/*  31: 36 */       throw new GlobalException("The input image must be grayscale");
/*  32:    */     }
/*  33: 37 */     if (this.maskSize.intValue() < 3) {
/*  34: 37 */       throw new GlobalException("Invalid mask size");
/*  35:    */     }
/*  36: 39 */     this.xdim = this.input.getXDim();
/*  37: 40 */     this.ydim = this.input.getYDim();
/*  38:    */     
/*  39: 42 */     Image theta = new DoubleImage(this.input, false);
/*  40: 43 */     theta.fill(0.0D);
/*  41:    */     
/*  42: 45 */     Image mod = new DoubleImage(this.input, false);
/*  43: 46 */     mod.fill(0.0D);
/*  44: 48 */     for (int y = 1; y < this.ydim - 1; y++) {
/*  45: 49 */       for (int x = 1; x < this.xdim - 1; x++)
/*  46:    */       {
/*  47: 51 */         double dx = this.input.getXYDouble(x - 1, y) - this.input.getXYDouble(x + 1, y);
/*  48: 52 */         double dy = this.input.getXYDouble(x, y - 1) - this.input.getXYDouble(x, y + 1);
/*  49:    */         
/*  50: 54 */         theta.setXYDouble(x, y, Math.atan2(dy, dx));
/*  51:    */         
/*  52: 56 */         double m = Math.sqrt(dx * dx + dy * dy);
/*  53:    */         
/*  54: 58 */         mod.setXYDouble(x, y, m);
/*  55: 60 */         if (m > this.max) {
/*  56: 60 */           this.max = m;
/*  57:    */         }
/*  58: 61 */         if (m < this.min) {
/*  59: 61 */           this.min = m;
/*  60:    */         }
/*  61:    */       }
/*  62:    */     }
/*  63: 65 */     this.output = new DoubleImage(this.input, false);
/*  64: 66 */     this.output.fill(0.0D);
/*  65:    */     
/*  66: 68 */     FlatSE se = FlatSE.square(this.maskSize.intValue());
/*  67: 70 */     for (int y = 1; y < this.ydim - 1; y++) {
/*  68: 71 */       for (int x = 1; x < this.xdim - 1; x++)
/*  69:    */       {
/*  70: 73 */         double d = getDominantOrientation(theta, mod, x, y, se.getCoords());
/*  71: 74 */         this.output.setXYDouble(x, y, d);
/*  72:    */       }
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   private double getDominantOrientation(Image theta, Image modulus, int x, int y, Point[] coords)
/*  77:    */   {
/*  78: 83 */     double tmp1 = 0.0D;double tmp2 = 0.0D;
/*  79: 85 */     for (int i = 0; i < coords.length; i++)
/*  80:    */     {
/*  81: 87 */       int _x = x - coords[i].x;
/*  82: 88 */       int _y = y - coords[i].y;
/*  83: 90 */       if ((_x >= 0) && (_y >= 0) && (_x < this.xdim) && (_y < this.ydim))
/*  84:    */       {
/*  85: 93 */         double t = theta.getXYDouble(_x, _y) + 3.141592653589793D;
/*  86:    */         
/*  87: 95 */         double m = modulus.getXYDouble(_x, _y);
/*  88:    */         
/*  89: 97 */         tmp1 += m * m * Math.sin(t * 2.0D);
/*  90: 98 */         tmp2 += m * m * Math.cos(t * 2.0D);
/*  91:    */       }
/*  92:    */     }
/*  93:102 */     double result = (Math.atan2(tmp1, tmp2) + 3.141592653589793D) / 6.283185307179586D;
/*  94:    */     
/*  95:104 */     return result;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static Image invoke(Image image, Integer maskSize)
/*  99:    */   {
/* 100:    */     try
/* 101:    */     {
/* 102:110 */       return (Image)new OrientationMap().preprocess(new Object[] { image, maskSize });
/* 103:    */     }
/* 104:    */     catch (GlobalException e)
/* 105:    */     {
/* 106:112 */       e.printStackTrace();
/* 107:    */     }
/* 108:113 */     return null;
/* 109:    */   }
/* 110:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.texture.OrientationMap
 * JD-Core Version:    0.7.0.1
 */