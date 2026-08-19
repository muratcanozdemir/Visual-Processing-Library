/*  1:   */ package vpt.algorithms.shape;
/*  2:   */ 
/*  3:   */ import java.awt.Point;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.geometric.Crop;
/*  8:   */ 
/*  9:   */ public class WidthFactor
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:21 */   public double[] output = null;
/* 13:22 */   public Image input = null;
/* 14:23 */   public Integer slides = null;
/* 15:   */   
/* 16:   */   public WidthFactor()
/* 17:   */   {
/* 18:26 */     this.inputFields = "input, slides";
/* 19:27 */     this.outputFields = "output";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:31 */     int cdim = this.input.getCDim();
/* 26:32 */     int ydim = this.input.getYDim();
/* 27:33 */     int xdim = this.input.getXDim();
/* 28:35 */     if (cdim != 1) {
/* 29:35 */       throw new GlobalException("Sorry, only grayscale images for now...");
/* 30:   */     }
/* 31:37 */     this.output = new double[this.slides.intValue()];
/* 32:   */     
/* 33:   */ 
/* 34:40 */     int top = ydim;
/* 35:41 */     int bottom = 0;
/* 36:   */     
/* 37:43 */     int left = xdim;
/* 38:44 */     int right = 0;
/* 39:46 */     for (int y = 0; y < ydim; y++) {
/* 40:47 */       for (int x = 0; x < xdim; x++)
/* 41:   */       {
/* 42:48 */         boolean p = this.input.getXYBoolean(x, y);
/* 43:49 */         if (p)
/* 44:   */         {
/* 45:50 */           if (y < top) {
/* 46:50 */             top = y;
/* 47:   */           }
/* 48:51 */           if (y > bottom) {
/* 49:51 */             bottom = y;
/* 50:   */           }
/* 51:53 */           if (x < left) {
/* 52:53 */             left = x;
/* 53:   */           }
/* 54:54 */           if (x > right) {
/* 55:54 */             right = x;
/* 56:   */           }
/* 57:   */         }
/* 58:   */       }
/* 59:   */     }
/* 60:59 */     int maxWidth = right - left;
/* 61:   */     
/* 62:   */ 
/* 63:62 */     int diff = bottom - top;
/* 64:63 */     int slideWidth = diff / this.slides.intValue();
/* 65:65 */     for (int i = 0; i < this.slides.intValue(); i++)
/* 66:   */     {
/* 67:66 */       Image tmp = Crop.invoke(this.input, new Point(0, top), new Point(xdim - 1, top + slideWidth));
/* 68:67 */       top += slideWidth;
/* 69:   */       
/* 70:   */ 
/* 71:70 */       int _xdim = tmp.getXDim();
/* 72:71 */       int _ydim = tmp.getYDim();
/* 73:   */       
/* 74:73 */       int _left = _xdim;
/* 75:74 */       int _right = 0;
/* 76:76 */       for (int y = 0; y < _ydim; y++) {
/* 77:77 */         for (int x = 0; x < _xdim; x++)
/* 78:   */         {
/* 79:78 */           boolean p = tmp.getXYBoolean(x, y);
/* 80:79 */           if (p)
/* 81:   */           {
/* 82:80 */             if (x < _left) {
/* 83:80 */               _left = x;
/* 84:   */             }
/* 85:81 */             if (x > _right) {
/* 86:81 */               _right = x;
/* 87:   */             }
/* 88:   */           }
/* 89:   */         }
/* 90:   */       }
/* 91:86 */       this.output[i] = ((_right - _left) / maxWidth);
/* 92:   */     }
/* 93:   */   }
/* 94:   */   
/* 95:   */   public static double[] invoke(Image img, Integer slides)
/* 96:   */   {
/* 97:   */     try
/* 98:   */     {
/* 99:93 */       return (double[])new WidthFactor().preprocess(new Object[] { img, slides });
/* :0:   */     }
/* :1:   */     catch (GlobalException e)
/* :2:   */     {
/* :3:95 */       e.printStackTrace();
/* :4:   */     }
/* :5:96 */     return null;
/* :6:   */   }
/* :7:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.shape.WidthFactor
 * JD-Core Version:    0.7.0.1
 */