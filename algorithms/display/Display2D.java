/*  1:   */ package vpt.algorithms.display;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import vpt.Algorithm;
/*  5:   */ import vpt.GlobalException;
/*  6:   */ import vpt.Image;
/*  7:   */ import vpt.gui.Frame2D;
/*  8:   */ 
/*  9:   */ public class Display2D
/* 10:   */   extends Algorithm
/* 11:   */ {
/* 12:   */   public Image img;
/* 13:   */   public String title;
/* 14:   */   public Boolean showInColor;
/* 15:   */   
/* 16:   */   public Display2D()
/* 17:   */   {
/* 18:22 */     this.inputFields = "img,title,showInColor";
/* 19:23 */     this.outputFields = "";
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void execute()
/* 23:   */     throws GlobalException
/* 24:   */   {
/* 25:28 */     if ((this.img.getCDim() != 3) && (this.showInColor.booleanValue()))
/* 26:   */     {
/* 27:29 */       System.err.println("Warning : color visualisation disabled, incompatible channel number");
/* 28:30 */       this.showInColor = Boolean.valueOf(false);
/* 29:   */     }
/* 30:33 */     new Frame2D(this.img, this.title, this.showInColor);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public static void invoke(Image img, String title, Boolean showInColor)
/* 34:   */   {
/* 35:   */     try
/* 36:   */     {
/* 37:38 */       new Display2D().preprocess(new Object[] { img, title, showInColor });
/* 38:   */     }
/* 39:   */     catch (GlobalException e)
/* 40:   */     {
/* 41:40 */       e.printStackTrace();
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   public static void invoke(Image img, String title)
/* 46:   */   {
/* 47:   */     try
/* 48:   */     {
/* 49:46 */       new Display2D().preprocess(new Object[] { img, title, Boolean.valueOf(false) });
/* 50:   */     }
/* 51:   */     catch (GlobalException e)
/* 52:   */     {
/* 53:48 */       e.printStackTrace();
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:   */   public static void invoke(Image img, Boolean showInColor)
/* 58:   */   {
/* 59:   */     try
/* 60:   */     {
/* 61:54 */       new Display2D().preprocess(new Object[] { img, "", showInColor });
/* 62:   */     }
/* 63:   */     catch (GlobalException e)
/* 64:   */     {
/* 65:56 */       e.printStackTrace();
/* 66:   */     }
/* 67:   */   }
/* 68:   */   
/* 69:   */   public static void invoke(Image img)
/* 70:   */   {
/* 71:   */     try
/* 72:   */     {
/* 73:63 */       new Display2D().preprocess(new Object[] { img, "", Boolean.valueOf(false) });
/* 74:   */     }
/* 75:   */     catch (GlobalException e)
/* 76:   */     {
/* 77:65 */       e.printStackTrace();
/* 78:   */     }
/* 79:   */   }
/* 80:   */ }


/* Location:           H:\ROI_v0.1.jar
 * Qualified Name:     vpt.algorithms.display.Display2D
 * JD-Core Version:    0.7.0.1
 */