/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import vpt.Algorithm;
/*   6:    */ import vpt.BooleanImage;
/*   7:    */ import vpt.GlobalException;
/*   8:    */ import vpt.Image;
/*   9:    */ import vpt.algorithms.geometric.ImageCentroid;
/*  10:    */ import vpt.algorithms.mm.binary.BInternGradient;
/*  11:    */ import vpt.util.Distance;
/*  12:    */ import vpt.util.Tools;
/*  13:    */ import vpt.util.se.FlatSE;
/*  14:    */ 
/*  15:    */ public class FourierDescriptor
/*  16:    */   extends Algorithm
/*  17:    */ {
/*  18: 24 */   public double[] output = null;
/*  19: 25 */   public Image input = null;
/*  20: 26 */   public Integer coeffs = null;
/*  21:    */   
/*  22:    */   public FourierDescriptor()
/*  23:    */   {
/*  24: 29 */     this.inputFields = "input, coeffs";
/*  25: 30 */     this.outputFields = "output";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void execute()
/*  29:    */     throws GlobalException
/*  30:    */   {
/*  31: 34 */     int cdim = this.input.getCDim();
/*  32: 35 */     int xdim = this.input.getXDim();
/*  33: 36 */     int ydim = this.input.getYDim();
/*  34: 38 */     if (cdim != 1) {
/*  35: 38 */       throw new GlobalException("Sorry, only grayscale images for now...");
/*  36:    */     }
/*  37: 45 */     Point centroid = ImageCentroid.invoke(this.input);
/*  38:    */     
/*  39:    */ 
/*  40: 48 */     Image borderImage = BInternGradient.invoke(this.input, FlatSE.square(3));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44: 52 */     this.output = new double[(int)Tools.volume(borderImage, 0)];
/*  45: 53 */     int syc = 0;
/*  46:    */     
/*  47: 55 */     BooleanImage mask = new BooleanImage(this.input, false);
/*  48: 56 */     mask.fill(false);
/*  49: 58 */     for (int x = 0; x < xdim; x++) {
/*  50: 59 */       for (int y = 0; y < ydim; y++)
/*  51:    */       {
/*  52: 60 */         boolean r = borderImage.getXYBoolean(x, y);
/*  53: 61 */         if (r)
/*  54:    */         {
/*  55: 63 */           boolean m = mask.getXYBoolean(x, y);
/*  56: 64 */           if (!m)
/*  57:    */           {
/*  58: 67 */             Point p = new Point(x, y);
/*  59:    */             for (;;)
/*  60:    */             {
/*  61: 70 */               mask.setXYBoolean(p.x, p.y, true);
/*  62: 71 */               this.output[(syc++)] = Distance.EuclideanDistance(p.x, p.y, centroid.x, centroid.y);
/*  63: 74 */               if ((p.x + 1 < xdim) && (borderImage.getXYBoolean(p.x + 1, p.y)) && (!mask.getXYBoolean(p.x + 1, p.y)))
/*  64:    */               {
/*  65: 75 */                 p.x += 1;
/*  66:    */               }
/*  67: 76 */               else if ((p.y + 1 < ydim) && (borderImage.getXYBoolean(p.x, p.y + 1)) && (!mask.getXYBoolean(p.x, p.y + 1)))
/*  68:    */               {
/*  69: 77 */                 p.y += 1;
/*  70:    */               }
/*  71: 78 */               else if ((p.x - 1 >= 0) && (borderImage.getXYBoolean(p.x - 1, p.y)) && (!mask.getXYBoolean(p.x - 1, p.y)))
/*  72:    */               {
/*  73: 79 */                 p.x -= 1;
/*  74:    */               }
/*  75:    */               else
/*  76:    */               {
/*  77: 80 */                 if ((p.y - 1 < 0) || (!borderImage.getXYBoolean(p.x, p.y - 1)) || (mask.getXYBoolean(p.x, p.y - 1))) {
/*  78:    */                   break;
/*  79:    */                 }
/*  80: 81 */                 p.y -= 1;
/*  81:    */               }
/*  82:    */             }
/*  83:    */           }
/*  84:    */         }
/*  85:    */       }
/*  86:    */     }
/*  87: 90 */     this.output = Tools.dftMag1d(this.output);
/*  88: 91 */     if (this.output.length >= this.coeffs.intValue())
/*  89:    */     {
/*  90: 92 */       double[] tmp = new double[this.coeffs.intValue()];
/*  91: 94 */       for (int i = 0; i < this.coeffs.intValue(); i++) {
/*  92: 95 */         tmp[i] = this.output[i];
/*  93:    */       }
/*  94: 97 */       this.output = tmp;
/*  95:    */     }
/*  96:    */     else
/*  97:    */     {
/*  98: 99 */       this.output = new double[this.coeffs.intValue()];
/*  99:100 */       System.err.println("Not enough border points");
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static double[] invoke(Image img, Integer coeffs)
/* 104:    */   {
/* 105:    */     try
/* 106:    */     {
/* 107:107 */       return (double[])new FourierDescriptor().preprocess(new Object[] { img, coeffs });
/* 108:    */     }
/* 109:    */     catch (GlobalException e)
/* 110:    */     {
/* 111:109 */       e.printStackTrace();
/* 112:    */     }
/* 113:110 */     return null;
/* 114:    */   }
/* 115:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.FourierDescriptor
 * JD-Core Version:    0.7.0.1
 */