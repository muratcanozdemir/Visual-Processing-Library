/*  1:   */ package vpt.algorithms.mm.gray.floating;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.se.FloatingFlatSE;
/*  7:   */ 
/*  8:   */ public class GFloatingClosing
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:15 */   public Image output = null;
/* 12:16 */   public Image input = null;
/* 13:17 */   public FloatingFlatSE se = null;
/* 14:   */   
/* 15:   */   public GFloatingClosing()
/* 16:   */   {
/* 17:20 */     this.inputFields = "input,se";
/* 18:21 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:25 */     this.output = GFloatingDilation.invoke(this.input, this.se);
/* 25:26 */     this.output = GFloatingErosion.invoke(this.output, this.se);
/* 26:   */   }
/* 27:   */   
/* 28:   */   public static Image invoke(Image image, FloatingFlatSE se)
/* 29:   */   {
/* 30:   */     try
/* 31:   */     {
/* 32:32 */       return (Image)new GFloatingClosing().preprocess(new Object[] { image, se });
/* 33:   */     }
/* 34:   */     catch (GlobalException e)
/* 35:   */     {
/* 36:34 */       e.printStackTrace();
/* 37:   */     }
/* 38:35 */     return null;
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.floating.GFloatingClosing
 * JD-Core Version:    0.7.0.1
 */