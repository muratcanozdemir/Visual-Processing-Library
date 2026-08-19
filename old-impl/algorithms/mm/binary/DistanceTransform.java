/*  1:   */ package vpt.algorithms.mm.binary;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.arithmetic.Subtraction;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class DistanceTransform
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:22 */   public Image output = null;
/* 14:23 */   public Image input = null;
/* 15:24 */   public Integer distance = null;
/* 16:   */   public static final int CHESSBOARD = 0;
/* 17:   */   public static final int CITYBLOCK = 1;
/* 18:   */   
/* 19:   */   public DistanceTransform()
/* 20:   */   {
/* 21:30 */     this.inputFields = "input,distance";
/* 22:31 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:35 */     FlatSE se = null;
/* 29:37 */     if (this.distance.intValue() == 0) {
/* 30:37 */       se = FlatSE.square(3);
/* 31:38 */     } else if (this.distance.intValue() == 1) {
/* 32:38 */       se = FlatSE.cross(3);
/* 33:   */     } else {
/* 34:39 */       throw new GlobalException("Unsupported distance type, quitting");
/* 35:   */     }
/* 36:41 */     this.output = new ByteImage(this.input, false);
/* 37:42 */     ((ByteImage)this.output).fill(0);
/* 38:   */     
/* 39:44 */     int cdim = this.output.getCDim();
/* 40:45 */     int xdim = this.output.getXDim();
/* 41:46 */     int ydim = this.output.getYDim();
/* 42:48 */     for (int c = 0; c < cdim; c++)
/* 43:   */     {
/* 44:50 */       Image tmp = this.input.getChannel(c);
/* 45:51 */       Image tmp2 = null;
/* 46:53 */       for (int i = 1; !tmp.isEmpty(c); i++)
/* 47:   */       {
/* 48:54 */         tmp2 = tmp;
/* 49:   */         
/* 50:   */ 
/* 51:57 */         tmp = BErosion.invoke(tmp, se);
/* 52:   */         
/* 53:   */ 
/* 54:60 */         Image diff = Subtraction.invoke(tmp2, tmp);
/* 55:63 */         for (int x = 0; x < xdim; x++) {
/* 56:64 */           for (int y = 0; y < ydim; y++)
/* 57:   */           {
/* 58:65 */             boolean p = diff.getXYBoolean(x, y);
/* 59:67 */             if (p) {
/* 60:68 */               this.output.setXYCByte(x, y, c, i);
/* 61:   */             }
/* 62:   */           }
/* 63:   */         }
/* 64:   */       }
/* 65:   */     }
/* 66:   */   }
/* 67:   */   
/* 68:   */   public static Image invoke(Image image, Integer distance)
/* 69:   */   {
/* 70:   */     try
/* 71:   */     {
/* 72:79 */       return (Image)new DistanceTransform().preprocess(new Object[] { image, distance });
/* 73:   */     }
/* 74:   */     catch (GlobalException e)
/* 75:   */     {
/* 76:81 */       e.printStackTrace();
/* 77:   */     }
/* 78:82 */     return null;
/* 79:   */   }
/* 80:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.DistanceTransform
 * JD-Core Version:    0.7.0.1
 */