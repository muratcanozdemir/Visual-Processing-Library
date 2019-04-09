/*  1:   */ package vpt.algorithms.mm.gray;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.se.FlatSE;
/*  7:   */ 
/*  8:   */ public class GOCCO
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11:16 */   public Image output = null;
/* 12:17 */   public Image input = null;
/* 13:18 */   public FlatSE se = null;
/* 14:   */   
/* 15:   */   public GOCCO()
/* 16:   */   {
/* 17:21 */     this.inputFields = "input,se";
/* 18:22 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:26 */     Image tmp1 = GOpening.invoke(this.input, this.se);
/* 25:27 */     tmp1 = GClosing.invoke(tmp1, this.se);
/* 26:   */     
/* 27:29 */     this.output = GClosing.invoke(this.input, this.se);
/* 28:30 */     this.output = GOpening.invoke(this.output, this.se);
/* 29:   */     
/* 30:32 */     int size = this.input.getSize();
/* 31:34 */     for (int i = 0; i < size; i++)
/* 32:   */     {
/* 33:35 */       double p1 = tmp1.getDouble(i);
/* 34:36 */       double p2 = this.output.getDouble(i);
/* 35:   */       
/* 36:38 */       this.output.setDouble(i, (p1 + p2) / 2.0D);
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static Image invoke(Image image, FlatSE se)
/* 41:   */   {
/* 42:   */     try
/* 43:   */     {
/* 44:45 */       return (Image)new GOCCO().preprocess(new Object[] { image, se });
/* 45:   */     }
/* 46:   */     catch (GlobalException e)
/* 47:   */     {
/* 48:47 */       e.printStackTrace();
/* 49:   */     }
/* 50:48 */     return null;
/* 51:   */   }
/* 52:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.GOCCO
 * JD-Core Version:    0.7.0.1
 */