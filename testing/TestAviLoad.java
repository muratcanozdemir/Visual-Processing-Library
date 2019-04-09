/*  1:   */ package vpt.testing;
/*  2:   */ 
/*  3:   */ import vpt.Image;
/*  4:   */ import vpt.algorithms.display.Display2D;
/*  5:   */ import vpt.algorithms.io.LoadAVI;
/*  6:   */ 
/*  7:   */ public class TestAviLoad
/*  8:   */ {
/*  9:   */   public static void main(String[] args)
/* 10:   */   {
/* 11:27 */     Image[] video = LoadAVI.invoke("samples/assortedcats.mpeg");
/* 12:29 */     for (int i = 0; i < video.length; i++) {
/* 13:30 */       Display2D.invoke(video[i], Boolean.valueOf(true));
/* 14:   */     }
/* 15:   */   }
/* 16:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.testing.TestAviLoad
 * JD-Core Version:    0.7.0.1
 */