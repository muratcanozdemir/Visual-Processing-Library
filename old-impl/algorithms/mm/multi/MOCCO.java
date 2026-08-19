/*  1:   */ package vpt.algorithms.mm.multi;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.ordering.Ordering;
/*  7:   */ import vpt.util.se.FlatSE;
/*  8:   */ 
/*  9:   */ public class MOCCO
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:16 */   public Image output = null;
/* 13:17 */   public Image input = null;
/* 14:18 */   public FlatSE se = null;
/* 15:19 */   public Ordering or = null;
/* 16:   */   
/* 17:   */   public MOCCO()
/* 18:   */   {
/* 19:22 */     this.inputFields = "input,se,or";
/* 20:23 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:27 */     Image tmp1 = MOpening.invoke(this.input, this.se, this.or);
/* 27:28 */     tmp1 = MClosing.invoke(tmp1, this.se, this.or);
/* 28:   */     
/* 29:30 */     this.output = MClosing.invoke(this.input, this.se, this.or);
/* 30:31 */     this.output = MOpening.invoke(this.output, this.se, this.or);
/* 31:   */     
/* 32:33 */     int size = this.input.getSize();
/* 33:35 */     for (int i = 0; i < size; i++)
/* 34:   */     {
/* 35:36 */       double p1 = tmp1.getDouble(i);
/* 36:37 */       double p2 = this.output.getDouble(i);
/* 37:   */       
/* 38:39 */       this.output.setDouble(i, (p1 + p2) / 2.0D);
/* 39:   */     }
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Image invoke(Image image, FlatSE se, Ordering or)
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:46 */       return (Image)new MOCCO().preprocess(new Object[] { image, se, or });
/* 47:   */     }
/* 48:   */     catch (GlobalException e)
/* 49:   */     {
/* 50:48 */       e.printStackTrace();
/* 51:   */     }
/* 52:49 */     return null;
/* 53:   */   }
/* 54:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.multi.MOCCO
 * JD-Core Version:    0.7.0.1
 */