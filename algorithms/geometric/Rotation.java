/*   1:    */ package vpt.algorithms.geometric;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.GlobalException;
/*   5:    */ import vpt.Image;
/*   6:    */ 
/*   7:    */ public class Rotation
/*   8:    */   extends Algorithm
/*   9:    */ {
/*  10: 14 */   public Image output = null;
/*  11: 16 */   public Image input = null;
/*  12: 17 */   public Double theta = null;
/*  13: 18 */   public Integer interpolation = null;
/*  14:    */   public static final int NEAREST_NEIGHBOR = 0;
/*  15:    */   public static final int BILINEAR = 1;
/*  16: 23 */   private int cdim = 0;
/*  17: 24 */   private int xdim = 0;
/*  18: 25 */   private int ydim = 0;
/*  19:    */   
/*  20:    */   public Rotation()
/*  21:    */   {
/*  22: 29 */     this.inputFields = "input,theta,interpolation";
/*  23: 30 */     this.outputFields = "output";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void execute()
/*  27:    */     throws GlobalException
/*  28:    */   {
/*  29: 34 */     this.cdim = this.input.getCDim();
/*  30: 35 */     this.xdim = this.input.getXDim();
/*  31: 36 */     this.ydim = this.input.getYDim();
/*  32: 38 */     if ((this.interpolation.intValue() != 1) && (this.interpolation.intValue() != 0)) {
/*  33: 39 */       throw new GlobalException("Unsupported interpolation type");
/*  34:    */     }
/*  35: 41 */     this.theta = Double.valueOf(this.theta.doubleValue() * 3.141592653589793D / 180.0D);
/*  36: 42 */     double mtheta = -1.0D * this.theta.doubleValue();
/*  37:    */     
/*  38: 44 */     double cos = Math.cos(this.theta.doubleValue());
/*  39: 45 */     double sin = Math.sin(this.theta.doubleValue());
/*  40:    */     
/*  41:    */ 
/*  42: 48 */     int newXdim = (int)Math.ceil(this.xdim * Math.abs(Math.cos(mtheta)) + this.ydim * Math.abs(Math.sin(mtheta)));
/*  43: 49 */     int newYdim = (int)Math.ceil(this.xdim * Math.abs(Math.sin(mtheta)) + this.ydim * Math.abs(Math.cos(mtheta)));
/*  44:    */     
/*  45: 51 */     int xpadding = (newXdim - this.xdim) / 2;
/*  46: 52 */     int ypadding = (newYdim - this.ydim) / 2;
/*  47:    */     
/*  48: 54 */     int xMiddle = this.xdim / 2;
/*  49: 55 */     int yMiddle = this.ydim / 2;
/*  50:    */     
/*  51: 57 */     this.output = this.input.newInstance(newXdim, newYdim, this.cdim);
/*  52: 58 */     this.output.fill(0.0D);
/*  53: 60 */     for (int c = 0; c < this.cdim; c++) {
/*  54: 61 */       for (int x = 0; x < newXdim; x++) {
/*  55: 62 */         for (int y = 0; y < newYdim; y++)
/*  56:    */         {
/*  57: 64 */           double _x = (x - xMiddle - xpadding) * cos + (y - yMiddle - ypadding) * sin + xMiddle;
/*  58: 65 */           double _y = -1 * (x - xMiddle - xpadding) * sin + (y - yMiddle - ypadding) * cos + yMiddle;
/*  59: 66 */           double p = 0.0D;
/*  60: 68 */           if (this.interpolation.intValue() == 0)
/*  61:    */           {
/*  62: 69 */             int px = (int)Math.round(_x);
/*  63: 70 */             int py = (int)Math.round(_y);
/*  64: 72 */             if ((px >= 0) && (px < this.xdim) && (py >= 0) && (py < this.ydim)) {
/*  65: 73 */               this.output.setXYCDouble(x, y, c, this.input.getXYCDouble(px, py, c));
/*  66:    */             }
/*  67:    */           }
/*  68: 75 */           else if (this.interpolation.intValue() == 1)
/*  69:    */           {
/*  70: 76 */             int l = (int)Math.floor(_x);
/*  71: 77 */             int k = (int)Math.floor(_y);
/*  72:    */             
/*  73: 79 */             double a = _x - l;
/*  74: 80 */             double b = _y - k;
/*  75: 82 */             if ((l >= 0) && (k >= 0) && (l < this.xdim) && (k < this.ydim))
/*  76:    */             {
/*  77: 84 */               double p1 = this.input.getXYCDouble(l, k, c);
/*  78: 85 */               double p2 = this.input.getXYCDouble(Math.min(l + 1, this.xdim - 1), k, c);
/*  79: 86 */               double p3 = this.input.getXYCDouble(l, Math.min(k + 1, this.ydim - 1), c);
/*  80: 87 */               double p4 = this.input.getXYCDouble(Math.min(l + 1, this.xdim - 1), Math.min(k + 1, this.ydim - 1), c);
/*  81:    */               
/*  82: 89 */               p = (1.0D - a) * (1.0D - b) * p1 + a * (1.0D - b) * p2 + (1.0D - a) * b * p3 + a * b * p4;
/*  83:    */               
/*  84: 91 */               this.output.setXYCDouble(x, y, c, p);
/*  85:    */             }
/*  86:    */           }
/*  87:    */         }
/*  88:    */       }
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   public static Image invoke(Image image, Double theta, Integer interpolation)
/*  93:    */   {
/*  94:    */     try
/*  95:    */     {
/*  96:100 */       return (Image)new Rotation().preprocess(new Object[] { image, theta, interpolation });
/*  97:    */     }
/*  98:    */     catch (GlobalException e)
/*  99:    */     {
/* 100:102 */       e.printStackTrace();
/* 101:    */     }
/* 102:103 */     return null;
/* 103:    */   }
/* 104:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.geometric.Rotation
 * JD-Core Version:    0.7.0.1
 */