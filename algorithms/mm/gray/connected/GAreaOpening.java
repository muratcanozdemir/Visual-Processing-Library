/*  1:   */ package vpt.algorithms.mm.gray.connected;
/*  2:   */ 
/*  3:   */ import vpt.Algorithm;
/*  4:   */ import vpt.BooleanImage;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.algorithms.mm.binary.attribute.BAreaOpening;
/*  8:   */ import vpt.algorithms.segmentation.Threshold;
/*  9:   */ 
/* 10:   */ public class GAreaOpening
/* 11:   */   extends Algorithm
/* 12:   */ {
/* 13:   */   public Image input;
/* 14:   */   public Integer area;
/* 15:   */   public Image output;
/* 16:   */   
/* 17:   */   public GAreaOpening()
/* 18:   */   {
/* 19:25 */     this.inputFields = "input, area";
/* 20:26 */     this.outputFields = "output";
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void execute()
/* 24:   */     throws GlobalException
/* 25:   */   {
/* 26:31 */     this.output = this.input.newInstance(false);
/* 27:33 */     for (int t = 0; t < 256; t++)
/* 28:   */     {
/* 29:35 */       BooleanImage stack = Threshold.invoke(this.input, 0.00392156862745098D * t);
/* 30:   */       
/* 31:37 */       stack = (BooleanImage)BAreaOpening.invoke(stack, this.area);
/* 32:39 */       for (int p = 0; p < stack.size(); p++) {
/* 33:40 */         if (stack.getBoolean(p)) {
/* 34:41 */           this.output.setByte(p, t);
/* 35:   */         }
/* 36:   */       }
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   public static Image invoke(Image input, Integer area)
/* 41:   */   {
/* 42:   */     try
/* 43:   */     {
/* 44:48 */       return (Image)new GAreaOpening().preprocess(new Object[] { input, area });
/* 45:   */     }
/* 46:   */     catch (GlobalException e)
/* 47:   */     {
/* 48:50 */       e.printStackTrace();
/* 49:   */     }
/* 50:51 */     return null;
/* 51:   */   }
/* 52:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.mm.gray.connected.GAreaOpening
 * JD-Core Version:    0.7.0.1
 */