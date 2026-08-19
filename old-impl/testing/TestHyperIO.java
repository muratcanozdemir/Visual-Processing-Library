/*   1:    */ package vpt.testing;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import vpt.ByteImage;
/*   5:    */ import vpt.Image;
/*   6:    */ import vpt.algorithms.display.Display2D;
/*   7:    */ import vpt.algorithms.io.Load;
/*   8:    */ 
/*   9:    */ public class TestHyperIO
/*  10:    */ {
/*  11:    */   public static void main(String[] args)
/*  12:    */   {
/*  13: 52 */     String path = "samples/hyperspectral/paviaU_gt.png";
/*  14:    */     
/*  15: 54 */     Image tmp = Load.invoke(path);
/*  16: 55 */     Display2D.invoke(tmp);
/*  17:    */     
/*  18: 57 */     Image color = new ByteImage(tmp.getXDim(), tmp.getYDim(), 3);
/*  19:    */     
/*  20: 59 */     color.setChannel(tmp, 0);
/*  21: 60 */     color.setChannel(tmp, 1);
/*  22: 61 */     color.setChannel(tmp, 2);
/*  23:    */     
/*  24: 63 */     int[] counter = new int[9];
/*  25: 65 */     for (int y = 0; y < color.getYDim(); y++) {
/*  26: 66 */       for (int x = 0; x < color.getXDim(); x++)
/*  27:    */       {
/*  28: 68 */         int p = color.getXYCByte(x, y, 0);
/*  29: 70 */         switch (p)
/*  30:    */         {
/*  31:    */         case 1: 
/*  32: 72 */           color.setXYCByte(x, y, 0, 255);
/*  33: 73 */           color.setXYCByte(x, y, 1, 0);
/*  34: 74 */           color.setXYCByte(x, y, 2, 0);
/*  35: 75 */           counter[0] += 1;
/*  36: 76 */           break;
/*  37:    */         case 2: 
/*  38: 78 */           color.setXYCByte(x, y, 0, 255);
/*  39: 79 */           color.setXYCByte(x, y, 1, 255);
/*  40: 80 */           color.setXYCByte(x, y, 2, 0);
/*  41: 81 */           counter[1] += 1;
/*  42: 82 */           break;
/*  43:    */         case 3: 
/*  44: 84 */           color.setXYCByte(x, y, 0, 255);
/*  45: 85 */           color.setXYCByte(x, y, 1, 0);
/*  46: 86 */           color.setXYCByte(x, y, 2, 255);
/*  47: 87 */           counter[2] += 1;
/*  48: 88 */           break;
/*  49:    */         case 4: 
/*  50: 90 */           color.setXYCByte(x, y, 0, 0);
/*  51: 91 */           color.setXYCByte(x, y, 1, 0);
/*  52: 92 */           color.setXYCByte(x, y, 2, 255);
/*  53: 93 */           counter[3] += 1;
/*  54: 94 */           break;
/*  55:    */         case 5: 
/*  56: 96 */           color.setXYCByte(x, y, 0, 0);
/*  57: 97 */           color.setXYCByte(x, y, 1, 255);
/*  58: 98 */           color.setXYCByte(x, y, 2, 0);
/*  59: 99 */           counter[4] += 1;
/*  60:100 */           break;
/*  61:    */         case 6: 
/*  62:102 */           color.setXYCByte(x, y, 0, 0);
/*  63:103 */           color.setXYCByte(x, y, 1, 255);
/*  64:104 */           color.setXYCByte(x, y, 2, 255);
/*  65:105 */           counter[5] += 1;
/*  66:106 */           break;
/*  67:    */         case 7: 
/*  68:108 */           color.setXYCByte(x, y, 0, 255);
/*  69:109 */           color.setXYCByte(x, y, 1, 255);
/*  70:110 */           color.setXYCByte(x, y, 2, 255);
/*  71:111 */           counter[6] += 1;
/*  72:112 */           break;
/*  73:    */         case 8: 
/*  74:114 */           color.setXYCByte(x, y, 0, 255);
/*  75:115 */           color.setXYCByte(x, y, 1, 128);
/*  76:116 */           color.setXYCByte(x, y, 2, 128);
/*  77:117 */           counter[7] += 1;
/*  78:118 */           break;
/*  79:    */         case 9: 
/*  80:120 */           color.setXYCByte(x, y, 0, 128);
/*  81:121 */           color.setXYCByte(x, y, 1, 0);
/*  82:122 */           color.setXYCByte(x, y, 2, 128);
/*  83:123 */           counter[8] += 1;
/*  84:124 */           break;
/*  85:    */         default: 
/*  86:126 */           color.setXYCByte(x, y, 0, 0);
/*  87:127 */           color.setXYCByte(x, y, 1, 0);
/*  88:128 */           color.setXYCByte(x, y, 2, 0);
/*  89:    */         }
/*  90:    */       }
/*  91:    */     }
/*  92:133 */     Display2D.invoke(color, Boolean.valueOf(true));
/*  93:135 */     for (int i = 0; i < 9; i++) {
/*  94:136 */       System.err.println(counter[i]);
/*  95:    */     }
/*  96:    */   }
/*  97:    */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.TestHyperIO
 * JD-Core Version:    0.7.0.1
 */