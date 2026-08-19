/*   1:    */ package vpt.algorithms.conversion;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.ByteImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ 
/*   8:    */ public class HSY2RGB
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11:    */   public Image input;
/*  12:    */   public Image output;
/*  13:    */   
/*  14:    */   public HSY2RGB()
/*  15:    */   {
/*  16: 22 */     this.inputFields = "input";
/*  17: 23 */     this.outputFields = "output";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void execute()
/*  21:    */     throws GlobalException
/*  22:    */   {
/*  23: 27 */     int xdim = this.input.getXDim();
/*  24: 28 */     int ydim = this.input.getYDim();
/*  25: 29 */     int cdim = this.input.getCDim();
/*  26: 31 */     if (cdim != 3) {
/*  27: 32 */       throw new GlobalException("And why exactly isn't the input image in color?");
/*  28:    */     }
/*  29: 34 */     this.output = new ByteImage(this.input, false);
/*  30: 36 */     for (int x = 0; x < xdim; x++) {
/*  31: 37 */       for (int y = 0; y < ydim; y++)
/*  32:    */       {
/*  33: 38 */         double H = this.input.getXYCDouble(x, y, 0);
/*  34: 39 */         double S = this.input.getXYCDouble(x, y, 1);
/*  35: 40 */         double Y = this.input.getXYCDouble(x, y, 2);
/*  36:    */         
/*  37: 42 */         int[] rgb = convert(H, S, Y);
/*  38:    */         
/*  39: 44 */         this.output.setXYCByte(x, y, 0, rgb[0]);
/*  40: 45 */         this.output.setXYCByte(x, y, 1, rgb[1]);
/*  41: 46 */         this.output.setXYCByte(x, y, 2, rgb[2]);
/*  42:    */       }
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static int[] convert(double H, double S, double Y)
/*  47:    */   {
/*  48: 60 */     int[] rgb = new int[3];
/*  49:    */     
/*  50:    */ 
/*  51: 63 */     double R = G = B = 0.0D;
/*  52:    */     
/*  53:    */ 
/*  54: 66 */     H = H * 3.141592653589793D * 2.0D;
/*  55:    */     
/*  56:    */ 
/*  57: 69 */     int k = (int)Math.floor(H / 1.047197551196598D);
/*  58: 70 */     double Hstar = H - k * 1.047197551196598D;
/*  59: 71 */     double C = Math.sqrt(3.0D) * S / (2.0D * Math.sin(2.094395102393195D - Hstar));
/*  60:    */     
/*  61: 73 */     double C1 = C * Math.cos(H);
/*  62: 74 */     double C2 = -1.0D * C * Math.sin(H);
/*  63:    */     
/*  64: 76 */     R = 1.0D * Y + 0.701D * C1 + 0.2730866773266931D * C2;
/*  65: 77 */     double G = 1.0D * Y - 0.299D * C1 - 0.3042635918629327D * C2;
/*  66: 78 */     double B = 1.0D * Y - 0.299D * C1 + 0.8504369465163189D * C2;
/*  67:    */     
/*  68: 80 */     rgb[0] = ((int)Math.round(R * 255.0D));
/*  69: 81 */     if (rgb[0] > 255) {
/*  70: 82 */       rgb[0] = 255;
/*  71: 83 */     } else if (rgb[0] < 0) {
/*  72: 84 */       rgb[0] = 0;
/*  73:    */     }
/*  74: 86 */     rgb[1] = ((int)Math.round(G * 255.0D));
/*  75: 87 */     if (rgb[1] > 255) {
/*  76: 88 */       rgb[1] = 255;
/*  77: 89 */     } else if (rgb[1] < 0) {
/*  78: 90 */       rgb[1] = 0;
/*  79:    */     }
/*  80: 92 */     rgb[2] = ((int)Math.round(B * 255.0D));
/*  81: 93 */     if (rgb[2] > 255) {
/*  82: 94 */       rgb[2] = 255;
/*  83: 95 */     } else if (rgb[2] < 0) {
/*  84: 96 */       rgb[2] = 0;
/*  85:    */     }
/*  86: 98 */     return rgb;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static Image invoke(Image image)
/*  90:    */   {
/*  91:    */     try
/*  92:    */     {
/*  93:112 */       return (Image)new HSY2RGB().preprocess(new Object[] { image });
/*  94:    */     }
/*  95:    */     catch (GlobalException e)
/*  96:    */     {
/*  97:114 */       e.printStackTrace();
/*  98:    */     }
/*  99:115 */     return null;
/* 100:    */   }
/* 101:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.conversion.HSY2RGB
 * JD-Core Version:    0.7.0.1
 */