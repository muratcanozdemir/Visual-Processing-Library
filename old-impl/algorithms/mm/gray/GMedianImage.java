/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.Equal;
/*  7:   */ import vpt.algorithms.arithmetic.Maximum;
/*  8:   */ import vpt.algorithms.arithmetic.Minimum;
/*  9:   */ import vpt.util.se.FlatSE;
/* 10:   */ 
/* 11:   */ public class GMedianImage
/* 12:   */   extends Algorithm
/* 13:   */ {
/* 14:18 */   public Image output = null;
/* 15:19 */   public Image input1 = null;
/* 16:20 */   public Image input2 = null;
/* 17:   */   
/* 18:   */   public GMedianImage()
/* 19:   */   {
/* 20:23 */     this.inputFields = "input1, input2";
/* 21:24 */     this.outputFields = "output";
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void execute()
/* 25:   */     throws GlobalException
/* 26:   */   {
/* 27:29 */     if (!this.input1.hasSameDimensionsAs(this.input2)) {
/* 28:30 */       throw new GlobalException("The arguments must be of the same dimensions!");
/* 29:   */     }
/* 30:32 */     Image z = Minimum.invoke(this.input1, this.input2);
/* 31:33 */     Image w = Maximum.invoke(this.input1, this.input2);
/* 32:34 */     Image m = Minimum.invoke(this.input1, this.input2);
/* 33:35 */     FlatSE se = FlatSE.square(3);
/* 34:36 */     Image tmp = m;
/* 35:   */     do
/* 36:   */     {
/* 37:39 */       tmp = m;
/* 38:40 */       z = GDilation.invoke(z, se);
/* 39:41 */       w = GErosion.invoke(w, se);
/* 40:42 */       m = Maximum.invoke(Minimum.invoke(z, w), tmp);
/* 41:44 */     } while (!Equal.invoke(tmp, m).booleanValue());
/* 42:46 */     this.output = m;
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static Image invoke(Image image, Image image2)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:51 */       return (Image)new GMedianImage().preprocess(new Object[] { image, image2 });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:53 */       e.printStackTrace();
/* 54:   */     }
/* 55:54 */     return null;
/* 56:   */   }
/* 57:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GMedianImage
 * JD-Core Version:    0.7.0.1
 */