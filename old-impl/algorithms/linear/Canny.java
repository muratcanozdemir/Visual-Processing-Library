/*   1:    */ package vpt.algorithms.linear;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ import vpt.algorithms.display.Display2D;
/*   9:    */ import vpt.algorithms.histogram.ContrastStretch;
/*  10:    */ import vpt.algorithms.segmentation.HysteresisThreshold;
/*  11:    */ import vpt.util.se.NonFlatSE;
/*  12:    */ 
/*  13:    */ public class Canny
/*  14:    */   extends Algorithm
/*  15:    */ {
/*  16: 15 */   public Image output = null;
/*  17: 16 */   public Image input = null;
/*  18: 17 */   public Double lowThreshold = null;
/*  19: 18 */   public Double highThreshold = null;
/*  20:    */   
/*  21:    */   public Canny()
/*  22:    */   {
/*  23: 21 */     this.inputFields = "input,lowThreshold,highThreshold";
/*  24: 22 */     this.outputFields = "output";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void execute()
/*  28:    */     throws GlobalException
/*  29:    */   {
/*  30: 26 */     this.lowThreshold = Double.valueOf(Math.max(0.0D, Math.min(this.lowThreshold.doubleValue(), 1.0D)));
/*  31: 27 */     this.highThreshold = Double.valueOf(Math.max(0.0D, Math.min(this.highThreshold.doubleValue(), 1.0D)));
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38: 34 */     NonFlatSE kernel = new NonFlatSE(new Point(2, 2), new double[][] { { 2.0D, 4.0D, 5.0D, 4.0D, 2.0D }, { 4.0D, 9.0D, 12.0D, 9.0D, 4.0D }, { 5.0D, 12.0D, 15.0D, 12.0D, 5.0D }, { 4.0D, 9.0D, 12.0D, 9.0D, 4.0D }, { 2.0D, 4.0D, 5.0D, 4.0D, 2.0D } });
/*  39: 35 */     Image tmp = Convolution.invoke(this.input, kernel);
/*  40:    */     
/*  41:    */ 
/*  42: 38 */     Image gradient = Sobel.invoke(tmp, Integer.valueOf(2), Boolean.valueOf(true));
/*  43: 39 */     Display2D.invoke(gradient, "", Boolean.valueOf(false));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47: 43 */     Image theta = Sobel.invoke(tmp, Integer.valueOf(3), Boolean.valueOf(false));
/*  48:    */     
/*  49: 45 */     this.output = new DoubleImage(gradient, true);
/*  50: 50 */     for (int c = 0; c < theta.getCDim(); c++) {
/*  51: 51 */       for (int x = 1; x < theta.getXDim() - 1; x++) {
/*  52: 52 */         for (int y = 1; y < theta.getYDim() - 1; y++)
/*  53:    */         {
/*  54: 54 */           double angle = theta.getXYCDouble(x, y, c);
/*  55: 56 */           if (angle < 0.0D) {
/*  56: 56 */             angle += 3.141592653589793D;
/*  57:    */           }
/*  58: 58 */           if (((angle >= 0.0D) && (angle < 0.3926990816987241D)) || ((angle >= 2.748893571891069D) && (angle <= 3.141592653589793D))) {
/*  59: 60 */             theta.setXYCDouble(x, y, c, 0.0D);
/*  60: 62 */           } else if ((angle >= 0.3926990816987241D) && (angle < 1.178097245096172D)) {
/*  61: 64 */             theta.setXYCDouble(x, y, c, 0.25D);
/*  62: 66 */           } else if ((angle >= 1.178097245096172D) && (angle < 1.963495408493621D)) {
/*  63: 68 */             theta.setXYCDouble(x, y, c, 0.5D);
/*  64: 70 */           } else if ((angle >= 1.963495408493621D) && (angle < 2.748893571891069D)) {
/*  65: 72 */             theta.setXYCDouble(x, y, c, 0.75D);
/*  66:    */           }
/*  67:    */         }
/*  68:    */       }
/*  69:    */     }
/*  70: 81 */     for (int c = 0; c < this.input.getCDim(); c++)
/*  71:    */     {
/*  72: 82 */       for (int x = 1; x < this.input.getXDim() - 1; x++) {
/*  73: 83 */         for (int y = 1; y < this.input.getYDim() - 1; y++)
/*  74:    */         {
/*  75: 84 */           double angle = theta.getXYCDouble(x, y, c);
/*  76: 85 */           double grad = gradient.getXYCDouble(x, y, c);
/*  77: 89 */           if (angle == 0.0D)
/*  78:    */           {
/*  79: 90 */             double west = gradient.getXYCDouble(x - 1, y, c);
/*  80: 91 */             double east = gradient.getXYCDouble(x + 1, y, c);
/*  81: 93 */             if ((grad < west) || (grad < east)) {
/*  82: 94 */               this.output.setXYCDouble(x, y, c, 0.0D);
/*  83:    */             }
/*  84:    */           }
/*  85: 96 */           else if (angle == 0.5D)
/*  86:    */           {
/*  87: 99 */             double north = gradient.getXYCDouble(x, y - 1, c);
/*  88:100 */             double south = gradient.getXYCDouble(x, y + 1, c);
/*  89:102 */             if ((grad < north) || (grad < south)) {
/*  90:103 */               this.output.setXYCDouble(x, y, c, 0.0D);
/*  91:    */             }
/*  92:    */           }
/*  93:105 */           else if (angle == 0.25D)
/*  94:    */           {
/*  95:108 */             double northEast = gradient.getXYCDouble(x + 1, y - 1, c);
/*  96:109 */             double southWest = gradient.getXYCDouble(x - 1, y + 1, c);
/*  97:111 */             if ((grad < northEast) || (grad < southWest)) {
/*  98:112 */               this.output.setXYCDouble(x, y, c, 0.0D);
/*  99:    */             }
/* 100:    */           }
/* 101:114 */           else if (angle == 0.75D)
/* 102:    */           {
/* 103:117 */             double northWest = gradient.getXYCDouble(x - 1, y - 1, c);
/* 104:118 */             double southEast = gradient.getXYCDouble(x + 1, y + 1, c);
/* 105:120 */             if ((grad < northWest) || (grad < southEast)) {
/* 106:121 */               this.output.setXYCDouble(x, y, c, 0.0D);
/* 107:    */             }
/* 108:    */           }
/* 109:    */           else
/* 110:    */           {
/* 111:124 */             this.output.setXYCDouble(x, y, c, 0.0D);
/* 112:    */           }
/* 113:    */         }
/* 114:    */       }
/* 115:131 */       for (int x = 0; x < this.output.getXDim(); x++) {
/* 116:132 */         this.output.setXYCDouble(x, 0, c, 0.0D);
/* 117:    */       }
/* 118:134 */       for (int x = 0; x < this.output.getXDim(); x++) {
/* 119:135 */         this.output.setXYCDouble(x, this.output.getYDim() - 1, c, 0.0D);
/* 120:    */       }
/* 121:137 */       for (int y = 0; y < this.output.getYDim(); y++) {
/* 122:138 */         this.output.setXYCDouble(0, y, c, 0.0D);
/* 123:    */       }
/* 124:140 */       for (int y = 0; y < this.output.getYDim(); y++) {
/* 125:141 */         this.output.setXYCDouble(0, this.output.getYDim() - 1, c, 0.0D);
/* 126:    */       }
/* 127:    */     }
/* 128:145 */     this.output = ContrastStretch.invoke(this.output);
/* 129:146 */     this.output = HysteresisThreshold.invoke(this.output, this.lowThreshold, this.highThreshold);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public static Image invoke(Image image, Double lowThreshold, Double highThreshold)
/* 133:    */   {
/* 134:    */     try
/* 135:    */     {
/* 136:152 */       return (Image)new Canny().preprocess(new Object[] { image, lowThreshold, highThreshold });
/* 137:    */     }
/* 138:    */     catch (GlobalException e)
/* 139:    */     {
/* 140:154 */       e.printStackTrace();
/* 141:    */     }
/* 142:155 */     return null;
/* 143:    */   }
/* 144:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.linear.Canny
 * JD-Core Version:    0.7.0.1
 */