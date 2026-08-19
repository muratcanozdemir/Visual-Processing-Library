/*  1:   */ package vpt.algorithms.mm.gray.nonflat;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.GlobalException;
/*  5:   */ import vpt.Image;
/*  6:   */ import vpt.util.se.NonFlatSE;
/*  7:   */ 
/*  8:   */ public class GNFClosing
/*  9:   */   extends Algorithm
/* 10:   */ {
/* 11: 9 */   public Image output = null;
/* 12:10 */   public Image input = null;
/* 13:11 */   public NonFlatSE se = null;
/* 14:   */   
/* 15:   */   public GNFClosing()
/* 16:   */   {
/* 17:14 */     this.inputFields = "input,se";
/* 18:15 */     this.outputFields = "output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void execute()
/* 22:   */     throws GlobalException
/* 23:   */   {
/* 24:20 */     this.output = GNFDilation.invoke(this.input, this.se);
/* 25:21 */     this.output = GNFErosion.invoke(this.output, this.se);
/* 26:   */   }
/* 27:   */   
/* 28:   */   public static Image invoke(Image image, NonFlatSE se)
/* 29:   */   {
/* 30:   */     try
/* 31:   */     {
/* 32:27 */       return (Image)new GNFClosing().preprocess(new Object[] { image, se });
/* 33:   */     }
/* 34:   */     catch (GlobalException e)
/* 35:   */     {
/* 36:29 */       e.printStackTrace();
/* 37:   */     }
/* 38:30 */     return null;
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.nonflat.GNFClosing
 * JD-Core Version:    0.7.0.1
 */