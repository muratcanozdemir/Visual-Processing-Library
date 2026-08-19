/*   1:    */ package vpt.algorithms.shape;
/*   2:    */ 
/*   3:    */ import java.awt.Point;
/*   4:    */ import vpt.Algorithm;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ import vpt.algorithms.display.Display2D;
/*   8:    */ import vpt.algorithms.geometric.ImageCentroid;
/*   9:    */ import vpt.util.Tools;
/*  10:    */ 
/*  11:    */ public class AxisOfSymmetry
/*  12:    */   extends Algorithm
/*  13:    */ {
/*  14:    */   public Image output;
/*  15: 20 */   public Image input = null;
/*  16:    */   
/*  17:    */   public AxisOfSymmetry()
/*  18:    */   {
/*  19: 23 */     this.inputFields = "input";
/*  20: 24 */     this.outputFields = "output";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void execute()
/*  24:    */     throws GlobalException
/*  25:    */   {
/*  26: 28 */     int cdim = this.input.getCDim();
/*  27: 30 */     if (cdim != 1) {
/*  28: 30 */       throw new GlobalException("Sorry, only mono-channel images for now.");
/*  29:    */     }
/*  30: 36 */     Point centroid = ImageCentroid.invoke(this.input);
/*  31:    */     
/*  32:    */ 
/*  33:    */ 
/*  34: 40 */     double maxSymmetry = 0.0D;
/*  35: 41 */     double maxSymmetryTheta = 0.0D;
/*  36: 43 */     for (int theta = 1; theta < 179; theta += 3)
/*  37:    */     {
/*  38: 44 */       double imageDiff = 0.0D;
/*  39: 45 */       double tan = Math.tan(Tools.degree2radian(theta));
/*  40: 47 */       for (int x = 0; x < this.input.getXDim(); x++) {
/*  41: 48 */         for (int y = 0; y < this.input.getYDim(); y++)
/*  42:    */         {
/*  43: 49 */           boolean left = this.input.getXYBoolean(x, y);
/*  44: 50 */           boolean right = false;
/*  45: 52 */           if (left) {
/*  46: 54 */             if (y - centroid.y - tan * (x - centroid.x) < 0.0D)
/*  47:    */             {
/*  48: 56 */               double A = 1.0D;
/*  49: 57 */               double B = -1.0D * tan;
/*  50: 58 */               double C = -1 * centroid.y + tan * centroid.x;
/*  51: 59 */               double D = -1.0D * B * x + A * y;
/*  52:    */               
/*  53: 61 */               double symX = (A * C - B * D) / (A * A + B * B);
/*  54: 62 */               double symY = (B * C - A * D) / (A * A + B * B);
/*  55:    */               
/*  56: 64 */               symX += x - symX;
/*  57: 65 */               symY += y - symY;
/*  58: 68 */               if ((symX < this.input.getXDim()) && (symX >= 0.0D) && (symY < this.input.getYDim()) && (symY >= 0.0D))
/*  59:    */               {
/*  60: 69 */                 right = this.input.getXYBoolean((int)symX, (int)symY);
/*  61:    */                 
/*  62:    */ 
/*  63: 72 */                 imageDiff += (right ? 1 : 0);
/*  64:    */               }
/*  65:    */             }
/*  66:    */           }
/*  67:    */         }
/*  68:    */       }
/*  69: 80 */       if (imageDiff > maxSymmetry)
/*  70:    */       {
/*  71: 81 */         maxSymmetry = imageDiff;
/*  72: 82 */         maxSymmetryTheta = theta;
/*  73:    */       }
/*  74:    */     }
/*  75:105 */     double tan = Math.tan(Tools.degree2radian(maxSymmetryTheta));
/*  76:107 */     for (int x = 0; x < this.input.getXDim(); x++) {
/*  77:108 */       for (int y = 0; y < this.input.getYDim(); y++) {
/*  78:109 */         if (y - centroid.y - tan * (x - centroid.x) < 0.0D) {
/*  79:110 */           this.input.setXYBoolean(x, y, false);
/*  80:    */         }
/*  81:    */       }
/*  82:    */     }
/*  83:113 */     Display2D.invoke(this.input);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static Image invoke(Image img)
/*  87:    */   {
/*  88:    */     try
/*  89:    */     {
/*  90:119 */       return (Image)new AxisOfSymmetry().preprocess(new Object[] { img });
/*  91:    */     }
/*  92:    */     catch (GlobalException e)
/*  93:    */     {
/*  94:121 */       e.printStackTrace();
/*  95:    */     }
/*  96:122 */     return null;
/*  97:    */   }
/*  98:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.AxisOfSymmetry
 * JD-Core Version:    0.7.0.1
 */