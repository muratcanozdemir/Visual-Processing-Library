/*   1:    */ package vpt.algorithms.conversion;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ 
/*   8:    */ public class RGB2HSY
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11:    */   public Image input;
/*  12:    */   public Image output;
/*  13:    */   
/*  14:    */   public RGB2HSY()
/*  15:    */   {
/*  16: 25 */     this.inputFields = "input";
/*  17: 26 */     this.outputFields = "output";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void execute()
/*  21:    */     throws GlobalException
/*  22:    */   {
/*  23: 31 */     int xdim = this.input.getXDim();
/*  24: 32 */     int ydim = this.input.getYDim();
/*  25: 33 */     int cdim = this.input.getCDim();
/*  26: 35 */     if (cdim != 3) {
/*  27: 36 */       throw new GlobalException("And why exactly isn't the input image in color?");
/*  28:    */     }
/*  29: 38 */     this.output = new DoubleImage(this.input, false);
/*  30: 40 */     for (int x = 0; x < xdim; x++) {
/*  31: 41 */       for (int y = 0; y < ydim; y++)
/*  32:    */       {
/*  33: 42 */         double r = this.input.getXYCDouble(x, y, 0);
/*  34: 43 */         double g = this.input.getXYCDouble(x, y, 1);
/*  35: 44 */         double b = this.input.getXYCDouble(x, y, 2);
/*  36:    */         
/*  37: 46 */         double[] hsy = convert(r, g, b);
/*  38:    */         
/*  39: 48 */         this.output.setXYCDouble(x, y, 0, hsy[0]);
/*  40: 49 */         this.output.setXYCDouble(x, y, 1, hsy[1]);
/*  41: 50 */         this.output.setXYCDouble(x, y, 2, hsy[2]);
/*  42:    */       }
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static double[] convert(double r, double g, double b)
/*  47:    */   {
/*  48: 64 */     double[] hsy = new double[3]; double 
/*  49:    */     
/*  50: 66 */       tmp17_16 = (hsy[2] = 0.0D);hsy[1] = tmp17_16;hsy[0] = tmp17_16;
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54: 70 */     hsy[2] = (0.299D * r + 0.587D * g + 0.114D * b);
/*  55:    */     
/*  56:    */ 
/*  57: 73 */     double C1 = r - 0.5D * g - 0.5D * b;
/*  58: 74 */     double C2 = -Math.sqrt(3.0D) / 2.0D * g + Math.sqrt(3.0D) / 2.0D * b;
/*  59:    */     
/*  60:    */ 
/*  61: 77 */     double C = Math.sqrt(C1 * C1 + C2 * C2);
/*  62: 80 */     if ((C <= 0.0D) && (C >= 0.0D)) {
/*  63: 81 */       hsy[0] = 0.0D;
/*  64: 82 */     } else if ((C != 0.0D) && (C2 <= 0.0D)) {
/*  65: 83 */       hsy[0] = Math.acos(C1 / C);
/*  66: 84 */     } else if ((C != 0.0D) && (C2 > 0.0D)) {
/*  67: 85 */       hsy[0] = (6.283185307179586D - Math.acos(C1 / C));
/*  68:    */     }
/*  69: 88 */     hsy[1] = (Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b)));
/*  70:    */     
/*  71:    */ 
/*  72: 91 */     hsy[0] /= 6.283185307179586D;
/*  73:    */     
/*  74: 93 */     return hsy;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static Image invoke(Image image)
/*  78:    */   {
/*  79:    */     try
/*  80:    */     {
/*  81:106 */       return (Image)new RGB2HSY().preprocess(new Object[] { image });
/*  82:    */     }
/*  83:    */     catch (GlobalException e)
/*  84:    */     {
/*  85:108 */       e.printStackTrace();
/*  86:    */     }
/*  87:109 */     return null;
/*  88:    */   }
/*  89:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.RGB2HSY
 * JD-Core Version:    0.7.0.1
 */