/*  1:   */ package vpt.algorithms.frequential;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.DoubleImage;
/*  6:   */ import vpt.GlobalException;
/*  7:   */ import vpt.Image;
/*  8:   */ import vpt.util.Tools;
/*  9:   */ 
/* 10:   */ public class DFT2D
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:   */   public Image input;
/* 14:   */   public Boolean shifted;
/* 15:   */   public DoubleImage[] output;
/* 16:   */   
/* 17:   */   public DFT2D()
/* 18:   */   {
/* 19:22 */     this.inputFields = "input, shifted";
/* 20:23 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:27 */     int xdim = this.input.getXDim();
/* 27:28 */     int ydim = this.input.getYDim();
/* 28:29 */     int cdim = this.input.getCDim();
/* 29:31 */     if (cdim > 1) {
/* 30:31 */       throw new GlobalException("Mono-channel images only!");
/* 31:   */     }
/* 32:33 */     this.output = new DoubleImage[3];
/* 33:   */     
/* 34:   */ 
/* 35:36 */     this.output[0] = new DoubleImage(this.input, false);
/* 36:   */     
/* 37:   */ 
/* 38:39 */     this.output[1] = new DoubleImage(this.input, false);
/* 39:   */     
/* 40:   */ 
/* 41:42 */     this.output[2] = new DoubleImage(this.input, false);
/* 42:44 */     for (int x = 0; x < xdim; x++)
/* 43:   */     {
/* 44:45 */       for (int y = 0; y < ydim; y++)
/* 45:   */       {
/* 46:46 */         double real = 0.0D;
/* 47:47 */         double imag = 0.0D;
/* 48:49 */         for (int _x = 0; _x < xdim; _x++) {
/* 49:50 */           for (int _y = 0; _y < ydim; _y++)
/* 50:   */           {
/* 51:51 */             double tmp = (_x * x / xdim + _y * y / ydim) * -6.283185307179586D;
/* 52:52 */             real += this.input.getXYDouble(_x, _y) * Math.cos(tmp);
/* 53:53 */             imag += this.input.getXYDouble(_x, _y) * Math.sin(tmp);
/* 54:   */           }
/* 55:   */         }
/* 56:57 */         real /= Math.sqrt(xdim * ydim);
/* 57:58 */         imag /= Math.sqrt(xdim * ydim);
/* 58:   */         
/* 59:60 */         this.output[0].setXYDouble(x, y, real);
/* 60:61 */         this.output[1].setXYDouble(x, y, imag);
/* 61:62 */         this.output[2].setXYDouble(x, y, Math.sqrt(real * real + imag * imag));
/* 62:   */       }
/* 63:64 */       System.err.println(x);
/* 64:   */     }
/* 65:67 */     if (this.shifted.booleanValue()) {
/* 66:68 */       this.output[2] = ((DoubleImage)Tools.shiftOrigin(this.output[2]));
/* 67:   */     }
/* 68:   */   }
/* 69:   */   
/* 70:   */   public static DoubleImage[] invoke(Image image, Boolean shifted)
/* 71:   */   {
/* 72:   */     try
/* 73:   */     {
/* 74:74 */       return (DoubleImage[])new DFT2D().preprocess(new Object[] { image, shifted });
/* 75:   */     }
/* 76:   */     catch (GlobalException e)
/* 77:   */     {
/* 78:76 */       e.printStackTrace();
/* 79:   */     }
/* 80:77 */     return null;
/* 81:   */   }
/* 82:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.frequential.DFT2D
 * JD-Core Version:    0.7.0.1
 */