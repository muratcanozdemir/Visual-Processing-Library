/*   1:    */ package vpt.algorithms.conversion;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ 
/*   8:    */ public class LAB2XYZ
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11: 16 */   public Image input = null;
/*  12: 17 */   public Image output = null;
/*  13:    */   
/*  14:    */   public LAB2XYZ()
/*  15:    */   {
/*  16: 20 */     this.inputFields = "input";
/*  17: 21 */     this.outputFields = "output";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void execute()
/*  21:    */     throws GlobalException
/*  22:    */   {
/*  23: 26 */     this.output = new DoubleImage(this.input, false);
/*  24:    */     
/*  25: 28 */     int xdim = this.input.getXDim();
/*  26: 29 */     int ydim = this.input.getYDim();
/*  27: 30 */     int cdim = this.input.getCDim();
/*  28: 32 */     if (cdim != 3) {
/*  29: 33 */       throw new GlobalException("The input must be a tristumulus LAB image");
/*  30:    */     }
/*  31: 35 */     for (int x = 0; x < xdim; x++) {
/*  32: 36 */       for (int y = 0; y < ydim; y++)
/*  33:    */       {
/*  34: 37 */         double L = this.input.getXYCDouble(x, y, 0);
/*  35: 38 */         double A = this.input.getXYCDouble(x, y, 1);
/*  36: 39 */         double B = this.input.getXYCDouble(x, y, 2);
/*  37:    */         
/*  38: 41 */         double[] xyz = convert(L, A, B);
/*  39:    */         
/*  40: 43 */         this.output.setXYCDouble(x, y, 0, xyz[0]);
/*  41: 44 */         this.output.setXYCDouble(x, y, 1, xyz[1]);
/*  42: 45 */         this.output.setXYCDouble(x, y, 2, xyz[2]);
/*  43:    */       }
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static double[] convert(double L, double A, double B)
/*  48:    */   {
/*  49: 60 */     double Xn = 0.950456D;
/*  50: 61 */     double Yn = 1.0D;
/*  51: 62 */     double Zn = 1.088754D;
/*  52:    */     
/*  53: 64 */     double delta = 0.2068965517241379D;
/*  54:    */     
/*  55: 66 */     double fy = (L + 16.0D) / 116.0D;
/*  56: 67 */     double fx = fy + A / 500.0D;
/*  57: 68 */     double fz = fy - B / 200.0D;
/*  58:    */     
/*  59: 70 */     double[] xyz = new double[3];
/*  60: 72 */     if (fy > delta) {
/*  61: 73 */       xyz[1] = (Yn * Math.pow(fy, 3.0D));
/*  62:    */     } else {
/*  63: 75 */       xyz[1] = ((fy - 0.1379310344827586D) * 3.0D * delta * delta * Yn);
/*  64:    */     }
/*  65: 77 */     if (fx > delta) {
/*  66: 78 */       xyz[0] = (Xn * Math.pow(fx, 3.0D));
/*  67:    */     } else {
/*  68: 80 */       xyz[0] = ((fx - 0.1379310344827586D) * 3.0D * delta * delta * Xn);
/*  69:    */     }
/*  70: 82 */     if (fz > delta) {
/*  71: 83 */       xyz[2] = (Zn * Math.pow(fz, 3.0D));
/*  72:    */     } else {
/*  73: 85 */       xyz[2] = ((fz - 0.1379310344827586D) * 3.0D * delta * delta * Zn);
/*  74:    */     }
/*  75: 87 */     return xyz;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static Image invoke(Image image)
/*  79:    */   {
/*  80:    */     try
/*  81:    */     {
/*  82:100 */       return (Image)new LAB2XYZ().preprocess(new Object[] { image });
/*  83:    */     }
/*  84:    */     catch (GlobalException e)
/*  85:    */     {
/*  86:102 */       e.printStackTrace();
/*  87:    */     }
/*  88:103 */     return null;
/*  89:    */   }
/*  90:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.LAB2XYZ
 * JD-Core Version:    0.7.0.1
 */