/*   1:    */ package vpt.algorithms.color;
/*   2:    */ 
/*   3:    */ import vpt.Algorithm;
/*   4:    */ import vpt.DoubleImage;
/*   5:    */ import vpt.GlobalException;
/*   6:    */ import vpt.Image;
/*   7:    */ 
/*   8:    */ public class ColorMoments
/*   9:    */   extends Algorithm
/*  10:    */ {
/*  11:    */   public Image input;
/*  12:    */   public double[] output;
/*  13:    */   private int xdim;
/*  14:    */   private int ydim;
/*  15:    */   
/*  16:    */   public ColorMoments()
/*  17:    */   {
/*  18: 25 */     this.inputFields = "input";
/*  19: 26 */     this.outputFields = "output";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void execute()
/*  23:    */     throws GlobalException
/*  24:    */   {
/*  25: 31 */     int cdim = this.input.getCDim();
/*  26: 32 */     this.xdim = this.input.getXDim();
/*  27: 33 */     this.ydim = this.input.getYDim();
/*  28: 35 */     if (cdim != 3) {
/*  29: 35 */       throw new GlobalException("Why is the input not in color ?");
/*  30:    */     }
/*  31: 38 */     Image tmp = new DoubleImage(this.input, true);
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45: 52 */     this.output = new double[27];
/*  46:    */     
/*  47: 54 */     double avg = moment(0, 0, tmp, 0, 0, 0);
/*  48:    */     
/*  49: 56 */     this.output[0] = (moment(0, 0, tmp, 1, 0, 0) / avg);
/*  50: 57 */     this.output[1] = (moment(0, 0, tmp, 0, 1, 0) / avg);
/*  51: 58 */     this.output[2] = (moment(0, 0, tmp, 0, 0, 1) / avg);
/*  52: 59 */     this.output[3] = (moment(0, 0, tmp, 2, 0, 0) / avg);
/*  53: 60 */     this.output[4] = (moment(0, 0, tmp, 0, 2, 0) / avg);
/*  54: 61 */     this.output[5] = (moment(0, 0, tmp, 0, 0, 2) / avg);
/*  55: 62 */     this.output[6] = (moment(0, 0, tmp, 1, 1, 0) / avg);
/*  56: 63 */     this.output[7] = (moment(0, 0, tmp, 1, 0, 1) / avg);
/*  57: 64 */     this.output[8] = (moment(0, 0, tmp, 0, 1, 1) / avg);
/*  58:    */     
/*  59: 66 */     this.output[9] = (moment(0, 1, tmp, 1, 0, 0) / avg);
/*  60: 67 */     this.output[10] = (moment(0, 1, tmp, 0, 1, 0) / avg);
/*  61: 68 */     this.output[11] = (moment(0, 1, tmp, 0, 0, 1) / avg);
/*  62: 69 */     this.output[12] = (moment(0, 1, tmp, 2, 0, 0) / avg);
/*  63: 70 */     this.output[13] = (moment(0, 1, tmp, 0, 2, 0) / avg);
/*  64: 71 */     this.output[14] = (moment(0, 1, tmp, 0, 0, 2) / avg);
/*  65: 72 */     this.output[15] = (moment(0, 1, tmp, 1, 1, 0) / avg);
/*  66: 73 */     this.output[16] = (moment(0, 1, tmp, 1, 0, 1) / avg);
/*  67: 74 */     this.output[17] = (moment(0, 1, tmp, 0, 1, 1) / avg);
/*  68:    */     
/*  69: 76 */     this.output[18] = (moment(1, 0, tmp, 1, 0, 0) / avg);
/*  70: 77 */     this.output[19] = (moment(1, 0, tmp, 0, 1, 0) / avg);
/*  71: 78 */     this.output[20] = (moment(1, 0, tmp, 0, 0, 1) / avg);
/*  72: 79 */     this.output[21] = (moment(1, 0, tmp, 2, 0, 0) / avg);
/*  73: 80 */     this.output[22] = (moment(1, 0, tmp, 0, 2, 0) / avg);
/*  74: 81 */     this.output[23] = (moment(1, 0, tmp, 0, 0, 2) / avg);
/*  75: 82 */     this.output[24] = (moment(1, 0, tmp, 1, 1, 0) / avg);
/*  76: 83 */     this.output[25] = (moment(1, 0, tmp, 1, 0, 1) / avg);
/*  77: 84 */     this.output[26] = (moment(1, 0, tmp, 0, 1, 1) / avg);
/*  78:    */   }
/*  79:    */   
/*  80:    */   private double moment(int p, int q, Image tmp, int r, int g, int b)
/*  81:    */   {
/*  82: 88 */     double moment = 0.0D;
/*  83: 90 */     for (int x = 0; x < this.xdim; x++) {
/*  84: 91 */       for (int y = 0; y < this.ydim; y++)
/*  85:    */       {
/*  86: 92 */         double red = tmp.getXYCDouble(x, y, 0);
/*  87: 93 */         double green = tmp.getXYCDouble(x, y, 1);
/*  88: 94 */         double blue = tmp.getXYCDouble(x, y, 2);
/*  89:    */         
/*  90: 96 */         moment += Math.pow(x + 1, p) * Math.pow(y + 1, q) * Math.pow(red, r) * Math.pow(green, g) * Math.pow(blue, b);
/*  91:    */       }
/*  92:    */     }
/*  93:100 */     return moment;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static double[] invoke(Image image)
/*  97:    */   {
/*  98:    */     try
/*  99:    */     {
/* 100:105 */       return (double[])new ColorMoments().preprocess(new Object[] { image });
/* 101:    */     }
/* 102:    */     catch (GlobalException e)
/* 103:    */     {
/* 104:107 */       e.printStackTrace();
/* 105:    */     }
/* 106:108 */     return null;
/* 107:    */   }
/* 108:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.color.ColorMoments
 * JD-Core Version:    0.7.0.1
 */