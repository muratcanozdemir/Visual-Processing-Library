/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class GInterpolation
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:16 */   public Image[] output = null;
/* 11:17 */   public Image input1 = null;
/* 12:18 */   public Image input2 = null;
/* 13:19 */   public Integer length = null;
/* 14:   */   
/* 15:   */   public GInterpolation()
/* 16:   */   {
/* 17:22 */     this.inputFields = "input1, input2, length";
/* 18:23 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     if (!this.input1.hasSameDimensionsAs(this.input2)) {
/* 25:29 */       throw new GlobalException("The arguments must be of the same dimensions!");
/* 26:   */     }
/* 27:31 */     if (this.length.intValue() < 0) {
/* 28:32 */       throw new GlobalException("Invalid sequence length!");
/* 29:   */     }
/* 30:34 */     int len = 2 * (int)Math.pow(2.0D, this.length.intValue()) + 1;
/* 31:   */     
/* 32:36 */     this.output = new Image[len];
/* 33:   */     
/* 34:38 */     this.output[0] = this.input1;
/* 35:39 */     this.output[(len - 1)] = this.input2;
/* 36:   */     
/* 37:41 */     fillIntermediateFrames(this.output, 0, len - 1);
/* 38:   */   }
/* 39:   */   
/* 40:   */   void fillIntermediateFrames(Image[] output, int index1, int index2)
/* 41:   */   {
/* 42:45 */     int index = index1 + (index2 - index1) / 2;
/* 43:47 */     if (output[index] != null) {
/* 44:47 */       return;
/* 45:   */     }
/* 46:49 */     Image tmp = GMedianImage.invoke(output[index1], output[index2]);
/* 47:50 */     output[index] = tmp;
/* 48:   */     
/* 49:52 */     fillIntermediateFrames(output, index1, index);
/* 50:53 */     fillIntermediateFrames(output, index, index2);
/* 51:   */   }
/* 52:   */   
/* 53:   */   public static Image[] invoke(Image image, Image image2, Integer length)
/* 54:   */   {
/* 55:   */     try
/* 56:   */     {
/* 57:58 */       return (Image[])new GInterpolation().preprocess(new Object[] { image, image2, length });
/* 58:   */     }
/* 59:   */     catch (GlobalException e)
/* 60:   */     {
/* 61:60 */       e.printStackTrace();
/* 62:   */     }
/* 63:61 */     return null;
/* 64:   */   }
/* 65:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GInterpolation
 * JD-Core Version:    0.7.0.1
 */