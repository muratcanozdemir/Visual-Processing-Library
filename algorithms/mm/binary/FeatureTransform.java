/*  1:   */ package vpt.algorithms.mm.binary;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.ByteImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.arithmetic.Subtraction;
/*  8:   */ import vpt.util.se.FlatSE;
/*  9:   */ 
/* 10:   */ public class FeatureTransform
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:19 */   public Image output = null;
/* 14:20 */   public Image input = null;
/* 15:21 */   public Integer distance = null;
/* 16:   */   public static final int CHESSBOARD = 0;
/* 17:   */   public static final int CITYBLOCK = 1;
/* 18:   */   
/* 19:   */   public FeatureTransform()
/* 20:   */   {
/* 21:27 */     this.inputFields = "input,distance";
/* 22:28 */     this.outputFields = "output";
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void execute()
/* 26:   */     throws GlobalException
/* 27:   */   {
/* 28:32 */     FlatSE se = null;
/* 29:34 */     if (this.distance.intValue() == 0) {
/* 30:34 */       se = FlatSE.square(3);
/* 31:35 */     } else if (this.distance.intValue() == 1) {
/* 32:35 */       se = FlatSE.cross(3);
/* 33:   */     } else {
/* 34:36 */       throw new GlobalException("Unsupported distance type, quitting");
/* 35:   */     }
/* 36:38 */     this.output = new ByteImage(this.input, false);
/* 37:39 */     ((ByteImage)this.output).fill(0);
/* 38:   */     
/* 39:41 */     int cdim = this.output.getCDim();
/* 40:42 */     int xdim = this.output.getXDim();
/* 41:43 */     int ydim = this.output.getYDim();
/* 42:45 */     for (int c = 0; c < cdim; c++)
/* 43:   */     {
/* 44:47 */       Image tmp = this.input.getChannel(c);
/* 45:48 */       Image tmp2 = null;
/* 46:50 */       for (int i = 1; !tmp.isFull(c); i++)
/* 47:   */       {
/* 48:51 */         tmp2 = tmp;
/* 49:   */         
/* 50:   */ 
/* 51:54 */         tmp = BDilation.invoke(tmp, se);
/* 52:   */         
/* 53:   */ 
/* 54:57 */         Image diff = Subtraction.invoke(tmp, tmp2);
/* 55:60 */         for (int x = 0; x < xdim; x++) {
/* 56:61 */           for (int y = 0; y < ydim; y++)
/* 57:   */           {
/* 58:62 */             boolean p = diff.getXYBoolean(x, y);
/* 59:64 */             if (p) {
/* 60:65 */               this.output.setXYCByte(x, y, c, i);
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
/* 72:76 */       return (Image)new FeatureTransform().preprocess(new Object[] { image, distance });
/* 73:   */     }
/* 74:   */     catch (GlobalException e)
/* 75:   */     {
/* 76:78 */       e.printStackTrace();
/* 77:   */     }
/* 78:79 */     return null;
/* 79:   */   }
/* 80:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.binary.FeatureTransform
 * JD-Core Version:    0.7.0.1
 */