/*  1:   */ package vpt.algorithms.mm.gray.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.algorithms.point.Inversion;
/*  7:   */ 
/*  8:   */ public class GAreaClosing
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:   */   public Image input;
/* 12:   */   public Integer area;
/* 13:   */   public Image output;
/* 14:   */   
/* 15:   */   public GAreaClosing()
/* 16:   */   {
/* 17:22 */     this.inputFields = "input,area";
/* 18:23 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:28 */     this.output = Inversion.invoke(GAreaOpening.invoke(Inversion.invoke(this.input), this.area));
/* 25:   */   }
/* 26:   */   
/* 27:   */   public static Image invoke(Image input, Integer area)
/* 28:   */   {
/* 29:   */     try
/* 30:   */     {
/* 31:39 */       return (Image)new GAreaClosing().preprocess(new Object[] { input, area });
/* 32:   */     }
/* 33:   */     catch (GlobalException e)
/* 34:   */     {
/* 35:41 */       e.printStackTrace();
/* 36:   */     }
/* 37:42 */     return null;
/* 38:   */   }
/* 39:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GAreaClosing
 * JD-Core Version:    0.7.0.1
 */