/*  1:   */ package vpt.util.se;
/*  2:   */ 
/*  3:   */ import java.awt.geom.Point2D.Double;
/*  4:   */ 
/*  5:   */ public class FloatingFlatSE
/*  6:   */ {
/*  7:   */   private Point2D.Double center;
/*  8:   */   private Point2D.Double[] coords;
/*  9:   */   
/* 10:   */   public static FloatingFlatSE circle(int radius, int perimeter)
/* 11:   */   {
/* 12:23 */     FloatingFlatSE se = new FloatingFlatSE();
/* 13:   */     
/* 14:25 */     se.center = new Point2D.Double(0.0D, 0.0D);
/* 15:26 */     se.coords = new Point2D.Double[perimeter];
/* 16:28 */     for (int i = 0; i < perimeter; i++)
/* 17:   */     {
/* 18:30 */       double x = -1 * radius * Math.sin(6.283185307179586D * i / perimeter);
/* 19:33 */       if (Math.abs(Math.round(x) - x) <= 1.0E-008D) {
/* 20:33 */         x = Math.round(x);
/* 21:   */       }
/* 22:36 */       double y = radius * Math.cos(6.283185307179586D * i / perimeter);
/* 23:39 */       if (Math.abs(Math.round(y) - y) <= 1.0E-008D) {
/* 24:39 */         y = Math.round(y);
/* 25:   */       }
/* 26:41 */       se.coords[i] = new Point2D.Double(x, y);
/* 27:   */     }
/* 28:46 */     return se;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public FloatingFlatSE reflection()
/* 32:   */   {
/* 33:51 */     return this;
/* 34:   */   }
/* 35:   */   
/* 36:   */   public Point2D.Double[] getCoords()
/* 37:   */   {
/* 38:56 */     return this.coords;
/* 39:   */   }
/* 40:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.util.se.FloatingFlatSE
 * JD-Core Version:    0.7.0.1
 */