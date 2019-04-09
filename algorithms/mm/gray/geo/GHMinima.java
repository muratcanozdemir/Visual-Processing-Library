/*  1:   */ package vpt.algorithms.mm.gray.geo;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.arithmetic.AdditionC;
/*  7:   */ 
/*  8:   */ public class GHMinima
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public Image input = null;
/* 12:17 */   public Integer h = null;
/* 13:18 */   public Image output = null;
/* 14:   */   
/* 15:   */   public GHMinima()
/* 16:   */   {
/* 17:21 */     this.inputFields = "input, h";
/* 18:22 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:27 */     if ((this.h.intValue() <= 0) || (this.h.intValue() >= 255)) {
/* 25:27 */       throw new GlobalException("What kind of an h value is that?");
/* 26:   */     }
/* 27:29 */     Image fPlusH = AdditionC.invoke(this.input, Double.valueOf(this.h.intValue() * 0.00392156862745098D));
/* 28:   */     
/* 29:31 */     this.output = FastGrayReconstruction.invoke(fPlusH, this.input, FastGrayReconstruction.CONNEXITY8, true);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public static Image invoke(Image image, Integer h)
/* 33:   */   {
/* 34:   */     try
/* 35:   */     {
/* 36:37 */       return (Image)new GHMinima().preprocess(new Object[] { image, h });
/* 37:   */     }
/* 38:   */     catch (GlobalException e)
/* 39:   */     {
/* 40:39 */       e.printStackTrace();
/* 41:   */     }
/* 42:40 */     return null;
/* 43:   */   }
/* 44:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.geo.GHMinima
 * JD-Core Version:    0.7.0.1
 */