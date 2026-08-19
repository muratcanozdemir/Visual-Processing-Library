/*   1:    */ package vpt.algorithms.linear;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.util.Tools;
/*   9:    */ import vpt.util.se.NonFlatSE;
/*  10:    */ 
/*  11:    */ public class Convolution
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14: 21 */   public Image output = null;
/*  15: 22 */   public Image input = null;
/*  16: 23 */   public NonFlatSE se = null;
/*  17:    */   private int xdim;
/*  18:    */   private int ydim;
/*  19:    */   private int cdim;
/*  20:    */   
/*  21:    */   public Convolution()
/*  22:    */   {
/*  23: 30 */     this.inputFields = "input,se";
/*  24: 31 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 37 */     this.output = new DoubleImage(this.input, false);
/*  31:    */     
/*  32: 39 */     this.xdim = this.output.getXDim();
/*  33: 40 */     this.ydim = this.output.getYDim();
/*  34: 41 */     this.cdim = this.output.getCDim();
/*  35:    */     
/*  36:    */ 
/*  37: 44 */     NonFlatSE[] subKernels = this.se.decompose();
/*  38:    */     
/*  39:    */ 
/*  40: 47 */     subKernels = null;
/*  41: 49 */     if (subKernels == null)
/*  42:    */     {
/*  43: 51 */       this.se = this.se.reflection();
/*  44: 53 */       for (int x = 0; x < this.xdim; x++) {
/*  45: 54 */         for (int y = 0; y < this.ydim; y++) {
/*  46: 55 */           for (int c = 0; c < this.cdim; c++) {
/*  47: 56 */             this.output.setXYCDouble(x, y, c, getConvolved(this.input, x, y, c, this.se.getCoords()));
/*  48:    */           }
/*  49:    */         }
/*  50:    */       }
/*  51:    */     }
/*  52:    */     else
/*  53:    */     {
/*  54: 59 */       Image tmp = new DoubleImage(this.input, false);
/*  55:    */       
/*  56: 61 */       subKernels[0] = subKernels[0].reflection();
/*  57: 63 */       for (int x = 0; x < this.xdim; x++) {
/*  58: 64 */         for (int y = 0; y < this.ydim; y++) {
/*  59: 65 */           for (int c = 0; c < this.cdim; c++) {
/*  60: 66 */             tmp.setXYCDouble(x, y, c, getConvolved(this.input, x, y, c, subKernels[0].getCoords()));
/*  61:    */           }
/*  62:    */         }
/*  63:    */       }
/*  64: 68 */       subKernels[1] = subKernels[1].reflection();
/*  65: 70 */       for (int x = 0; x < this.xdim; x++) {
/*  66: 71 */         for (int y = 0; y < this.ydim; y++) {
/*  67: 72 */           for (int c = 0; c < this.cdim; c++) {
/*  68: 73 */             this.output.setXYCDouble(x, y, c, getConvolved(tmp, x, y, c, subKernels[1].getCoords()));
/*  69:    */           }
/*  70:    */         }
/*  71:    */       }
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   private double getConvolved(Image img, int x, int y, int c, Point[] coords)
/*  76:    */   {
/*  77: 81 */     double output = 0.0D;
/*  78: 82 */     double coeffSum = 0.0D;
/*  79: 84 */     for (int i = 0; i < coords.length; i++)
/*  80:    */     {
/*  81: 87 */       int _x = x - coords[i].x;
/*  82: 88 */       int _y = y - coords[i].y;
/*  83: 91 */       if ((_x < 0) || (_y < 0) || (_x >= this.xdim) || (_y >= this.ydim))
/*  84:    */       {
/*  85: 92 */         _x = Math.abs(_x % this.xdim);
/*  86: 93 */         _y = Math.abs(_y % this.ydim);
/*  87:    */       }
/*  88: 96 */       double input = img.getXYCDouble(_x, _y, c);
/*  89: 97 */       double kernel = this.se.getXYDouble(coords[i].x + this.se.center.x, coords[i].y + this.se.center.y);
/*  90:    */       
/*  91: 99 */       coeffSum += kernel;
/*  92:    */       
/*  93:101 */       output += input * kernel;
/*  94:    */     }
/*  95:104 */     if (Tools.cmpr(coeffSum, 0.0D) != 0) {
/*  96:105 */       return output / coeffSum;
/*  97:    */     }
/*  98:107 */     return output;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public static Image invoke(Image image, NonFlatSE se)
/* 102:    */   {
/* 103:    */     try
/* 104:    */     {
/* 105:113 */       return (Image)new Convolution().preprocess(new Object[] { image, se });
/* 106:    */     }
/* 107:    */     catch (GlobalException e)
/* 108:    */     {
/* 109:115 */       e.printStackTrace();
/* 110:    */     }
/* 111:116 */     return null;
/* 112:    */   }
/* 113:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.Convolution
 * JD-Core Version:    0.7.0.1
 */