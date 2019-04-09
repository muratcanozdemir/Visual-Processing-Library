/*   1:    */ package vpt.algorithms.conversion;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.DoubleImage;
/*   6:    */ import vpt.GlobalException;
/*   7:    */ import vpt.Image;
/*   8:    */ 
/*   9:    */ public class RGB2NUHSY733
/*  10:    */   extends Algorithm
/*  11:    */ {
/*  12:    */   public Image input;
/*  13:    */   public Image output;
/*  14:    */   
/*  15:    */   public RGB2NUHSY733()
/*  16:    */   {
/*  17: 21 */     this.inputFields = "input";
/*  18: 22 */     this.outputFields = "output";
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void execute()
/*  22:    */     throws GlobalException
/*  23:    */   {
/*  24: 27 */     int xdim = this.input.getXDim();
/*  25: 28 */     int ydim = this.input.getYDim();
/*  26: 29 */     int cdim = this.input.getCDim();
/*  27: 31 */     if (cdim != 3) {
/*  28: 32 */       throw new GlobalException("And why exactly isn't the input image in color?");
/*  29:    */     }
/*  30: 34 */     this.output = new DoubleImage(this.input, false);
/*  31:    */     
/*  32: 36 */     double coeff = 360.0D;
/*  33: 38 */     for (int x = 0; x < xdim; x++) {
/*  34: 39 */       for (int y = 0; y < ydim; y++)
/*  35:    */       {
/*  36: 40 */         double r = this.input.getXYCDouble(x, y, 0);
/*  37: 41 */         double g = this.input.getXYCDouble(x, y, 1);
/*  38: 42 */         double b = this.input.getXYCDouble(x, y, 2);
/*  39:    */         
/*  40: 44 */         double[] hsy = convert(r, g, b);
/*  41:    */         
/*  42: 46 */         hsy[1] = ((int)Math.floor(255.0D * hsy[1] / 86.0D));
/*  43: 47 */         hsy[2] = ((int)Math.floor(255.0D * hsy[2] / 86.0D));
/*  44:    */         
/*  45:    */ 
/*  46:    */ 
/*  47: 51 */         int hue = (int)Math.floor(hsy[0] * 360.0D);
/*  48: 54 */         if ((hue >= 330) || (hue <= 22)) {
/*  49: 54 */           hue = 0;
/*  50: 57 */         } else if ((hue >= 22) && (hue <= 45)) {
/*  51: 57 */           hue = 1;
/*  52: 60 */         } else if ((hue >= 45) && (hue <= 70)) {
/*  53: 60 */           hue = 2;
/*  54: 63 */         } else if ((hue >= 70) && (hue <= 155)) {
/*  55: 63 */           hue = 3;
/*  56: 66 */         } else if ((hue >= 155) && (hue <= 186)) {
/*  57: 66 */           hue = 4;
/*  58: 69 */         } else if ((hue >= 186) && (hue <= 278)) {
/*  59: 69 */           hue = 5;
/*  60: 72 */         } else if ((hue >= 278) && (hue <= 330)) {
/*  61: 72 */           hue = 6;
/*  62:    */         } else {
/*  63: 75 */           System.err.println("sorun var " + hue);
/*  64:    */         }
/*  65: 77 */         hsy[0] = hue;
/*  66:    */         
/*  67: 79 */         this.output.setXYCByte(x, y, 0, (int)hsy[0]);
/*  68: 80 */         this.output.setXYCByte(x, y, 1, (int)hsy[1]);
/*  69: 81 */         this.output.setXYCByte(x, y, 2, (int)hsy[2]);
/*  70:    */       }
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static double[] convert(double r, double g, double b)
/*  75:    */   {
/*  76: 95 */     double[] hsy = new double[3]; double 
/*  77:    */     
/*  78: 97 */       tmp17_16 = (hsy[2] = 0.0D);hsy[1] = tmp17_16;hsy[0] = tmp17_16;
/*  79:    */     
/*  80:    */ 
/*  81:    */ 
/*  82:101 */     hsy[2] = (0.299D * r + 0.587D * g + 0.114D * b);
/*  83:    */     
/*  84:    */ 
/*  85:104 */     double C1 = r - 0.5D * g - 0.5D * b;
/*  86:105 */     double C2 = -Math.sqrt(3.0D) / 2.0D * g + Math.sqrt(3.0D) / 2.0D * b;
/*  87:    */     
/*  88:    */ 
/*  89:108 */     double C = Math.sqrt(C1 * C1 + C2 * C2);
/*  90:111 */     if ((C <= 0.0D) && (C >= 0.0D)) {
/*  91:112 */       hsy[0] = 0.0D;
/*  92:113 */     } else if ((C != 0.0D) && (C2 <= 0.0D)) {
/*  93:114 */       hsy[0] = Math.acos(C1 / C);
/*  94:115 */     } else if ((C != 0.0D) && (C2 > 0.0D)) {
/*  95:116 */       hsy[0] = (6.283185307179586D - Math.acos(C1 / C));
/*  96:    */     }
/*  97:119 */     hsy[1] = (Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b)));
/*  98:    */     
/*  99:    */ 
/* 100:122 */     hsy[0] /= 6.283185307179586D;
/* 101:    */     
/* 102:124 */     return hsy;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Image invoke(Image image)
/* 106:    */   {
/* 107:    */     try
/* 108:    */     {
/* 109:137 */       return (Image)new RGB2NUHSY733().preprocess(new Object[] { image });
/* 110:    */     }
/* 111:    */     catch (GlobalException e)
/* 112:    */     {
/* 113:139 */       e.printStackTrace();
/* 114:    */     }
/* 115:140 */     return null;
/* 116:    */   }
/* 117:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.RGB2NUHSY733
 * JD-Core Version:    0.7.0.1
 */