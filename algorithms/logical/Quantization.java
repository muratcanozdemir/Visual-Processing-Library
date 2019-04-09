/*  1:   */ package vpt.algorithms.logical;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ 
/*  7:   */ public class Quantization
/*  8:   */   extends Algorithm
/*  9:   */ {
/* 10:   */   public Image input;
/* 11:   */   public Integer bit;
/* 12:   */   public Image output;
/* 13:   */   
/* 14:   */   public Quantization()
/* 15:   */   {
/* 16:19 */     this.inputFields = "input,bit";
/* 17:20 */     this.outputFields = "output";
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void execute()
/* 21:   */     throws GlobalException
/* 22:   */   {
/* 23:25 */     int size = this.input.getSize();
/* 24:   */     
/* 25:27 */     int[] mask = { -128, -64, -32, -16, 
/* 26:28 */       -8, -4, -2, -1 };
/* 27:   */     
/* 28:30 */     this.output = this.input.newInstance(false);
/* 29:32 */     if ((this.bit.intValue() < 1) || (this.bit.intValue() > 8)) {
/* 30:32 */       throw new GlobalException("The bit number must be in [1, 8]");
/* 31:   */     }
/* 32:34 */     for (int i = 0; i < size; i++)
/* 33:   */     {
/* 34:35 */       int v = this.input.getByte(i);
/* 35:   */       
/* 36:37 */       v &= mask[(this.bit.intValue() - 1)];
/* 37:   */       
/* 38:39 */       this.output.setByte(i, v);
/* 39:   */     }
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Image invoke(Image img, Integer bit)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:45 */       return (Image)new Quantization().preprocess(new Object[] { img, bit });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:47 */       e.printStackTrace();
/* 51:   */     }
/* 52:48 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.logical.Quantization
 * JD-Core Version:    0.7.0.1
 */